package dev.polaris_light.supersourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.polaris_light.supersourceblock.block.entity.EmptyItemSourceBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class EmptyItemSourceBlockRenderer implements BlockEntityRenderer<EmptyItemSourceBlockEntity> {
    public EmptyItemSourceBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull EmptyItemSourceBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemOnFacesRenderer.render(blockEntity.getStoredItem(), blockEntity.getLevel(), poseStack, bufferSource, packedLight, partialTick);
    }
}
