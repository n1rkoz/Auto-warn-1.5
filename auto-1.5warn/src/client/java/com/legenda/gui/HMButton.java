package com.legenda.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HMButton {
    private final int x, y, w, h;
    private final Text text;
    private final Runnable action;

    public HMButton(int x, int y, int w, int h, Text text, Runnable action) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.action = action;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hover = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        int bg = hover ? HMRenderUtils.withAlpha(0x7A5CFF, 70) : HMRenderUtils.withAlpha(0xFFFFFF, 14);

        HMRenderUtils.drawRoundedRect(ctx, x, y, w, h, 5, bg);

        int tw = MinecraftClient.getInstance().textRenderer.getWidth(text);
        ctx.drawText(MinecraftClient.getInstance().textRenderer, text, x + (w - tw) / 2, y + 6, 0xFFFFFF, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
            action.run();
            return true;
        }
        return false;
    }
}