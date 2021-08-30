package com.butukay.freecam.mixin;

import com.butukay.freecam.CameraEntity;
import com.butukay.freecam.Freecam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    private Entity cameraEntityOriginal;
//    private float realYaw;
//    private float realPitch;

    @Inject(method = "renderWorld", at = @At(
            value = "INVOKE", shift = Shift.AFTER,
            target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Entity camera = CameraEntity.getCamera();

            if (camera != null) {
                this.cameraEntityOriginal = this.client.getCameraEntity();
                this.client.setCameraEntity(camera);
            }
        }
//        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld()) {
//            Entity entity = this.client.getCameraEntity();
//
//            if (entity != null) {
//                this.realYaw = entity.yaw;
//                this.realPitch = entity.pitch;
//                MiscUtils.setEntityRotations(entity, MiscUtils.getCameraYaw(), MiscUtils.getCameraPitch());
//            }
//        }
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci) {
        if (Freecam.isEnabled() && this.cameraEntityOriginal != null) {
            this.client.setCameraEntity(this.cameraEntityOriginal);
            this.cameraEntityOriginal = null;
        }
//        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld()) {
//            Entity entity = this.client.getCameraEntity();
//
//            if (entity != null) {
//                MiscUtils.setEntityRotations(entity, this.realYaw, this.realPitch);
//            }
//        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void removeHandRendering(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
