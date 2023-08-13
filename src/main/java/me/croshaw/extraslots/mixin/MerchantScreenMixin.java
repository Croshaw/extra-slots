package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.client.widgets.IScrollableWidget;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantScreen.class)
public class MerchantScreenMixin {
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if(((IScrollableWidget)this).extraSlots$scroll(mouseX, mouseY, amount))
            cir.setReturnValue(true);
    }
}
