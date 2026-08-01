package dev.polaris_light.supersourceblock;

import com.mojang.logging.LogUtils;

import dev.polaris_light.supersourceblock.block.ModBlocks;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.compat.MekanismCompat;
import dev.polaris_light.supersourceblock.compat.mekanism.block.MekanismChemicalBlocks;
import dev.polaris_light.supersourceblock.compat.mekanism.block.entity.MekanismChemicalBlockEntities;
import dev.polaris_light.supersourceblock.compat.mekanism.item.MekanismChemicalItems;
import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import dev.polaris_light.supersourceblock.data.reload.FluidSourceReloadListener;
import dev.polaris_light.supersourceblock.data.reload.ItemSourceReloadListener;
import dev.polaris_light.supersourceblock.item.ModCreativeModeTabs;
import dev.polaris_light.supersourceblock.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(SuperSourceBlockMod.MODID)
public class SuperSourceBlockMod {
    public static final String MODID = "super_source_block";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SuperSourceBlockMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        if (MekanismCompat.isMekanismLoaded()) {
            MekanismChemicalBlocks.BLOCKS.register(modEventBus);
            MekanismChemicalItems.ITEMS.register(modEventBus);
            MekanismChemicalBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        }

        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        MekanismCompat.registerSourceBlockCapabilities(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

        modContainer.registerConfig(ModConfig.Type.COMMON, SuperSourceConfig.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.EMPTY_FLUID_SOURCE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.createFluidHandler()
        );
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.SUPER_FLUID_SOURCE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.createFluidHandler()
        );
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.EMPTY_ITEM_SOURCE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.createItemHandler()
        );
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.SUPER_ITEM_SOURCE_BLOCK_ENTITY.get(),
            (blockEntity, side) -> blockEntity.createItemHandler()
        );
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new FluidSourceReloadListener());
        event.addListener(new ItemSourceReloadListener());
        MekanismCompat.addReloadListeners(event);
    }
}
