package com.legenda.gui;

import net.minecraft.client.gui.DrawContext;

public class HMToggle {
    private final int x, y, w, h;
    private boolean enabled;
    private float anim;

    public HMToggle(int x, int y, int w, int h, boolean enabled) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.enabled = enabled;
        this.anim = enabled ? 1f : 0f;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        anim += ((enabled ? 1f : 0f) - anim) * 0.2f;

        int track = HMRenderUtils.interpolateColor(
                HMRenderUtils.withAlpha(0x2A2D3A, 255),
                HMRenderUtils.withAlpha(0x7A5CFF, 255),
                anim
        );

        HMRenderUtils.drawRoundedRect(ctx, x, y, w, h, h / 2, track);

        int knobSize = h - 4;
        int knobX = x + 2 + (int)((w - knobSize - 4) * anim);
        int knobY = y + 2;

        HMRenderUtils.drawRoundedRect(ctx, knobX, knobY, knobSize, knobSize, knobSize / 2, 0xFFFFFFFF);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
            enabled = !enabled;
            return true;
        }
        return false;
    }
}