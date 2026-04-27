package dev.polaris_light.supersourceblock.item;

import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SuperSourceBlockMod.MODID);

    public static final DeferredItem<BlockItem> EMPTY_FLUID_SOURCE_BLOCK = ITEMS.register(
        "empty_fluid_source_block",
        () -> new SourceStateBlockItem(ModBlocks.EMPTY_FLUID_SOURCE_BLOCK.get(), new Item.Properties())
    );

    public static final DeferredItem<BlockItem> SUPER_FLUID_SOURCE_BLOCK = ITEMS.register(
        "super_fluid_source_block",
        () -> new SourceStateBlockItem(ModBlocks.SUPER_FLUID_SOURCE_BLOCK.get(), new Item.Properties())
    );

    public static final DeferredItem<BlockItem> EMPTY_ITEM_SOURCE_BLOCK = ITEMS.register(
        "empty_item_source_block",
        () -> new SourceStateBlockItem(ModBlocks.EMPTY_ITEM_SOURCE_BLOCK.get(), new Item.Properties())
    );

    public static final DeferredItem<BlockItem> SUPER_ITEM_SOURCE_BLOCK = ITEMS.register(
        "super_item_source_block",
        () -> new SourceStateBlockItem(ModBlocks.SUPER_ITEM_SOURCE_BLOCK.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
