package com.legenda.gui;

import com.legenda.ModerationKeybinds;
import com.legenda.moderation.ModerationScanner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModerationScanScreen extends Screen {
    private final Screen parent;
    private final List<String> foundNicks = new ArrayList<>();
    private String lastCopiedNick = "";

    private HMButton rescanButton;
    private HMButton closeButton;

    private static final int W = 420;
    private static final int H = 320;

    public ModerationScanScreen(Screen parent) {
        super(Text.translatable("text.moderation.scan_title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = (width - W) / 2;
        int y = (height - H) / 2;

        rescanButton = new HMButton(x + 14, y + 14, 120, 22, Text.translatable("text.moderation.rescan"), this::rescan);
        closeButton = new HMButton(x + W - 104, y + 14, 90, 22, Text.translatable("text.moderation.close"), () -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) client.setScreen(parent);
        });

        rescan();
    }

    private void rescan() {
        foundNicks.clear();
        foundNicks.addAll(ModerationScanner.scanCurrentTab());
    }

    private String getSelectedNick() {
        if (lastCopiedNick != null && !lastCopiedNick.isBlank()) return lastCopiedNick;
        if (!foundNicks.isEmpty()) return foundNicks.get(0);
        return "";
    }

    private void copyNick(String nick) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(nick);
            lastCopiedNick = nick;
            ModerationKeybinds.setLastCopiedNick(nick);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = (width - W) / 2;
        int y = (height - H) / 2;

        ctx.fill(0, 0, width, height, 0xAA000000);
        ctx.fill(x, y, x + W, y + H, 0xFF171717);

        ctx.drawText(textRenderer, Text.translatable("text.moderation.found_count", String.valueOf(foundNicks.size())), x + 14, y + 44, 0xFFFFFF, false);

        rescanButton.render(ctx, mouseX, mouseY, delta);
        closeButton.render(ctx, mouseX, mouseY, delta);

        int startY = y + 74;
        int rowH = 22;
        int btnW = 74;
        int btnX = x + W - 14 - btnW;

        for (int i = 0; i < foundNicks.size(); i++) {
            String nick = foundNicks.get(i);
            int rowY = startY + i * (rowH + 6);

            if (rowY > y + H - 34) break;

            boolean hover = mouseX >= x + 14 && mouseX < x + W - 14 && mouseY >= rowY && mouseY < rowY + rowH;
            int bg = hover ? 0xFF262626 : 0xFF1D1D1D;

            ctx.fill(x + 14, rowY, x + W - 14, rowY + rowH, bg);
            ctx.drawText(textRenderer, Text.literal(nick), x + 22, rowY + 7, 0xFFFFFF, false);

            ctx.fill(btnX, rowY + 2, btnX + btnW, rowY + rowH - 2, 0xFF4A4A4A);
            ctx.drawText(textRenderer, Text.translatable("text.moderation.copy"), btnX + 10, rowY + 7, 0xFFFFFF, false);
        }

        if (!lastCopiedNick.isBlank()) {
            ctx.drawText(textRenderer, Text.translatable("text.moderation.last_copied", lastCopiedNick), x + 14, y + H - 18, 0xAAAAAA, false);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rescanButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (closeButton.mouseClicked(mouseX, mouseY, button)) return true;

        int x = (width - W) / 2;
        int y = (height - H) / 2;
        int startY = y + 74;
        int rowH = 22;
        int btnW = 74;
        int btnX = x + W - 14 - btnW;

        if (button == 0) {
            for (int i = 0; i < foundNicks.size(); i++) {
                String nick = foundNicks.get(i);
                int rowY = startY + i * (rowH + 6);

                if (mouseX >= btnX && mouseX < btnX + btnW && mouseY >= rowY + 2 && mouseY < rowY + rowH - 2) {
                    copyNick(nick);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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