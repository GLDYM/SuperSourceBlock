package dev.polaris_light.supersourceblock;

import com.mojang.logging.LogUtils;

import dev.polaris_light.supersourceblock.block.ModBlocks;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.compat.MekanismCompat;
import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import dev.polaris_light.supersourceblock.data.reload.FluidSourceReloadListener;
import dev.polaris_light.supersourceblock.data.reload.ItemSourceReloadListener;
import dev.polaris_light.supersourceblock.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(SuperSourceBlockMod.MODID)
public class SuperSourceBlockMod {
    public static final String MODID = "super_source_block";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUPER_SOURCE_TAB =
        CREATIVE_MODE_TABS.register("super_source_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.super_source_block"))
                .withTabsBefore(new ResourceKey[] {CreativeModeTabs.FUNCTIONAL_BLOCKS})
                .icon(() -> ModItems.SUPER_FLUID_SOURCE_BLOCK.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
                    output.accept(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
                    output.accept(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
                    output.accept(ModItems.SUPER_ITEM_SOURCE_BLOCK.get());
                })
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build()
        );

    public SuperSourceBlockMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

        modContainer.registerConfig(ModConfig.Type.COMMON, SuperSourceConfig.SPEC);
        try {
            MekanismCompat.init(modEventBus);
        } catch (NoClassDefFoundError ignored) {
        }
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

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
            event.accept(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
            event.accept(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
            event.accept(ModItems.SUPER_ITEM_SOURCE_BLOCK.get());
        }
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new FluidSourceReloadListener());
        event.addListener(new ItemSourceReloadListener());
    }
}
