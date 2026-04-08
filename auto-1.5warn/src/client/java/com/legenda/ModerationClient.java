package com.legenda;

import com.legenda.moderation.ModerationCommands;
import com.legenda.moderation.ModerationConfigManager;
import com.legenda.moderation.ModerationState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModerationClient implements ClientModInitializer {
    public static final String MOD_VERSION = "1.0.0";

    @Override
    public void onInitializeClient() {
        ModerationConfigManager.load();
        ModerationKeybinds.register();
        ModerationCommands.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModerationKeybinds.handleClientTick();
            ModerationState.tick();
        });
    }
}