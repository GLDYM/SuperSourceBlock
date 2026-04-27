package dev.polaris_light.supersourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.polaris_light.supersourceblock.block.entity.EmptyFluidSourceBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class EmptyFluidSourceBlockRenderer implements BlockEntityRenderer<EmptyFluidSourceBlockEntity> {
    public EmptyFluidSourceBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull EmptyFluidSourceBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack fluidStack = blockEntity.getStoredFluid();
        if (fluidStack.isEmpty()) {
            return;
        }

        float fillRatio = Math.min(1.0F, (float) fluidStack.getAmount() / (float) Math.max(1, blockEntity.getRequiredMb()));
        FluidTankRenderer.renderTankFluid(fluidStack, fillRatio, poseStack, bufferSource);
    }
}
