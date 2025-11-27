package com.sourceblock.block;

import com.sourceblock.SourceBlockMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, SourceBlockMod.MODID);

    public static final RegistryObject<Block> SOURCE_BLOCK = BLOCKS.register("source_block",
        () -> new SourceBlock(BlockBehaviour.Properties.of()
            .strength(0.5F, 1200.0F)
            .sound(SoundType.METAL)));

    public static final RegistryObject<Block> CREATIVE_SOURCE_BLOCK = BLOCKS.register("creative_source_block",
        () -> new CreativeSourceBlock(BlockBehaviour.Properties.of()
            .strength(0.5F, 3600000.0F)
            .noLootTable()
            .sound(SoundType.METAL)));

    public static final RegistryObject<Block> CREATIVE_ITEM_SOURCE_BLOCK = BLOCKS.register("creative_item_source_block",
        () -> new CreativeItemSourceBlock(BlockBehaviour.Properties.of()
            .strength(0.5F, 3600000.0F)
            .noLootTable()
            .sound(SoundType.METAL)));

    public static final RegistryObject<Block> ITEM_SOURCE_BLOCK = BLOCKS.register("item_source_block",
        () -> new ItemSourceBlock(BlockBehaviour.Properties.of()
            .strength(0.5F, 1200.0F)
            .sound(SoundType.METAL)));
}

