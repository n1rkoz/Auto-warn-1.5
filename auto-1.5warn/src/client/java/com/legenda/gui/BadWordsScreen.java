package com.legenda.gui;

import com.legenda.ModerationConfig;
import com.legenda.moderation.ModerationConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BadWordsScreen extends Screen {
    private final Screen parent;
    private HMTextField inputField;
    private HMButton addButton;
    private HMButton removeButton;
    private HMButton clearButton;
    private HMButton closeButton;

    public BadWordsScreen(Screen parent) {
        super(Text.translatable("text.moderation.badwords_title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = (width - 420) / 2;
        int y = (height - 320) / 2;

        inputField = new HMTextField(textRenderer, x + 14, y + 40, 260, 20, "");

        addButton = new HMButton(x + 282, y + 39, 54, 22, Text.translatable("text.moderation.badwords_add"), this::addWord);
        removeButton = new HMButton(x + 340, y + 39, 66, 22, Text.translatable("text.moderation.badwords_remove"), this::removeWord);
        clearButton = new HMButton(x + 14, y + 280, 90, 22, Text.translatable("text.moderation.badwords_clear"), this::clearWords);
        closeButton = new HMButton(x + 316, y + 280, 90, 22, Text.translatable("text.moderation.badwords_back"), () -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) client.setScreen(parent);
        });
    }

    private void addWord() {
        String word = inputField.getText() == null ? "" : inputField.getText().trim();
        if (word.isBlank()) return;

        boolean exists = ModerationConfig.badWords.stream()
                .anyMatch(s -> s != null && s.equalsIgnoreCase(word));

        if (!exists) {
            ModerationConfig.badWords.add(word);
            ModerationConfigManager.save();
        }

        inputField.setText("");
    }

    private void removeWord() {
        String word = inputField.getText() == null ? "" : inputField.getText().trim();
        if (word.isBlank()) return;

        ModerationConfig.badWords.removeIf(s -> s != null && s.equalsIgnoreCase(word));
        ModerationConfigManager.save();
    }

    private void clearWords() {
        ModerationConfig.badWords.clear();
        ModerationConfigManager.save();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = (width - 420) / 2;
        int y = (height - 320) / 2;

        ctx.fill(0, 0, width, height, 0xAA000000);
        ctx.fill(x, y, x + 420, y + 320, 0xFF171717);

        ctx.drawText(textRenderer, Text.translatable("text.moderation.badwords_title"), x + 14, y + 14, 0xFFFFFF, false);
        ctx.drawText(textRenderer, Text.translatable("text.moderation.badwords_words", String.valueOf(ModerationConfig.badWords.size())), x + 14, y + 24, 0xAAAAAA, false);

        inputField.render(ctx, mouseX, mouseY, delta);
        addButton.render(ctx, mouseX, mouseY, delta);
        removeButton.render(ctx, mouseX, mouseY, delta);
        clearButton.render(ctx, mouseX, mouseY, delta);
        closeButton.render(ctx, mouseX, mouseY, delta);

        int listY = y + 80;
        for (int i = 0; i < ModerationConfig.badWords.size(); i++) {
            String word = ModerationConfig.badWords.get(i);
            int rowY = listY + i * 16;
            if (rowY > y + 270) break;

            ctx.fill(x + 14, rowY, x + 406, rowY + 14, 0xFF1D1D1D);
            ctx.drawText(textRenderer, Text.literal(word), x + 20, rowY + 3, 0xFFFFFF, false);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inputField.mouseClicked(mouseX, mouseY, button)) return true;
        if (addButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (removeButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (clearButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (closeButton.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return inputField.mouseDragged(mouseX, mouseY, button) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        inputField.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) client.setScreen(parent);
            return true;
        }
        if (inputField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (inputField.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}