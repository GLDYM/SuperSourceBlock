package com.sourceblock.item;

import com.sourceblock.SourceBlockMod;
import com.sourceblock.block.ModBlocks;
import com.sourceblock.block.SourceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, SourceBlockMod.MODID);

    public static final RegistryObject<BlockItem> EMPTY_SOURCE_BLOCK = ITEMS.register("empty_source_block",
        () -> new SourceBlockItem(ModBlocks.SOURCE_BLOCK.get(), 
            new Item.Properties(), SourceBlock.FluidType.EMPTY));

    public static final RegistryObject<BlockItem> WATER_SOURCE_BLOCK = ITEMS.register("water_source_block",
        () -> new SourceBlockItem(ModBlocks.SOURCE_BLOCK.get(), 
            new Item.Properties(), SourceBlock.FluidType.WATER));

    public static final RegistryObject<BlockItem> LAVA_SOURCE_BLOCK = ITEMS.register("lava_source_block",
        () -> new SourceBlockItem(ModBlocks.SOURCE_BLOCK.get(), 
            new Item.Properties(), SourceBlock.FluidType.LAVA));

    public static final RegistryObject<BlockItem> MILK_SOURCE_BLOCK = ITEMS.register("milk_source_block",
        () -> new SourceBlockItem(ModBlocks.SOURCE_BLOCK.get(), 
            new Item.Properties(), SourceBlock.FluidType.MILK));

    public static final RegistryObject<BlockItem> CREATIVE_SOURCE_BLOCK = ITEMS.register("creative_source_block",
        () -> new BlockItem(ModBlocks.CREATIVE_SOURCE_BLOCK.get(), 
            new Item.Properties()));

    public static final RegistryObject<BlockItem> CREATIVE_ITEM_SOURCE_BLOCK = ITEMS.register("creative_item_source_block",
        () -> new BlockItem(ModBlocks.CREATIVE_ITEM_SOURCE_BLOCK.get(), 
            new Item.Properties()));
}

