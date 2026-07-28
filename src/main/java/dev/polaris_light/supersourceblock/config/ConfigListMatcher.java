package dev.polaris_light.supersourceblock.config;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

final class ConfigListMatcher {
    private ConfigListMatcher() {
    }

    public static boolean matchesItem(ItemStack stack, List<? extends String> entries) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        for (String rawEntry : entries) {
            String entry = normalize(rawEntry);
            if (entry == null) {
                continue;
            }
            if (entry.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(entry.substring(1));
                if (id != null && stack.is(TagKey.create(Registries.ITEM, id))) {
                    return true;
                }
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id != null && BuiltInRegistries.ITEM.getKey(item).equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesFluid(FluidStack stack, List<? extends String> entries) {
        if (stack.isEmpty()) {
            return false;
        }
        Fluid fluid = stack.getFluid();
        for (String rawEntry : entries) {
            String entry = normalize(rawEntry);
            if (entry == null) {
                continue;
            }
            if (entry.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(entry.substring(1));
                if (id != null && fluid.builtInRegistryHolder().is(TagKey.create(Registries.FLUID, id))) {
                    return true;
                }
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id != null && BuiltInRegistries.FLUID.getKey(fluid).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String entry) {
        if (entry == null) {
            return null;
        }
        String trimmed = entry.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
