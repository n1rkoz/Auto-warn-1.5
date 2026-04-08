package com.legenda.moderation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class ModerationState {
    private static boolean historyCheckActive = false;
    private static long historyCheckStartedAt = 0L;
    private static String historyNick = "";
    private static boolean historyFound = false;
    private static boolean historyReported = false;

    private static boolean warnPending = false;
    private static String warnNick = "";
    private static String warnReason = "";
    private static boolean warnAllowed = false;
    private static boolean warnReported = false;

    private ModerationState() {}

    public static void startHistoryCheck(String nick) {
        historyNick = nick == null ? "" : nick.trim();
        if (historyNick.isBlank()) return;

        historyCheckActive = true;
        historyCheckStartedAt = System.currentTimeMillis();
        historyFound = false;
        historyReported = false;
    }

    public static void startWarnFlow(String nick, String reason) {
        warnNick = nick == null ? "" : nick.trim();
        warnReason = reason == null ? "" : reason.trim();
        if (warnNick.isBlank()) return;

        warnPending = true;
        warnAllowed = false;
        warnReported = false;

        startHistoryCheck(warnNick);
    }

    public static void onChatMessage(String message) {
        if (message == null || message.isBlank()) return;

        if (historyCheckActive && message.contains("1.5")) {
            historyFound = true;
        }
    }

    public static void tick() {
        long now = System.currentTimeMillis();

        if (historyCheckActive && now - historyCheckStartedAt >= 3000L) {
            historyCheckActive = false;

            if (!historyReported) {
                historyReported = true;

                if (historyFound) {
                    showLocal("Найден активный варн");
                    warnAllowed = false;
                } else {
                    showLocal("Игрок чист");
                    warnAllowed = true;
                }
            }
        }

        if (warnPending && !historyCheckActive && !warnReported) {
            warnReported = true;

            if (historyFound) {
                showLocal("Найден активный варн, выдать варн нельзя!");
                warnPending = false;
                return;
            }

            if (warnAllowed) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.player != null) {
                    client.player.networkHandler.sendChatCommand(
                            "warn " + warnNick + " " + warnReason
                    );
                }
            }

            warnPending = false;
        }
    }

    private static void showLocal(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(Text.literal(text));
        }
    }
}