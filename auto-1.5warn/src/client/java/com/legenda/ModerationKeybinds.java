package com.legenda;

import com.legenda.gui.ModerationScanScreen;
import com.legenda.moderation.ModerationState;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class ModerationKeybinds {
    public static KeyBinding OPEN_SCAN_GUI;
    public static KeyBinding SEND_HISTORY;
    public static KeyBinding WARN_PLAYER;

    private static String lastCopiedNick = "";

    private ModerationKeybinds() {}

    public static void register() {
        OPEN_SCAN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moderation.open_scan_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.moderation"
        ));

        SEND_HISTORY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moderation.send_history",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.moderation"
        ));

        WARN_PLAYER = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moderation.warn_player",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.moderation"
        ));
    }

    public static void handleClientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        while (OPEN_SCAN_GUI != null && OPEN_SCAN_GUI.wasPressed()) {
            client.setScreen(new ModerationScanScreen(client.currentScreen));
        }

        while (SEND_HISTORY != null && SEND_HISTORY.wasPressed()) {
            if (lastCopiedNick == null || lastCopiedNick.isBlank()) return;

            ModerationState.startHistoryCheck(lastCopiedNick);

            if (client.player != null) {
                client.player.networkHandler.sendChatCommand("history " + lastCopiedNick);
            }
        }

        while (WARN_PLAYER != null && WARN_PLAYER.wasPressed()) {
            if (lastCopiedNick == null || lastCopiedNick.isBlank()) return;

            ModerationState.startWarnFlow(
                    lastCopiedNick,
                    "1.5 Ваш никнейм нарушает пункт правил 1.5. Через 2 часа аккаунт будет заблокирован навсегда без возможности разбана, весь прогресс будет безвозвратно утерян."
            );

            if (client.player != null) {
                client.player.networkHandler.sendChatCommand("history " + lastCopiedNick);
            }
        }
    }

    public static void setLastCopiedNick(String nick) {
        lastCopiedNick = nick == null ? "" : nick.trim();
    }

    public static String getLastCopiedNick() {
        return lastCopiedNick;
    }
}