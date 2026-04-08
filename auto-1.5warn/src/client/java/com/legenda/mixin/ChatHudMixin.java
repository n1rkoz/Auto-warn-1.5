package com.legenda.mixin;

import com.legenda.moderation.ModerationState;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void legenda$onAddMessage(Text message, CallbackInfo ci) {
        if (message != null) {
            ModerationState.onChatMessage(message.getString());
        }
    }
}