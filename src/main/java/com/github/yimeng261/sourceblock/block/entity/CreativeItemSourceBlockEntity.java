package com.github.yimeng261.sourceblock.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.github.yimeng261.sourceblock.block.entity.ItemSourceBlockEntity.tryTransferItem;

/**
 * 创造物品源方块实体 - 无限提供指定的物品
 */
public class CreativeItemSourceBlockEntity extends BlockEntity {
    private ItemStack storedItem = ItemStack.EMPTY;
    
    // 每个面的计时器
    private final Map<Direction, Integer> tickCounters = new HashMap<>();
    private final Map<Direction, Boolean> fastMode = new HashMap<>();
    
    private static final int SLOW_INTERVAL = 20;
    private static final int FAST_INTERVAL = 1;

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(ItemHandlerImpl::new);

    public CreativeItemSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_ITEM_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
        
        // 初始化所有面的计时器和模式
        for (Direction direction : Direction.values()) {
            tickCounters.put(direction, 0);
            fastMode.put(direction, false);
        }
    }

    public void setItem(ItemStack item) {
        this.storedItem = item.copy();
        this.storedItem.setCount(64); // 设置为最大堆叠数
        setChanged();
        // 同步到客户端
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void clearItem() {
        this.storedItem = ItemStack.EMPTY;
        setChanged();
        // 同步到客户端
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeItemSourceBlockEntity blockEntity) {
        if (level.isClientSide || blockEntity.storedItem.isEmpty()) return;

        // 对每个面进行处理
        for (Direction direction : Direction.values()) {
            int currentTick = blockEntity.tickCounters.get(direction);
            boolean isFastMode = blockEntity.fastMode.get(direction);
            
            currentTick++;
            
            int interval = isFastMode ? FAST_INTERVAL : SLOW_INTERVAL;
            if (currentTick >= interval) {
                currentTick = 0;
                
                BlockPos neighborPos = pos.relative(direction);
                boolean success = tryTransferItem(level, neighborPos, direction.getOpposite(), blockEntity.storedItem);
                
                blockEntity.fastMode.put(direction, success);
            }
            
            blockEntity.tickCounters.put(direction, currentTick);
        }

        blockEntity.setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        
        if (tag.contains("StoredItem")) {
            this.storedItem = ItemStack.of(tag.getCompound("StoredItem"));
        } else {
            this.storedItem = ItemStack.EMPTY;
        }
    }

    // ========== 客户端同步 ==========

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AABB getRenderBoundingBox() {
        // 扩大渲染边界以确保渲染器被调用
        return new AABB(worldPosition).inflate(1.0);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    private class ItemHandlerImpl implements IItemHandler {
        @Override
        public int getSlots() {
            return 1;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot != 0 || storedItem.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = storedItem.copy();
            result.setCount(Integer.MAX_VALUE);
            return result;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack; // 不接受输入
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || storedItem.isEmpty()) {
                return ItemStack.EMPTY;
            }
            
            ItemStack extracted = storedItem.copy();
            extracted.setCount(Math.min(amount, storedItem.getMaxStackSize()));
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false; // 不接受输入
        }
    }
}

