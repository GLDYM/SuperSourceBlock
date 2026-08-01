package dev.polaris_light.supersourceblock.compat;

import dev.polaris_light.supersourceblock.compat.mekanism.MekanismChemicalCapabilities;
import dev.polaris_light.supersourceblock.compat.mekanism.MekanismChemicalUi;
import dev.polaris_light.supersourceblock.compat.mekanism.client.MekanismChemicalClient;
import dev.polaris_light.supersourceblock.compat.mekanism.data.ChemicalSourceRule;
import dev.polaris_light.supersourceblock.compat.mekanism.data.MekanismChemicalRecipeManager;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class MekanismCompat {
    private static final String MEKANISM_MOD_ID = "mekanism";

    private MekanismCompat() {
    }

    public static boolean isMekanismLoaded() {
        return ModList.get().isLoaded(MEKANISM_MOD_ID);
    }


    public static void registerSourceBlockCapabilities(IEventBus modEventBus) {
        if (!isMekanismLoaded()) {
            return;
        }
        modEventBus.addListener(MekanismChemicalCapabilities::registerCompatCapabilities);
        modEventBus.addListener(MekanismChemicalCapabilities::registerSourceBlockCapabilities);
    }

    public static void addReloadListeners(AddReloadListenerEvent event) {
        if (!isMekanismLoaded()) {
            return;
        }
        MekanismChemicalRecipeManager.addReloadListeners(event);
    }

    public static void setChemicalRules(List<ChemicalSourceRule> rules) {
        if (!isMekanismLoaded()) {
            return;
        }
        MekanismChemicalRecipeManager.setChemicalRules(rules);
    }

    public static void appendChemicalTooltip(CompoundTag tag, HolderLookup.Provider registries, List<Component> tooltipComponents) {
        if (!isMekanismLoaded()) {
            return;
        }
        MekanismChemicalUi.appendChemicalTooltip(tag, registries, tooltipComponents);
    }

    public static void registerClientRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!isMekanismLoaded()) {
            return;
        }
        MekanismChemicalClient.registerRenderers(event);
    }
}
