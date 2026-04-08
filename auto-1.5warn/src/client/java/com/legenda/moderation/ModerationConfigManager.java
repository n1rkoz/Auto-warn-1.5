package com.legenda.moderation;

import com.legenda.ModerationConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModerationConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("moderation.json");

    private ModerationConfigManager() {}

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            ModerationConfig.badWords = new ArrayList<>();
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            Data data = GSON.fromJson(json, Data.class);

            ModerationConfig.badWords = new ArrayList<>();
            if (data != null && data.badWords != null) {
                ModerationConfig.badWords.addAll(data.badWords);
            }
        } catch (Exception e) {
            ModerationConfig.badWords = new ArrayList<>();
        }
    }

    public static void save() {
        try {
            Data data = new Data();
            data.badWords = ModerationConfig.badWords == null ? new ArrayList<>() : new ArrayList<>(ModerationConfig.badWords);

            String json = GSON.toJson(data);
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, json, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static class Data {
        List<String> badWords = new ArrayList<>();
    }
}