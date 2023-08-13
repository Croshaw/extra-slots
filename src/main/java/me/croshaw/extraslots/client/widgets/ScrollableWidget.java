package me.croshaw.extraslots.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import me.croshaw.extraslots.ExtraSlots;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ScrollableWidget extends DrawableHelper implements Drawable, Element, Selectable {

    public static final Identifier TEXTURE = ExtraSlots.id("textures/gui/widget.png");
    private int x;
    private int y;
    private boolean isActive;
    private final int[] positions;
    private double currentDeltaY;
    private int currentPosition;
    private final ScrollButton buttonWidget;
    private final ClientPlayerEntity player;

    public ScrollableWidget(int x, int y, MinecraftClient client) {
        this.x = x;
        this.y = y;
        player=client.player;
        int slotHeight = 3+(int)Math.ceil((double) InventoryHelper.getSize(player) / 9);
        this.currentPosition= InventoryHelper.getCurrentScrollPos(player);
        currentDeltaY=0;
        int scrollHeight = Math.round((float) 53 /slotHeight*3);
        positions = new int[slotHeight-2];
        positions[0] = this.y+3;
        for(int i = 1; i < positions.length-1; i++) {
            positions[i] = positions[i-1]+53/slotHeight;
        }
        positions[positions.length-1] = this.y+56-scrollHeight;
        if(currentPosition >= positions.length)
            currentPosition = positions.length-1;
        buttonWidget = new ScrollButton(this.x, positions[currentPosition], 6, scrollHeight, 9, 0, 6, TEXTURE, button -> {});
        isActive=slotHeight > 3 && client.player!=null && !client.player.isCreative();
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean value) {
        this.isActive=value;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(!isActive) return false;
        currentPosition -=amount;
        if(currentPosition < 0)
            currentPosition=0;
        else if(currentPosition > positions.length-1)
            currentPosition = positions.length-1;
        else {
            buttonWidget.setPos(this.x, positions[currentPosition]);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(currentPosition);
            ClientPlayNetworking.send(ExtraSlots.SYNC_MESSAGE_C2S, buf);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(buttonWidget.isMouseOver(mouseX, mouseY)) {
            currentDeltaY-=deltaY;
            if(Math.abs(currentDeltaY)==positions.length) {
                boolean result = mouseScrolled(mouseX, mouseY, currentDeltaY/positions.length);
                currentDeltaY=0;
                return result;
            }
            if(Math.abs(currentDeltaY)>5)
                currentDeltaY=0;
        }
        else
            currentDeltaY=0;
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(!isActive) return;
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.disableDepthTest();
        drawTexture(matrices, x, y, 0, 0, 9, 59);
        buttonWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.FOCUSED;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        buttonWidget.setPos(x, buttonWidget.y);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 59;
    }
}
