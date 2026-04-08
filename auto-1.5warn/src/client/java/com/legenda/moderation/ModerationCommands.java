package com.legenda.moderation;

import com.legenda.ModerationConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public final class ModerationCommands {
    private ModerationCommands() {}

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("badwords")
                    .then(ClientCommandManager.literal("list").executes(context -> {
                        if (ModerationConfig.badWords.isEmpty()) {
                            context.getSource().sendFeedback(Text.literal("Черный список пуст"));
                        } else {
                            context.getSource().sendFeedback(Text.literal("Черный список: " + String.join(", ", ModerationConfig.badWords)));
                        }
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("clear").executes(context -> {
                        ModerationConfig.badWords.clear();
                        ModerationConfigManager.save();
                        context.getSource().sendFeedback(Text.literal("Черный список очищен"));
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("add")
                            .then(ClientCommandManager.argument("word", StringArgumentType.word())
                                    .executes(context -> {
                                        String word = StringArgumentType.getString(context, "word").trim();
                                        if (word.isBlank()) {
                                            context.getSource().sendFeedback(Text.literal("Пустое слово"));
                                            return 0;
                                        }

                                        boolean exists = ModerationConfig.badWords.stream()
                                                .anyMatch(s -> s != null && s.equalsIgnoreCase(word));

                                        if (!exists) {
                                            ModerationConfig.badWords.add(word);
                                            ModerationConfigManager.save();
                                            context.getSource().sendFeedback(Text.literal("Добавлено: " + word));
                                        } else {
                                            context.getSource().sendFeedback(Text.literal("Уже есть: " + word));
                                        }

                                        return 1;
                                    })))
                    .then(ClientCommandManager.literal("remove")
                            .then(ClientCommandManager.argument("word", StringArgumentType.word())
                                    .executes(context -> {
                                        String word = StringArgumentType.getString(context, "word").trim();
                                        if (word.isBlank()) {
                                            context.getSource().sendFeedback(Text.literal("Пустое слово"));
                                            return 0;
                                        }

                                        boolean removed = ModerationConfig.badWords.removeIf(s -> s != null && s.equalsIgnoreCase(word));
                                        if (removed) {
                                            ModerationConfigManager.save();
                                            context.getSource().sendFeedback(Text.literal("Удалено: " + word));
                                        } else {
                                            context.getSource().sendFeedback(Text.literal("Не найдено: " + word));
                                        }

                                        return 1;
                                    }))));
        });
    }
}