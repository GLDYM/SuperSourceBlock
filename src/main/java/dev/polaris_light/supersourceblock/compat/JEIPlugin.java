package dev.polaris_light.supersourceblock.compat;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SuperSourceBlockMod.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
            new ItemStack(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get()),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.super_source_block.empty_fluid_source_block.info")
        );
        registration.addIngredientInfo(
            new ItemStack(ModItems.SUPER_FLUID_SOURCE_BLOCK.get()),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.super_source_block.super_fluid_source_block.info")
        );
        registration.addIngredientInfo(
            new ItemStack(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get()),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.super_source_block.empty_item_source_block.info")
        );
        registration.addIngredientInfo(
            new ItemStack(ModItems.SUPER_ITEM_SOURCE_BLOCK.get()),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.super_source_block.super_item_source_block.info")
        );
    }
}

