package dev.polaris_light.supersourceblock.compat;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.compat.mekanism.EmptyFluidChemicalHandler;
import dev.polaris_light.supersourceblock.compat.mekanism.EmptyItemChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class MekanismCompat {
    private static final String MEKANISM_MOD_ID = "mekanism";

    private MekanismCompat() {
    }

    public static void init(IEventBus modEventBus) {
        if (!ModList.get().isLoaded(MEKANISM_MOD_ID)) {
            return;
        }
        modEventBus.addListener(MekanismCompat::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        try {
            event.registerBlockEntity(
                Capabilities.CHEMICAL.block(),
                ModBlockEntities.EMPTY_FLUID_SOURCE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> side == null ? null : new EmptyFluidChemicalHandler()
            );
            event.registerBlockEntity(
                Capabilities.CHEMICAL.block(),
                ModBlockEntities.EMPTY_ITEM_SOURCE_BLOCK_ENTITY.get(),
                (blockEntity, side) -> side == null ? null : new EmptyItemChemicalHandler()
            );
        } catch (Exception e) {
            SuperSourceBlockMod.LOGGER.error("Failed to register Mekanism chemical compatibility", e);
        }
    }
}
