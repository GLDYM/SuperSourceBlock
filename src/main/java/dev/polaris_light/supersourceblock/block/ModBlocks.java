package dev.polaris_light.supersourceblock.block;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SuperSourceBlockMod.MODID);

    public static final DeferredBlock<Block> EMPTY_FLUID_SOURCE_BLOCK = BLOCKS.register(
        "empty_fluid_source_block",
        () -> new EmptyFluidSourceBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(1.0F, 1200.0F)
                .sound(SoundType.METAL)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .noLootTable()
        )
    );

    public static final DeferredBlock<Block> SUPER_FLUID_SOURCE_BLOCK = BLOCKS.register(
        "super_fluid_source_block",
        () -> new SuperFluidSourceBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(2.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .lightLevel(state -> 12)
                .noLootTable()
        )
    );

    public static final DeferredBlock<Block> EMPTY_ITEM_SOURCE_BLOCK = BLOCKS.register(
        "empty_item_source_block",
        () -> new EmptyItemSourceBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(1.0F, 1200.0F)
                .sound(SoundType.METAL)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .noLootTable()
        )
    );

    public static final DeferredBlock<Block> SUPER_ITEM_SOURCE_BLOCK = BLOCKS.register(
        "super_item_source_block",
        () -> new SuperItemSourceBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(2.0F, 3600000.0F)
                .sound(SoundType.METAL)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .lightLevel(state -> 12)
                .noLootTable()
        )
    );

    private ModBlocks() {
    }
}
