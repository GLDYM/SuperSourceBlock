package com.github.yimeng261.sourceblock.compat;

import com.github.yimeng261.sourceblock.SourceBlockMod;
import com.github.yimeng261.sourceblock.block.entity.ModBlockEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Mekanism兼容性处理
 * 仅在Mekanism模组存在时加载
 */
public class MekanismCompat {
    
    private static final String MEKANISM_MOD_ID = "mekanism";
    
    /**
     * 检查Mekanism是否已安装
     */
    public static boolean isMekanismLoaded() {
        return ModList.get().isLoaded(MEKANISM_MOD_ID);
    }
    
    /**
     * 初始化Mekanism兼容性
     * 仅在Mekanism已安装时调用
     */
    public static void init(IEventBus modEventBus) {
        if (isMekanismLoaded()) {
            SourceBlockMod.LOGGER.info("检测到Mekanism，启用化学物质处理功能");
            modEventBus.addListener(MekanismCompat::commonSetup);
        }
    }
    
    /**
     * 在CommonSetup阶段注册Mekanism化学物质能力
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                // 注册SourceBlockEntity的化学物质处理能力
                registerChemicalCapability(ModBlockEntities.SOURCE_BLOCK_ENTITY.get());
                
                // 注册ItemSourceBlockEntity的化学物质处理能力
                registerChemicalCapability(ModBlockEntities.ITEM_SOURCE_BLOCK_ENTITY.get());
                
                SourceBlockMod.LOGGER.info("已注册Mekanism化学物质处理能力");
            } catch (Exception e) {
                SourceBlockMod.LOGGER.error("注册Mekanism化学物质能力失败: {}", e.getMessage(), e);
            }
        });
    }
    
    /**
     * 为指定的方块实体类型注册化学物质能力
     */
    private static <T> void registerChemicalCapability(T blockEntityType) {
        try {
            // 由于Forge的Capability系统在1.20中的工作方式，
            // 我们需要在方块实体的getCapability方法中处理
            // 这个方法主要用于初始化和日志记录
            SourceBlockMod.LOGGER.debug("准备为方块实体注册化学能力: {}", blockEntityType);
        } catch (Exception e) {
            SourceBlockMod.LOGGER.error("注册化学能力时出错", e);
        }
    }
}

