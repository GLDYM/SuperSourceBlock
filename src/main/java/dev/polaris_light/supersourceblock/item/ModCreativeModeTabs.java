package dev.polaris_light.supersourceblock.item;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.compat.MekanismCompat;
import dev.polaris_light.supersourceblock.compat.mekanism.item.MekanismChemicalItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SuperSourceBlockMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUPER_SOURCE_TAB =
        CREATIVE_MODE_TABS.register("super_source_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.super_source_block"))
                .withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .icon(() -> ModItems.SUPER_FLUID_SOURCE_BLOCK.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
                    output.accept(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
                    output.accept(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
                    output.accept(ModItems.SUPER_ITEM_SOURCE_BLOCK.get());
                    if (MekanismCompat.isMekanismLoaded()) {
                        output.accept(MekanismChemicalItems.EMPTY_CHEMICAL_SOURCE_BLOCK.get());
                        output.accept(MekanismChemicalItems.SUPER_CHEMICAL_SOURCE_BLOCK.get());
                    }
                })
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build()
        );

    private ModCreativeModeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(ModCreativeModeTabs::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            return;
        }
        event.accept(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
        event.accept(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
        event.accept(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
        event.accept(ModItems.SUPER_ITEM_SOURCE_BLOCK.get());
        if (MekanismCompat.isMekanismLoaded()) {
            event.accept(MekanismChemicalItems.EMPTY_CHEMICAL_SOURCE_BLOCK.get());
            event.accept(MekanismChemicalItems.SUPER_CHEMICAL_SOURCE_BLOCK.get());
        }
    }
}
