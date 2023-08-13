package me.croshaw.extraslots.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.croshaw.extraslots.client.widgets.IScrollableWidget;
import me.croshaw.extraslots.client.widgets.ScrollableWidget;
import me.croshaw.extraslots.utils.Constant;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements IScrollableWidget {

    @Shadow protected int x;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow protected int y;
    @Shadow @Nullable protected Slot focusedSlot;

    @Shadow public abstract T getScreenHandler();

    @Unique
    private ScrollableWidget scrollableWidget;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addWidget(CallbackInfo ci) {
        if(client != null)
            scrollableWidget = new ScrollableWidget(this.x+this.backgroundWidth, this.y+this.backgroundHeight-86, client);
    }

    @Override
    public boolean extraSlots$scroll(double mouseX, double mouseY, double amount) {
        if(scrollableWidget.isMouseOver(mouseX, mouseY) || (this.focusedSlot!=null && this.focusedSlot.inventory instanceof PlayerInventory))
            return scrollableWidget.mouseScrolled(mouseX, mouseY, amount);
        else return false;
    }

    @Override
    public void extraSlots$updateWidget(int x, int y) {
        scrollableWidget.setPos(x, y);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return extraSlots$scroll(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        scrollableWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 0))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(this.client == null || this.client.player == null || this.getScreenHandler() == null || !Constant.RenderTextOnSlot) return;
        for(int i = 0; i < this.getScreenHandler().slots.size(); ++i) {
            Slot slot = this.getScreenHandler().slots.get(i);
            if(!this.client.player.isCreative() && scrollableWidget.isActive() && slot.inventory == client.player.getInventory() && slot.getIndex() >=9 && slot.getIndex() < 36) {
                this.setZOffset(100);
                this.itemRenderer.zOffset = 100.0F;
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                matrices.scale(Constant.TextSize, Constant.TextSize, Constant.TextSize);
                float upscaleF = 1/Constant.TextSize;
                textRenderer.draw(matrices, getText(slot.getIndex()-8), (int)(slot.x*upscaleF), (int)(slot.y*upscaleF), Constant.TextColor);
                matrices.scale(upscaleF, upscaleF, upscaleF);
                this.itemRenderer.zOffset = 0F;
                this.setZOffset(0);
            }
        }
    }

    @Unique
    private Text getText(int i) {
        i += InventoryHelper.getCurrentScrollPos(client.player)*9;
        int totalSize = 27 + InventoryHelper.getSize(client.player);
        if(i > totalSize)
            i-=totalSize;
        return Text.of(String.valueOf(i));
    }
}
