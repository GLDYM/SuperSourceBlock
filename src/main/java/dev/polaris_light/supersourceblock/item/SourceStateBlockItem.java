package dev.polaris_light.supersourceblock.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SourceStateBlockItem extends BlockItem {
    public SourceStateBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null || data.isEmpty()) {
            return;
        }
        HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }

        CompoundTag tag = data.copyTag();

        if (tag.contains("StoredFluid", CompoundTag.TAG_COMPOUND)) {
            FluidStack fluid = FluidStack.parse(registries, tag.getCompound("StoredFluid")).orElse(FluidStack.EMPTY);
            if (fluid.isEmpty()) {
                tooltipComponents.add(Component.translatable("tooltip.super_source_block.stored_empty").withStyle(ChatFormatting.GRAY));
            } else {
                tooltipComponents.add(
                    Component.translatable(
                        "tooltip.super_source_block.stored_fluid",
                        fluid.getHoverName(),
                        Component.literal(String.valueOf(fluid.getAmount()))
                    ).withStyle(ChatFormatting.GRAY)
                );
            }
        }

        if (tag.contains("StoredItem", CompoundTag.TAG_COMPOUND)) {
            ItemStack storedItem = ItemStack.parse(registries, tag.getCompound("StoredItem")).orElse(ItemStack.EMPTY);
            int amount = tag.contains("StoredCount", CompoundTag.TAG_INT) ? tag.getInt("StoredCount") : storedItem.getCount();
            if (storedItem.isEmpty()) {
                tooltipComponents.add(Component.translatable("tooltip.super_source_block.stored_empty").withStyle(ChatFormatting.GRAY));
            } else {
                tooltipComponents.add(
                    Component.translatable(
                        "tooltip.super_source_block.stored_item",
                        storedItem.getHoverName(),
                        Component.literal(String.valueOf(amount))
                    ).withStyle(ChatFormatting.GRAY)
                );
            }
        }
    }
}

