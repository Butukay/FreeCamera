package com.butukay.freecam;

import com.butukay.freecam.config.FreecamConfig;
import com.butukay.freecam.config.FreecamConfigUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class Freecam implements ModInitializer {

    private static boolean enabled = false;
    private static boolean spectator;

    @Override
    public void onInitialize() {

        FreecamConfigUtil.loadConfig();

        KeyBinding binding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.freecam.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.butukay-tweaks"));

        ClientTickCallback.EVENT.register(client -> {
            while (binding1.wasPressed()) {
                toggleFreecam(client);
            }
        });
    }

    public static void toggleFreecam(MinecraftClient client) {
        enabled = !enabled;

        if (enabled) {
            CameraEntity.createCamera(MinecraftClient.getInstance());
        } else {
            CameraEntity.removeCamera();
        }

        client.player.sendMessage(new TranslatableText("message.freecam." + (enabled ? "enabled" : "disabled")), getConfig().isActionBar());
    }

    public static FreecamConfig getConfig() {
        return FreecamConfigUtil.getConfig();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isSpectator() {
        return spectator;
    }

    public static void setSpectator(boolean spectator) {
        Freecam.spectator = spectator;
    }
}
