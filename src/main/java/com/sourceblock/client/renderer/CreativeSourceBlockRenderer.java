package com.sourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sourceblock.block.entity.CreativeSourceBlockEntity;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * 创造源方块渲染器
 * 在方块的6个面上显示流体贴图
 */
public class CreativeSourceBlockRenderer implements BlockEntityRenderer<CreativeSourceBlockEntity> {
    private static final float OFFSET = 0.001f; // 极小偏移避免z-fighting
    private static final float START = 0.3125f; // 起始位置（居中）
    private static final float END = 0.6875f;   // 结束位置

    public CreativeSourceBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull CreativeSourceBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        FluidStack fluidStack = blockEntity.getStoredFluid();
        
        if (fluidStack.isEmpty()) {
            return;
        }

        // 获取流体贴图和颜色
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(fluidTypeExtensions.getStillTexture(fluidStack));

        int color = fluidTypeExtensions.getTintColor(fluidStack);
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        if (alpha < 0.1f) alpha = 1.0f;

        poseStack.pushPose();

        // 使用cutout渲染类型
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        Matrix4f matrix = poseStack.last().pose();

        // 使用最大光照值确保足够亮
        int light = LightTexture.pack(packedLight, 15); // 全亮

        // 计算UV坐标
        float minU = sprite.getU(START * 16);
        float maxU = sprite.getU(END * 16);
        float minV = sprite.getV(START * 16);
        float maxV = sprite.getV(END * 16);

        // 渲染6个面 - 注意顶点顺序要正确，让面朝外可见
        // 顶面 (Y+) - 从上往下看是逆时针
        addQuad(consumer, matrix, 
            START, 1 + OFFSET, START, minU, minV,
            START, 1 + OFFSET, END, minU, maxV,
            END, 1 + OFFSET, END, maxU, maxV,
            END, 1 + OFFSET, START, maxU, minV,
            0, 1, 0, red, green, blue, alpha, light);

        // 底面 (Y-) - 从下往上看是逆时针
        addQuad(consumer, matrix,
            START, -OFFSET, START, minU, minV,
            END, -OFFSET, START, maxU, minV,
            END, -OFFSET, END, maxU, maxV,
            START, -OFFSET, END, minU, maxV,
            0, -1, 0, red, green, blue, alpha, light);

        // 北面 (Z-) - 从北往南看是逆时针
        addQuad(consumer, matrix,
            START, START, -OFFSET, minU, maxV,
            START, END, -OFFSET, minU, minV,
            END, END, -OFFSET, maxU, minV,
            END, START, -OFFSET, maxU, maxV,
            0, 0, -1, red, green, blue, alpha, light);

        // 南面 (Z+) - 从南往北看是逆时针
        addQuad(consumer, matrix,
            START, START, 1 + OFFSET, minU, maxV,
            END, START, 1 + OFFSET, maxU, maxV,
            END, END, 1 + OFFSET, maxU, minV,
            START, END, 1 + OFFSET, minU, minV,
            0, 0, 1, red, green, blue, alpha, light);

        // 西面 (X-) - 从西往东看是逆时针
        addQuad(consumer, matrix,
            -OFFSET, START, START, minU, maxV,
            -OFFSET, START, END, maxU, maxV,
            -OFFSET, END, END, maxU, minV,
            -OFFSET, END, START, minU, minV,
            -1, 0, 0, red, green, blue, alpha, light);

        // 东面 (X+) - 从东往西看是逆时针
        addQuad(consumer, matrix,
            1 + OFFSET, START, START, minU, maxV,
            1 + OFFSET, END, START, minU, minV,
            1 + OFFSET, END, END, maxU, minV,
            1 + OFFSET, START, END, maxU, maxV,
            1, 0, 0, red, green, blue, alpha, light);

        poseStack.popPose();
    }

    private void addQuad(VertexConsumer consumer, Matrix4f matrix,
                        float x1, float y1, float z1, float u1, float v1,
                        float x2, float y2, float z2, float u2, float v2,
                        float x3, float y3, float z3, float u3, float v3,
                        float x4, float y4, float z4, float u4, float v4,
                        float nx, float ny, float nz,
                        float r, float g, float b, float a, int light) {
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1).uv2(light).normal(nx, ny, nz).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2).uv2(light).normal(nx, ny, nz).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3).uv2(light).normal(nx, ny, nz).endVertex();
        consumer.vertex(matrix, x4, y4, z4).color(r, g, b, a).uv(u4, v4).uv2(light).normal(nx, ny, nz).endVertex();
    }
}
