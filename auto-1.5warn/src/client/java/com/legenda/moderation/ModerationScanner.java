package com.legenda.moderation;

import com.legenda.ModerationConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class ModerationScanner {
    private ModerationScanner() {}

    public static List<String> scanCurrentTab() {
        List<String> found = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.getNetworkHandler() == null) {
            return found;
        }

        Collection<PlayerListEntry> players = client.getNetworkHandler().getPlayerList();
        if (players == null || players.isEmpty()) {
            return found;
        }

        for (PlayerListEntry entry : players) {
            if (entry == null || entry.getProfile() == null) continue;

            String nick = entry.getProfile().getName();
            if (nick == null || nick.isBlank()) continue;

            if (matchesBadWords(nick)) {
                found.add(nick);
            }
        }

        return found;
    }

    public static boolean matchesBadWords(String nick) {
        if (nick == null || nick.isBlank()) return false;
        if (ModerationConfig.badWords == null || ModerationConfig.badWords.isEmpty()) return false;

        String lowerNick = nick.toLowerCase(Locale.ROOT);

        for (String badWord : ModerationConfig.badWords) {
            if (badWord == null || badWord.isBlank()) continue;

            if (lowerNick.contains(badWord.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        return false;
    }
}