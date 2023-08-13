package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.client.widgets.IScrollableWidget;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LoomScreen.class)
public class LoomScreenMixin {
    @Inject(method = "mouseScrolled", at = @At("TAIL"))
    private void mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        ((IScrollableWidget)this).extraSlots$scroll(mouseX, mouseY, amount);
    }
}