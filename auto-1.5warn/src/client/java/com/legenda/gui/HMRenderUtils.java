package com.legenda.gui;

import net.minecraft.client.gui.DrawContext;

public final class HMRenderUtils {
    private HMRenderUtils() {}

    public static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    public static int interpolateColor(int startColor, int endColor, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));

        int a1 = (startColor >> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void drawOutline(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void drawGlowOutline(DrawContext ctx, int x, int y, int w, int h, int color, int glow) {
        drawOutline(ctx, x, y, w, h, color);
    }
}