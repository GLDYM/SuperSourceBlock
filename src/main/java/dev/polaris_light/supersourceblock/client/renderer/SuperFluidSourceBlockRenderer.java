package dev.polaris_light.supersourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.polaris_light.supersourceblock.block.entity.SuperFluidSourceBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SuperFluidSourceBlockRenderer implements BlockEntityRenderer<SuperFluidSourceBlockEntity> {
    public SuperFluidSourceBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull SuperFluidSourceBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluidStack = blockEntity.getStoredFluid();
        if (fluidStack.isEmpty()) {
            return;
        }
        FluidTankRenderer.renderTankFluid(fluidStack, 1.0F, poseStack, bufferSource);
    }
}
