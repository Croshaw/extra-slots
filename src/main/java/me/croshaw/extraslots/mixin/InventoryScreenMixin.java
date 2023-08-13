package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.client.widgets.IScrollableWidget;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }
    @Inject(method = "init", at = @At("TAIL"))
    public void addWidget(CallbackInfo ci) {
        ((IScrollableWidget)this).extraSlots$updateWidget(this.x+this.backgroundWidth, this.y+this.backgroundHeight-86);
    }
    @Inject(method = "method_19891", at = @At("TAIL"))
    private void updateWidgetPosition(ButtonWidget button, CallbackInfo ci) {
        ((IScrollableWidget)this).extraSlots$updateWidget(this.x+this.backgroundWidth, this.y+this.backgroundHeight-86);
    }
}
