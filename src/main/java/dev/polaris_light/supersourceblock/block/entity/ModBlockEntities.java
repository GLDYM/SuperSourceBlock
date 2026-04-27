package dev.polaris_light.supersourceblock.block.entity;

import com.mojang.datafixers.types.Type;
import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SuperSourceBlockMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmptyFluidSourceBlockEntity>> EMPTY_FLUID_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register(
            "empty_fluid_source_block_entity",
            () -> BlockEntityType.Builder.of(EmptyFluidSourceBlockEntity::new, ModBlocks.EMPTY_FLUID_SOURCE_BLOCK.get()).build((Type<?>) null)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperFluidSourceBlockEntity>> SUPER_FLUID_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register(
            "super_fluid_source_block_entity",
            () -> BlockEntityType.Builder.of(SuperFluidSourceBlockEntity::new, ModBlocks.SUPER_FLUID_SOURCE_BLOCK.get()).build((Type<?>) null)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmptyItemSourceBlockEntity>> EMPTY_ITEM_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register(
            "empty_item_source_block_entity",
            () -> BlockEntityType.Builder.of(EmptyItemSourceBlockEntity::new, ModBlocks.EMPTY_ITEM_SOURCE_BLOCK.get()).build((Type<?>) null)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperItemSourceBlockEntity>> SUPER_ITEM_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register(
            "super_item_source_block_entity",
            () -> BlockEntityType.Builder.of(SuperItemSourceBlockEntity::new, ModBlocks.SUPER_ITEM_SOURCE_BLOCK.get()).build((Type<?>) null)
        );

    private ModBlockEntities() {
    }
}
