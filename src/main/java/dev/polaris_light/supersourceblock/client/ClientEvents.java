package dev.polaris_light.supersourceblock.client;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.block.ModBlocks;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.client.renderer.EmptyFluidSourceBlockRenderer;
import dev.polaris_light.supersourceblock.client.renderer.EmptyItemSourceBlockRenderer;
import dev.polaris_light.supersourceblock.client.renderer.SuperFluidSourceBlockRenderer;
import dev.polaris_light.supersourceblock.client.renderer.SuperItemSourceBlockRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SuperSourceBlockMod.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.EMPTY_FLUID_SOURCE_BLOCK_ENTITY.get(), EmptyFluidSourceBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SUPER_FLUID_SOURCE_BLOCK_ENTITY.get(), SuperFluidSourceBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.EMPTY_ITEM_SOURCE_BLOCK_ENTITY.get(), EmptyItemSourceBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SUPER_ITEM_SOURCE_BLOCK_ENTITY.get(), SuperItemSourceBlockRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.EMPTY_FLUID_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SUPER_FLUID_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.EMPTY_ITEM_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SUPER_ITEM_SOURCE_BLOCK.get(), RenderType.translucent());
        });
    }
}
