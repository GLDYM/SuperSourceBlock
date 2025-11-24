package com.sourceblock.block.entity;

import com.sourceblock.SourceBlockMod;
import com.sourceblock.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SourceBlockMod.MODID);

    public static final RegistryObject<BlockEntityType<SourceBlockEntity>> SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("source_block_entity", () ->
            BlockEntityType.Builder.of(SourceBlockEntity::new, ModBlocks.SOURCE_BLOCK.get())
                .build(null));

    public static final RegistryObject<BlockEntityType<CreativeSourceBlockEntity>> CREATIVE_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("creative_source_block_entity", () ->
            BlockEntityType.Builder.of(CreativeSourceBlockEntity::new, ModBlocks.CREATIVE_SOURCE_BLOCK.get())
                .build(null));

    public static final RegistryObject<BlockEntityType<CreativeItemSourceBlockEntity>> CREATIVE_ITEM_SOURCE_BLOCK_ENTITY =
        BLOCK_ENTITIES.register("creative_item_source_block_entity", () ->
            BlockEntityType.Builder.of(CreativeItemSourceBlockEntity::new, ModBlocks.CREATIVE_ITEM_SOURCE_BLOCK.get())
                .build(null));
}

