package dev.polaris_light.supersourceblock.block.entity;

import dev.polaris_light.supersourceblock.block.ModBlocks;
import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import dev.polaris_light.supersourceblock.data.FluidSourceRule;
import dev.polaris_light.supersourceblock.data.SourceRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyFluidSourceBlockEntity extends BlockEntity implements IFluidHandler {
    private static final String STORED_FLUID_KEY = "StoredFluid";
    private FluidStack storedFluid = FluidStack.EMPTY;

    public EmptyFluidSourceBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EMPTY_FLUID_SOURCE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public FluidStack getStoredFluid() {
        return this.storedFluid;
    }

    public int getRequiredMb() {
        return SuperSourceConfig.requiredMb();
    }

    private int getRemainingCapacity() {
        return Math.max(0, getRequiredMb() - this.storedFluid.getAmount());
    }

    private void setStoredFluid(FluidStack stack) {
        this.storedFluid = stack;
        setChangedAndSync();
    }

    private void setChangedAndSync() {
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void transformIntoSuperSource(FluidSourceRule rule) {
        if (this.level == null || this.level.isClientSide || this.storedFluid.isEmpty()) {
            return;
        }
        BlockPos pos = getBlockPos();
        this.level.setBlockAndUpdate(pos, ModBlocks.SUPER_FLUID_SOURCE_BLOCK.get().defaultBlockState());
        if (this.level.getBlockEntity(pos) instanceof SuperFluidSourceBlockEntity superEntity) {
            superEntity.setStoredFluid(this.storedFluid.copyWithAmount(Integer.MAX_VALUE));
            superEntity.setCustomOutputAmount(resolveOutputAmount(rule));
            superEntity.setCustomIntervalTicks(resolveIntervalTicks(rule));
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidStack.EMPTY;
        }
        return this.storedFluid.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? getRequiredMb() : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (tank != 0 || stack.isEmpty()) {
            return false;
        }
        return this.storedFluid.isEmpty() || this.storedFluid.is(stack.getFluid());
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource)) {
            return 0;
        }
        int accepted = Math.min(resource.getAmount(), getRemainingCapacity());
        if (accepted <= 0) {
            return 0;
        }
        if (action.execute()) {
            if (this.storedFluid.isEmpty()) {
                setStoredFluid(resource.copyWithAmount(accepted));
            } else {
                setStoredFluid(this.storedFluid.copyWithAmount(this.storedFluid.getAmount() + accepted));
            }
            FluidSourceRule matchedRule = getMatchedRule();
            if (shouldTransform(matchedRule)) {
                transformIntoSuperSource(matchedRule);
            }
        }
        return accepted;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || this.storedFluid.isEmpty() || !this.storedFluid.is(resource.getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || this.storedFluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        int drainedAmount = Math.min(maxDrain, this.storedFluid.getAmount());
        FluidStack drained = this.storedFluid.copyWithAmount(drainedAmount);
        if (action.execute()) {
            int remain = this.storedFluid.getAmount() - drainedAmount;
            setStoredFluid(remain <= 0 ? FluidStack.EMPTY : this.storedFluid.copyWithAmount(remain));
        }
        return drained;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.storedFluid.isEmpty()) {
            tag.put(STORED_FLUID_KEY, this.storedFluid.save(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(STORED_FLUID_KEY)) {
            this.storedFluid = FluidStack.parse(registries, tag.getCompound(STORED_FLUID_KEY)).orElse(FluidStack.EMPTY);
        } else {
            this.storedFluid = FluidStack.EMPTY;
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

    private FluidSourceRule getMatchedRule() {
        if (this.storedFluid.isEmpty() || SuperSourceConfig.allowAnyFluidSource()) {
            return null;
        }
        return SourceRecipeManager.INSTANCE.findFluidRule(this.storedFluid, this.storedFluid.getAmount());
    }

    private boolean shouldTransform(FluidSourceRule matchedRule) {
        if (this.storedFluid.isEmpty()) {
            return false;
        }
        if (SuperSourceConfig.allowAnyFluidSource()) {
            return this.storedFluid.getAmount() >= getRequiredMb();
        }
        return matchedRule != null;
    }

    private int resolveOutputAmount(FluidSourceRule rule) {
        boolean useMax = rule != null && rule.maxOutput() != null ? rule.maxOutput() : SuperSourceConfig.superFluidUseIntegerMaxOutput();
        if (useMax) {
            return Integer.MAX_VALUE;
        }
        if (rule != null && rule.outputAmount() != null) {
            return Math.max(1, rule.outputAmount());
        }
        return SuperSourceConfig.superFluidOutputAmount();
    }

    private int resolveIntervalTicks(FluidSourceRule rule) {
        if (rule != null && rule.interval() != null) {
            return Math.max(1, rule.interval());
        }
        return SuperSourceConfig.superFluidOutputIntervalTicks();
    }

    public IFluidHandler createFluidHandler() {
        return this;
    }
}
