package dev.polaris_light.supersourceblock.block.entity;

import dev.polaris_light.supersourceblock.block.ModBlocks;
import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import dev.polaris_light.supersourceblock.data.ItemSourceRule;
import dev.polaris_light.supersourceblock.data.SourceRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyItemSourceBlockEntity extends BlockEntity implements IItemHandler {
    private static final String STORED_ITEM_KEY = "StoredItem";
    private static final String STORED_COUNT_KEY = "StoredCount";

    private ItemStack storedItem = ItemStack.EMPTY;
    private int storedCount = 0;

    public EmptyItemSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EMPTY_ITEM_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ItemStack getStoredItem() {
        return this.storedItem;
    }

    public int getStoredCount() {
        return this.storedCount;
    }

    private int getRequiredItems() {
        return SuperSourceConfig.requiredItems();
    }

    private int getRemainingCapacity() {
        return Math.max(0, getRequiredItems() - this.storedCount);
    }

    private boolean canAccept(ItemStack stack) {
        return this.storedItem.isEmpty() || ItemStack.isSameItemSameComponents(this.storedItem, stack);
    }

    private void setChangedAndSync() {
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void transformIntoSuperSource(ItemSourceRule rule) {
        if (this.level == null || this.level.isClientSide || this.storedItem.isEmpty()) {
            return;
        }
        BlockPos pos = getBlockPos();
        this.level.setBlockAndUpdate(pos, ModBlocks.SUPER_ITEM_SOURCE_BLOCK.get().defaultBlockState());
        if (this.level.getBlockEntity(pos) instanceof SuperItemSourceBlockEntity superEntity) {
            superEntity.setStoredItem(this.storedItem.copyWithCount(1));
            superEntity.setCustomOutputAmount(resolveOutputAmount(rule));
            superEntity.setCustomIntervalTicks(resolveIntervalTicks(rule));
        }
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot != 0 || this.storedItem.isEmpty() || this.storedCount <= 0) {
            return ItemStack.EMPTY;
        }
        return this.storedItem.copyWithCount(this.storedCount);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot != 0 || stack.isEmpty() || !canAccept(stack)) {
            return stack;
        }
        int accepted = Math.min(stack.getCount(), getRemainingCapacity());
        if (accepted <= 0) {
            return stack;
        }

        if (!simulate) {
            if (this.storedItem.isEmpty()) {
                this.storedItem = stack.copyWithCount(1);
            }
            this.storedCount += accepted;
            setChangedAndSync();
            ItemSourceRule matchedRule = getMatchedRule();
            if (shouldTransform(matchedRule)) {
                transformIntoSuperSource(matchedRule);
            }
        }

        ItemStack remainder = stack.copy();
        remainder.shrink(accepted);
        return remainder;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || amount <= 0 || this.storedItem.isEmpty() || this.storedCount <= 0) {
            return ItemStack.EMPTY;
        }

        int extracted = Math.min(amount, Math.min(this.storedCount, this.storedItem.getMaxStackSize()));
        ItemStack out = this.storedItem.copyWithCount(extracted);
        if (!simulate) {
            this.storedCount -= extracted;
            if (this.storedCount <= 0) {
                this.storedItem = ItemStack.EMPTY;
                this.storedCount = 0;
            }
            setChangedAndSync();
        }
        return out;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? Integer.MAX_VALUE : 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == 0 && !stack.isEmpty() && canAccept(stack);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storedItem.isEmpty()) {
            tag.put(STORED_ITEM_KEY, this.storedItem.save(registries));
            tag.putInt(STORED_COUNT_KEY, this.storedCount);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(STORED_ITEM_KEY)) {
            this.storedItem = ItemStack.parse(registries, tag.getCompound(STORED_ITEM_KEY)).orElse(ItemStack.EMPTY);
            this.storedCount = Math.max(0, tag.getInt(STORED_COUNT_KEY));
            if (this.storedCount == 0 || this.storedItem.isEmpty()) {
                this.storedItem = ItemStack.EMPTY;
                this.storedCount = 0;
            }
        } else {
            this.storedItem = ItemStack.EMPTY;
            this.storedCount = 0;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private ItemSourceRule getMatchedRule() {
        if (this.storedItem.isEmpty() || this.storedCount <= 0 || SuperSourceConfig.allowAnyItemSource()) {
            return null;
        }
        return SourceRecipeManager.INSTANCE.findItemRule(this.storedItem, this.storedCount);
    }

    private boolean shouldTransform(ItemSourceRule matchedRule) {
        if (this.storedItem.isEmpty() || this.storedCount <= 0) {
            return false;
        }
        if (SuperSourceConfig.allowAnyItemSource()) {
            return this.storedCount >= getRequiredItems();
        }
        return matchedRule != null;
    }

    private int resolveOutputAmount(ItemSourceRule rule) {
        boolean useMax = rule != null && rule.maxOutput() != null ? rule.maxOutput() : SuperSourceConfig.superItemUseIntegerMaxOutput();
        if (useMax) {
            return Integer.MAX_VALUE;
        }
        if (rule != null && rule.outputAmount() != null) {
            return Math.max(1, rule.outputAmount());
        }
        return SuperSourceConfig.superItemOutputAmount();
    }

    private int resolveIntervalTicks(ItemSourceRule rule) {
        if (rule != null && rule.interval() != null) {
            return Math.max(1, rule.interval());
        }
        return SuperSourceConfig.superItemOutputIntervalTicks();
    }

    public IItemHandler createItemHandler() {
        return this;
    }
}
