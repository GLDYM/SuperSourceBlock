package com.github.yimeng261.sourceblock.client;

import com.github.yimeng261.sourceblock.SourceBlockMod;
import com.github.yimeng261.sourceblock.block.entity.ModBlockEntities;
import com.github.yimeng261.sourceblock.client.renderer.CreativeSourceBlockRenderer;
import com.github.yimeng261.sourceblock.client.renderer.CreativeItemSourceBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SourceBlockMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            ModBlockEntities.CREATIVE_SOURCE_BLOCK_ENTITY.get(),
            CreativeSourceBlockRenderer::new
        );
        
        event.registerBlockEntityRenderer(
            ModBlockEntities.CREATIVE_ITEM_SOURCE_BLOCK_ENTITY.get(),
            CreativeItemSourceBlockRenderer::new
        );
        
        SourceBlockMod.LOGGER.info("Registered block entity renderers");
    }
}

