package dev.polaris_light.supersourceblock.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

final class ItemOnFacesRenderer {
    private static final float ITEM_SCALE = 0.75F;
    private static final float BLOCK_SCALE = 0.75F;

    private ItemOnFacesRenderer() {
    }

    static void render(ItemStack storedItem, Level level, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        if (storedItem.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        float scale = storedItem.getItem() instanceof BlockItem ? BLOCK_SCALE : ITEM_SCALE;
        double time = level != null ? (level.getGameTime() + partialTick) : partialTick;
        float angle = (float) ((time * 2.5D) % 360.0D);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(storedItem, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, level, 0);
        poseStack.popPose();
    }
}
