package dev.polaris_light.supersourceblock.block.entity;

import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperFluidSourceBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements IFluidHandler {
    private static final String STORED_FLUID_KEY = "StoredFluid";
    private static final String CUSTOM_OUTPUT_AMOUNT_KEY = "CustomOutputAmount";
    private static final String DIRECTION_CURSOR_KEY = "DirectionCursor";
    private static final String CUSTOM_INTERVAL_TICKS_KEY = "CustomIntervalTicks";
    private static final String TICK_COUNTER_KEY = "TickCounter";
    private static final Direction[] OUTPUT_ORDER = {
        Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN
    };

    private FluidStack storedFluid = FluidStack.EMPTY;
    private int customOutputAmount = -1;
    private int customIntervalTicks = -1;
    private int directionCursor = 0;
    private int tickCounter = 0;

    public SuperFluidSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SUPER_FLUID_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void setStoredFluid(FluidStack fluid) {
        this.storedFluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid.copyWithAmount(Integer.MAX_VALUE);
        setChangedAndSync();
    }

    public FluidStack getStoredFluid() {
        return this.storedFluid;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, SuperFluidSourceBlockEntity blockEntity) {
        if (level.isClientSide || blockEntity.storedFluid.isEmpty()) {
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
            int transferred = tryTransferFluid(level, pos.relative(direction), direction.getOpposite(), blockEntity.storedFluid, remaining);
            remaining -= transferred;
        }
        blockEntity.directionCursor = (blockEntity.directionCursor + 1) % OUTPUT_ORDER.length;
    }

    private int getOutputAmount() {
        return this.customOutputAmount > 0 ? this.customOutputAmount : SuperSourceConfig.superFluidOutputAmount();
    }

    private int getIntervalTicks() {
        return this.customIntervalTicks > 0 ? this.customIntervalTicks : SuperSourceConfig.superFluidOutputIntervalTicks();
    }

    private static int tryTransferFluid(Level level, BlockPos targetPos, Direction side, FluidStack stored, int outputAmount) {
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, side);
        if (handler == null || stored.isEmpty()) {
            return 0;
        }
        FluidStack toTransfer = stored.copyWithAmount(outputAmount);
        return handler.fill(toTransfer, FluidAction.EXECUTE);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0 || this.storedFluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return this.storedFluid.copyWithAmount(Integer.MAX_VALUE);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? Integer.MAX_VALUE : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || this.storedFluid.isEmpty() || !this.storedFluid.is(resource.getFluid())) {
            return FluidStack.EMPTY;
        }
        return this.storedFluid.copyWithAmount(resource.getAmount());
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || this.storedFluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return this.storedFluid.copyWithAmount(maxDrain);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storedFluid.isEmpty()) {
            tag.put(STORED_FLUID_KEY, this.storedFluid.save(registries));
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
        if (tag.contains(STORED_FLUID_KEY)) {
            this.storedFluid = FluidStack.parse(registries, tag.getCompound(STORED_FLUID_KEY)).orElse(FluidStack.EMPTY);
            if (!this.storedFluid.isEmpty()) {
                this.storedFluid.setAmount(Integer.MAX_VALUE);
            }
        } else {
            this.storedFluid = FluidStack.EMPTY;
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

    public IFluidHandler createFluidHandler() {
        return this;
    }
}
