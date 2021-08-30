package com.butukay.freecam.mixin;

import com.butukay.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.entity.Entity;

@Mixin(value = WorldRenderer.class, priority = 1001)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(" +
                    "Lnet/minecraft/client/render/Camera;" +
                    "Lnet/minecraft/client/render/Frustum;ZIZ)V"))
    private void preSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Freecam.setSpectator(true);
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(" +
                    "Lnet/minecraft/client/render/Camera;" +
                    "Lnet/minecraft/client/render/Frustum;ZIZ)V"))
    private void postSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci) {
        Freecam.setSpectator(false);
    }

    @Redirect(method = "render", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera) {
        if (Freecam.isEnabled()) {
            return MinecraftClient.getInstance().player;
        }

        return camera.getFocusedEntity();
    }

    @Redirect(method = "setupTerrain", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/BuiltChunkStorage;updateCameraPosition(DD)V"))
    private void preventRenderChunkPositionUpdates(net.minecraft.client.render.BuiltChunkStorage storage, double viewEntityX, double viewEntityZ) {
        if (!Freecam.isEnabled()) {
            storage.updateCameraPosition(viewEntityX, viewEntityZ);
        }
    }
}
