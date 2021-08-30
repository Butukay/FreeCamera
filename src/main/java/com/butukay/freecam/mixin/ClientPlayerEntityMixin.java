package com.butukay.freecam.mixin;

import com.butukay.freecam.CameraEntity;
import com.butukay.freecam.Freecam;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    protected abstract boolean isCamera();

    @Shadow
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = {@At("HEAD")}, method = {"sendChatMessage"}, cancellable = true)
    public void onChatMessage(final String message, final CallbackInfo ci) {
        if (Freecam.getConfig().isEnableCommand()) {
            if (message.equals(Freecam.getConfig().getCommandName())) {
                Freecam.toggleFreecam(client);
                ci.cancel();
            }
        }
    }

    @Redirect(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    private boolean preventVerticalMotion(ClientPlayerEntity player) {
        if (Freecam.isEnabled()) {
            return false;
        }

        return this.isCamera();
    }

    @Redirect(method = "tickMovement", require = 0, at = @At(
            value = "FIELD", ordinal = 1,
            target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
    private boolean preventFlyStateToggle(PlayerAbilities abilities) {
        if (Freecam.isEnabled()) {
            return false;
        }

        return abilities.allowFlying;
    }

    @Inject(method = "tickNewAi", at = @At("RETURN"))
    private void preventJumpingInCameraMode(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            this.jumping = false;
        }
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void preventSneakingInCameraMode(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = client.player;

        CameraEntity.movementTick(player.input.sneaking, player.input.jumping);
    }

    @Override
    public boolean isSpectator() {
        return super.isSpectator() || Freecam.isSpectator();
    }
}
