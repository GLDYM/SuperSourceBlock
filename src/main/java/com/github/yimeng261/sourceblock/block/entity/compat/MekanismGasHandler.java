package com.github.yimeng261.sourceblock.block.entity.compat;

import com.github.yimeng261.sourceblock.block.SourceBlock;
import com.github.yimeng261.sourceblock.block.entity.SourceBlockEntity;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Mekanism气体处理实现
 * 空源方块可以销毁所有输入的气体
 */
public class MekanismGasHandler implements IGasHandler {
    
    private final SourceBlockEntity blockEntity;
    
    public MekanismGasHandler(SourceBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
    
    /**
     * 检查是否是空源方块（可以销毁化学物质）
     */
    private boolean isVoidMode() {
        if (blockEntity.getLevel() == null) return false;
        
        BlockState state = blockEntity.getBlockState();
        SourceBlock.FluidType fluidType = state.getValue(SourceBlock.FLUID_TYPE);
        
        return fluidType == SourceBlock.FluidType.EMPTY;
    }
    
    @Override
    public int getTanks() {
        return isVoidMode() ? 1 : 0;
    }
    
    @NotNull
    @Override
    public GasStack getChemicalInTank(int tank) {
        // 虚空方块不存储气体
        return GasStack.EMPTY;
    }
    
    @Override
    public void setChemicalInTank(int tank, @NotNull GasStack stack) {
        // 不支持设置
    }
    
    @Override
    public long getTankCapacity(int tank) {
        // 返回最大容量表示可以接受任意数量的气体
        return isVoidMode() ? Long.MAX_VALUE : 0;
    }
    
    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        // 空源方块接受所有气体用于销毁
        return isVoidMode();
    }
    
    @NotNull
    @Override
    public GasStack insertChemical(int tank, @NotNull GasStack stack, Action action) {
        if (!isVoidMode() || stack.isEmpty()) {
            return stack;
        }
        
        // 销毁所有输入的气体（返回空表示全部接受）
        return GasStack.EMPTY;
    }
    
    @NotNull
    @Override
    public GasStack extractChemical(int tank, long amount, Action action) {
        // 虚空方块不提供气体输出
        return GasStack.EMPTY;
    }

    @NotNull
    @Override
    public GasStack insertChemical(@NotNull GasStack stack, Action action) {
        if (!isVoidMode() || stack.isEmpty()) {
            return stack;
        }
        
        // 销毁所有输入的气体（返回空表示全部接受）
        return GasStack.EMPTY;
    }

    @NotNull
    @Override
    public GasStack extractChemical(long amount, Action action) {
        // 虚空方块不提供气体输出
        return GasStack.EMPTY;
    }

    @NotNull
    @Override
    public GasStack getEmptyStack() {
        return GasStack.EMPTY;
    }
}

