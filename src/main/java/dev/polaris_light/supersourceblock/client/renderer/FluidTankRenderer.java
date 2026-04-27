package dev.polaris_light.supersourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

final class FluidTankRenderer {
    private static final float TANK_MIN = 0.125F;
    private static final float TANK_MAX = 0.875F;
    private static final float TANK_BOTTOM = 0.125F;
    private static final float TANK_HEIGHT = 0.75F;
    private static final int LIGHT = 15728880;

    private FluidTankRenderer() {
    }

    static void renderTankFluid(FluidStack fluidStack, float fillRatio, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (fluidStack.isEmpty() || fillRatio <= 0.0F) {
            return;
        }
        float clampedRatio = Math.max(0.0F, Math.min(1.0F, fillRatio));
        float yBottom = TANK_BOTTOM;
        float yTop = yBottom + (TANK_HEIGHT * clampedRatio);

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(stillTexture);

        int color = fluidTypeExtensions.getTintColor(fluidStack);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = ((color >> 24) & 255) / 255.0F;
        if (alpha < 0.1F) {
            alpha = 1.0F;
        }

        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        addQuad(consumer, matrix, TANK_MIN, yTop, TANK_MIN, minU, minV, TANK_MIN, yTop, TANK_MAX, minU, maxV, TANK_MAX, yTop, TANK_MAX, maxU, maxV, TANK_MAX, yTop, TANK_MIN, maxU, minV, 0.0F, 1.0F, 0.0F, red, green, blue, alpha);
        addQuad(consumer, matrix, TANK_MIN, yBottom, TANK_MIN, minU, minV, TANK_MAX, yBottom, TANK_MIN, maxU, minV, TANK_MAX, yBottom, TANK_MAX, maxU, maxV, TANK_MIN, yBottom, TANK_MAX, minU, maxV, 0.0F, -1.0F, 0.0F, red, green, blue, alpha);
        addQuad(consumer, matrix, TANK_MIN, yBottom, TANK_MIN, minU, maxV, TANK_MIN, yTop, TANK_MIN, minU, minV, TANK_MAX, yTop, TANK_MIN, maxU, minV, TANK_MAX, yBottom, TANK_MIN, maxU, maxV, 0.0F, 0.0F, -1.0F, red, green, blue, alpha);
        addQuad(consumer, matrix, TANK_MIN, yBottom, TANK_MAX, minU, maxV, TANK_MAX, yBottom, TANK_MAX, maxU, maxV, TANK_MAX, yTop, TANK_MAX, maxU, minV, TANK_MIN, yTop, TANK_MAX, minU, minV, 0.0F, 0.0F, 1.0F, red, green, blue, alpha);
        addQuad(consumer, matrix, TANK_MIN, yBottom, TANK_MIN, minU, maxV, TANK_MIN, yBottom, TANK_MAX, maxU, maxV, TANK_MIN, yTop, TANK_MAX, maxU, minV, TANK_MIN, yTop, TANK_MIN, minU, minV, -1.0F, 0.0F, 0.0F, red, green, blue, alpha);
        addQuad(consumer, matrix, TANK_MAX, yBottom, TANK_MIN, minU, maxV, TANK_MAX, yTop, TANK_MIN, minU, minV, TANK_MAX, yTop, TANK_MAX, maxU, minV, TANK_MAX, yBottom, TANK_MAX, maxU, maxV, 1.0F, 0.0F, 0.0F, red, green, blue, alpha);
        poseStack.popPose();
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, float x4, float y4, float z4, float u4, float v4, float nx, float ny, float nz, float r, float g, float b, float a) {
        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LIGHT).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LIGHT).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LIGHT).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a).setUv(u4, v4).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LIGHT).setNormal(nx, ny, nz);
    }
}
