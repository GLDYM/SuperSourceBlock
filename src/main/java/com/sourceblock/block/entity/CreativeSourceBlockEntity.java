package com.sourceblock.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 创造源方块实体 - 无限提供指定的流体
 */
public class CreativeSourceBlockEntity extends BlockEntity {
    private FluidStack storedFluid = FluidStack.EMPTY;
    
    // 每个面的计时器
    private final Map<Direction, Integer> tickCounters = new HashMap<>();
    private final Map<Direction, Boolean> fastMode = new HashMap<>();
    
    private static final int SLOW_INTERVAL = 20;
    private static final int FAST_INTERVAL = 1;
    private static final int TRANSFER_AMOUNT = Integer.MAX_VALUE;
    public static final int CAPACITY = Integer.MAX_VALUE;

    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new FluidHandlerImpl());

    public CreativeSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
        
        // 初始化所有面的计时器和模式
        for (Direction direction : Direction.values()) {
            tickCounters.put(direction, 0);
            fastMode.put(direction, false);
        }
    }

    public void setFluid(FluidStack fluid) {
        this.storedFluid = fluid.copy();
        this.storedFluid.setAmount(CAPACITY);
        setChanged();
        // 同步到客户端
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void clearFluid() {
        this.storedFluid = FluidStack.EMPTY;
        setChanged();
        // 同步到客户端
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public FluidStack getStoredFluid() {
        return storedFluid;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeSourceBlockEntity blockEntity) {
        if (level.isClientSide || blockEntity.storedFluid.isEmpty()) return;

        FluidStack fluidToTransfer = blockEntity.storedFluid.copy();
        fluidToTransfer.setAmount(TRANSFER_AMOUNT);

        // 对每个面进行处理
        for (Direction direction : Direction.values()) {
            int currentTick = blockEntity.tickCounters.get(direction);
            boolean isFastMode = blockEntity.fastMode.get(direction);
            
            currentTick++;
            
            int interval = isFastMode ? FAST_INTERVAL : SLOW_INTERVAL;
            if (currentTick >= interval) {
                currentTick = 0;
                
                BlockPos neighborPos = pos.relative(direction);
                boolean success = tryTransferFluid(level, neighborPos, direction.getOpposite(), fluidToTransfer);
                
                blockEntity.fastMode.put(direction, success);
            }
            
            blockEntity.tickCounters.put(direction, currentTick);
        }

        blockEntity.setChanged();
    }

    private static boolean tryTransferFluid(Level level, BlockPos pos, Direction direction, FluidStack fluidStack) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<IFluidHandler> capability = be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction);
            return capability.map(handler -> {
                int filled = handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                return filled > 0;
            }).orElse(false);
        }
        return false;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (!storedFluid.isEmpty()) {
            tag.put("StoredFluid", storedFluid.writeToNBT(new CompoundTag()));
        }
        
        CompoundTag facesTag = new CompoundTag();
        for (Direction direction : Direction.values()) {
            CompoundTag faceTag = new CompoundTag();
            faceTag.putInt("tick", tickCounters.get(direction));
            faceTag.putBoolean("fast", fastMode.get(direction));
            facesTag.put(direction.getName(), faceTag);
        }
        tag.put("faces", facesTag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        
        if (tag.contains("StoredFluid")) {
            this.storedFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("StoredFluid"));
        } else {
            this.storedFluid = FluidStack.EMPTY;
        }
        
        if (tag.contains("faces")) {
            CompoundTag facesTag = tag.getCompound("faces");
            for (Direction direction : Direction.values()) {
                if (facesTag.contains(direction.getName())) {
                    CompoundTag faceTag = facesTag.getCompound(direction.getName());
                    tickCounters.put(direction, faceTag.getInt("tick"));
                    fastMode.put(direction, faceTag.getBoolean("fast"));
                }
            }
        }
    }

    // ========== 客户端同步 ==========

    @Override
    public CompoundTag getUpdateTag() {
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
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    private class FluidHandlerImpl implements IFluidHandler {
        @Override
        public int getTanks() {
            return 1;
        }

        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (tank != 0 || storedFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FluidStack result = storedFluid.copy();
            result.setAmount(CAPACITY);
            return result;
        }

        @Override
        public int getTankCapacity(int tank) {
            return CAPACITY;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return false; // 不接受输入
        }

        @Override
        public int fill(FluidStack resource, @NotNull FluidAction action) {
            return 0; // 不接受输入
        }

        @NotNull
        @Override
        public FluidStack drain(FluidStack resource, @NotNull FluidAction action) {
            if (resource.isEmpty() || storedFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            
            if (storedFluid.getFluid() == resource.getFluid()) {
                return new FluidStack(storedFluid.getFluid(), resource.getAmount());
            }
            
            return FluidStack.EMPTY;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            if (maxDrain <= 0 || storedFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            
            return new FluidStack(storedFluid.getFluid(), maxDrain);
        }
    }
}

