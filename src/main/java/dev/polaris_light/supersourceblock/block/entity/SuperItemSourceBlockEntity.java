package dev.polaris_light.supersourceblock.block.entity;

import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperItemSourceBlockEntity extends BlockEntity implements IItemHandler {
    private static final String STORED_ITEM_KEY = "StoredItem";
    private static final String CUSTOM_OUTPUT_AMOUNT_KEY = "CustomOutputAmount";
    private static final String DIRECTION_CURSOR_KEY = "DirectionCursor";
    private static final String CUSTOM_INTERVAL_TICKS_KEY = "CustomIntervalTicks";
    private static final String TICK_COUNTER_KEY = "TickCounter";
    private static final Direction[] OUTPUT_ORDER = {
        Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN
    };

    private ItemStack storedItem = ItemStack.EMPTY;
    private int customOutputAmount = -1;
    private int customIntervalTicks = -1;
    private int directionCursor = 0;
    private int tickCounter = 0;

    public SuperItemSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SUPER_ITEM_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        setChangedAndSync();
    }

    public ItemStack getStoredItem() {
        return this.storedItem;
    }

    public void setCustomOutputAmount(int customOutputAmount) {
        this.customOutputAmount = customOutputAmount;
        setChangedAndSync();
    }

    public void setCustomIntervalTicks(int customIntervalTicks) {
        this.customIntervalTicks = customIntervalTicks;
        setChangedAndSync();
    }

    private void setChangedAndSync() {
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SuperItemSourceBlockEntity blockEntity) {
        if (level.isClientSide || blockEntity.storedItem.isEmpty()) {
            return;
        }
        blockEntity.tickCounter++;
        if (blockEntity.tickCounter < blockEntity.getIntervalTicks()) {
            return;
        }
        blockEntity.tickCounter = 0;

        int remaining = blockEntity.getOutputAmount();
        for (int i = 0; i < OUTPUT_ORDER.length && remaining > 0; i++) {
            Direction direction = OUTPUT_ORDER[(blockEntity.directionCursor + i) % OUTPUT_ORDER.length];
            int inserted = tryTransferItem(level, pos.relative(direction), direction.getOpposite(), blockEntity.storedItem, remaining);
            remaining -= inserted;
        }
        blockEntity.directionCursor = (blockEntity.directionCursor + 1) % OUTPUT_ORDER.length;
    }

    private int getOutputAmount() {
        return this.customOutputAmount > 0 ? this.customOutputAmount : SuperSourceConfig.superItemOutputAmount();
    }

    private int getIntervalTicks() {
        return this.customIntervalTicks > 0 ? this.customIntervalTicks : SuperSourceConfig.superItemOutputIntervalTicks();
    }

    private static int tryTransferItem(Level level, BlockPos targetPos, Direction side, ItemStack stored, int outputAmount) {
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, side);
        if (handler == null || stored.isEmpty()) {
            return 0;
        }
        int remaining = outputAmount;
        int totalInserted = 0;
        int maxStack = Math.max(1, stored.getMaxStackSize());
        for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
            int batch = Math.min(remaining, maxStack);
            ItemStack toInsert = stored.copyWithCount(batch);
            ItemStack remainder = handler.insertItem(slot, toInsert, false);
            int inserted = toInsert.getCount() - remainder.getCount();
            if (inserted > 0) {
                totalInserted += inserted;
                remaining -= inserted;
            }
        }
        return totalInserted;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot != 0 || this.storedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.storedItem.copyWithCount(Integer.MAX_VALUE);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || amount <= 0 || this.storedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.storedItem.copyWithCount(Math.min(amount, this.storedItem.getMaxStackSize()));
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? Integer.MAX_VALUE : 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storedItem.isEmpty()) {
            tag.put(STORED_ITEM_KEY, this.storedItem.save(registries));
        }
        if (this.customOutputAmount > 0) {
            tag.putInt(CUSTOM_OUTPUT_AMOUNT_KEY, this.customOutputAmount);
        }
        if (this.customIntervalTicks > 0) {
            tag.putInt(CUSTOM_INTERVAL_TICKS_KEY, this.customIntervalTicks);
        }
        tag.putInt(DIRECTION_CURSOR_KEY, this.directionCursor);
        tag.putInt(TICK_COUNTER_KEY, this.tickCounter);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(STORED_ITEM_KEY)) {
            this.storedItem = ItemStack.parse(registries, tag.getCompound(STORED_ITEM_KEY)).orElse(ItemStack.EMPTY);
            if (!this.storedItem.isEmpty()) {
                this.storedItem.setCount(1);
            }
        } else {
            this.storedItem = ItemStack.EMPTY;
        }
        this.customOutputAmount = tag.contains(CUSTOM_OUTPUT_AMOUNT_KEY) ? tag.getInt(CUSTOM_OUTPUT_AMOUNT_KEY) : -1;
        this.customIntervalTicks = tag.contains(CUSTOM_INTERVAL_TICKS_KEY) ? tag.getInt(CUSTOM_INTERVAL_TICKS_KEY) : -1;
        this.directionCursor = Math.floorMod(tag.getInt(DIRECTION_CURSOR_KEY), OUTPUT_ORDER.length);
        this.tickCounter = Math.max(0, tag.getInt(TICK_COUNTER_KEY));
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

    public IItemHandler createItemHandler() {
        return this;
    }
}
