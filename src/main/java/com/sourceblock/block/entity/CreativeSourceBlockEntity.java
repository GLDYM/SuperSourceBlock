package com.sourceblock.block.entity;

import com.sourceblock.block.SourceBlock;
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

import static com.sourceblock.block.entity.SourceBlockEntity.tryTransferFluid;

/**
 * 创造源方块实体 - 无限提供指定的流体
 */
public class CreativeSourceBlockEntity extends BlockEntity {
    private FluidStack storedFluid = FluidStack.EMPTY;

    public static final int CAPACITY = Integer.MAX_VALUE;

    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(FluidHandlerImpl::new);

    public CreativeSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
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
        if (level.isClientSide) return;

        // 获取流体类型
        FluidStack fluidStack = blockEntity.storedFluid;
        if (fluidStack.isEmpty()) return;

        // 对每个面进行处理
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            tryTransferFluid(level, neighborPos, direction.getOpposite(), fluidStack);
        }

        blockEntity.setChanged();
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (!storedFluid.isEmpty()) {
            tag.put("StoredFluid", storedFluid.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        
        if (tag.contains("StoredFluid")) {
            this.storedFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("StoredFluid"));
        } else {
            this.storedFluid = FluidStack.EMPTY;
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

