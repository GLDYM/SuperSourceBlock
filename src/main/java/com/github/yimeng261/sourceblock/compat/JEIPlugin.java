package com.github.yimeng261.sourceblock.compat;

import com.github.yimeng261.sourceblock.SourceBlockMod;
import com.github.yimeng261.sourceblock.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SourceBlockMod.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        // 为牛奶源方块添加信息提示
        registration.addIngredientInfo(
            Collections.singletonList(new ItemStack(ModItems.MILK_SOURCE_BLOCK.get())),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.sourceblock.milk_source_block.info")
        );
        
        // 为水源方块添加信息提示
        registration.addIngredientInfo(
            Collections.singletonList(new ItemStack(ModItems.WATER_SOURCE_BLOCK.get())),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.sourceblock.water_source_block.info")
        );
        
        // 为岩浆源方块添加信息提示
        registration.addIngredientInfo(
            Collections.singletonList(new ItemStack(ModItems.LAVA_SOURCE_BLOCK.get())),
            VanillaTypes.ITEM_STACK,
            Component.translatable("jei.sourceblock.lava_source_block.info")
        );
    }
}

