package me.croshaw.extraslots.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ScrollButton extends TexturedButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int hoveredUOffset;

    public ScrollButton(int x, int y, int width, int height, int u, int v, int hoveredUOffset, Identifier texture, PressAction pressAction) {
        super(x, y, width, height, u, v, texture, pressAction);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.hoveredUOffset = hoveredUOffset;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        int i = this.u;
        if (this.isHovered()) {
            i += this.hoveredUOffset;
        }

        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, (float) i, (float) this.v, this.width, this.height, 256, 256);
        drawTexture(matrices, this.x, this.y + this.height - 1, (float) i, (float) this.v + 52, this.width, 1, 256, 256);
        if (this.hovered) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }
}
