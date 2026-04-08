package com.legenda.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class HMTextField {
    private final TextRenderer textRenderer;
    private final int x, y, w, h;
    private String text;

    private boolean focused;
    private int cursor;
    private int selectionStart;
    private int selectionEnd;
    private boolean dragging;

    public HMTextField(TextRenderer textRenderer, int x, int y, int w, int h, String initialText) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = initialText == null ? "" : initialText;
        this.cursor = this.text.length();
        this.selectionStart = this.cursor;
        this.selectionEnd = this.cursor;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value == null ? "" : value;
        this.cursor = this.text.length();
        this.selectionStart = this.cursor;
        this.selectionEnd = this.cursor;
        this.dragging = false;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int bg = focused ? HMRenderUtils.withAlpha(0x161826, 255) : HMRenderUtils.withAlpha(0x11131C, 255);
        int border = focused ? HMRenderUtils.withAlpha(0x7A5CFF, 180) : HMRenderUtils.withAlpha(0x2A2D3A, 180);

        HMRenderUtils.drawRoundedRect(ctx, x, y, w, h, 4, bg);
        HMRenderUtils.drawOutline(ctx, x, y, w, h, border);

        int textX = x + 5;
        int textY = y + 5;

        String shown = text;
        while (textRenderer.getWidth(shown) > w - 10 && shown.length() > 0) {
            shown = shown.substring(1);
        }

        int visibleOffset = text.length() - shown.length();
        int visibleCursor = Math.max(0, cursor - visibleOffset);
        int visibleSelStart = Math.max(0, selectionStart - visibleOffset);
        int visibleSelEnd = Math.max(0, selectionEnd - visibleOffset);

        if (focused && hasSelection()) {
            int s = Math.min(visibleSelStart, visibleSelEnd);
            int e = Math.max(visibleSelStart, visibleSelEnd);

            if (e > 0) {
                int selX1 = textX + textRenderer.getWidth(shown.substring(0, Math.min(s, shown.length())));
                int selX2 = textX + textRenderer.getWidth(shown.substring(0, Math.min(e, shown.length())));
                ctx.fill(selX1, y + 4, selX2, y + h - 4, HMRenderUtils.withAlpha(0x7A5CFF, 90));
            }
        }

        ctx.drawText(textRenderer, shown, textX, textY, 0xFFFFFF, false);

        if (focused) {
            int cursorPos = Math.min(visibleCursor, shown.length());
            int cx = textX + textRenderer.getWidth(shown.substring(0, cursorPos));
            ctx.fill(cx, y + 4, cx + 1, y + h - 4, 0xFFFFFFFF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = button == 0 && mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;

        if (inside) {
            focused = true;
            setCursorByMouse(mouseX);
            selectionStart = cursor;
            selectionEnd = cursor;
            dragging = true;
            return true;
        }

        dragging = false;
        focused = false;
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!focused || !dragging || button != 0) return false;
        setCursorByMouse(mouseX);
        selectionEnd = cursor;
        return true;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;

        boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        if (ctrl && keyCode == GLFW.GLFW_KEY_A) {
            selectionStart = 0;
            selectionEnd = text.length();
            cursor = text.length();
            return true;
        }

        if (ctrl && keyCode == GLFW.GLFW_KEY_C) {
            if (hasSelection()) {
                String selected = getSelectedText();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) client.keyboard.setClipboard(selected);
            }
            return true;
        }

        if (ctrl && keyCode == GLFW.GLFW_KEY_X) {
            if (hasSelection()) {
                String selected = getSelectedText();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) client.keyboard.setClipboard(selected);
                replaceSelection("");
            }
            return true;
        }

        if (ctrl && keyCode == GLFW.GLFW_KEY_V) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                String clip = client.keyboard.getClipboard();
                if (clip != null) replaceSelection(clip);
            }
            return true;
        }

        if (shift && keyCode == GLFW.GLFW_KEY_INSERT) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                String clip = client.keyboard.getClipboard();
                if (clip != null) replaceSelection(clip);
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (hasSelection()) {
                replaceSelection("");
            } else if (cursor > 0) {
                text = text.substring(0, cursor - 1) + text.substring(cursor);
                cursor--;
                selectionStart = cursor;
                selectionEnd = cursor;
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (hasSelection()) {
                replaceSelection("");
            } else if (cursor < text.length()) {
                text = text.substring(0, cursor) + text.substring(cursor + 1);
                selectionStart = cursor;
                selectionEnd = cursor;
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if (ctrl) cursor = moveCursorWordLeft(cursor);
            else if (cursor > 0) cursor--;

            if (!shift) {
                selectionStart = cursor;
                selectionEnd = cursor;
            } else {
                selectionEnd = cursor;
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (ctrl) cursor = moveCursorWordRight(cursor);
            else if (cursor < text.length()) cursor++;

            if (!shift) {
                selectionStart = cursor;
                selectionEnd = cursor;
            } else {
                selectionEnd = cursor;
            }
            return true;
        }

        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        if (Character.isISOControl(chr)) return false;

        replaceSelection(String.valueOf(chr));
        return true;
    }

    private void replaceSelection(String insert) {
        if (!hasSelection()) {
            text = text.substring(0, cursor) + insert + text.substring(cursor);
            cursor += insert.length();
        } else {
            int a = Math.min(selectionStart, selectionEnd);
            int b = Math.max(selectionStart, selectionEnd);
            text = text.substring(0, a) + insert + text.substring(b);
            cursor = a + insert.length();
        }

        selectionStart = cursor;
        selectionEnd = cursor;
    }

    private boolean hasSelection() {
        return selectionStart != selectionEnd;
    }

    private String getSelectedText() {
        int a = Math.min(selectionStart, selectionEnd);
        int b = Math.max(selectionStart, selectionEnd);
        return text.substring(a, b);
    }

    private void setCursorByMouse(double mouseX) {
        int localX = (int) mouseX - (x + 5);
        if (localX <= 0) {
            cursor = 0;
            return;
        }

        int pos = 0;
        while (pos < text.length()) {
            int width = textRenderer.getWidth(text.substring(0, pos + 1));
            if (width > localX) break;
            pos++;
        }

        cursor = Math.min(pos, text.length());
    }

    private int moveCursorWordLeft(int pos) {
        if (pos <= 0) return 0;

        while (pos > 0 && text.charAt(pos - 1) == ' ') pos--;
        while (pos > 0 && text.charAt(pos - 1) != ' ') pos--;
        return pos;
    }

    private int moveCursorWordRight(int pos) {
        if (pos >= text.length()) return text.length();

        while (pos < text.length() && text.charAt(pos) != ' ') pos++;
        while (pos < text.length() && text.charAt(pos) == ' ') pos++;
        return pos;
    }
}