package com.sourceblock.block;

import com.sourceblock.block.entity.CreativeSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 创造源方块 - 可以复制任何流体
 * 玩家手持流体桶或流体容器右键，方块会记住该流体并无限输出
 */
public class CreativeSourceBlock extends BaseEntityBlock {

    public CreativeSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CreativeSourceBlockEntity entity) {
            // 检查是否是桶
            if (stack.getItem() instanceof BucketItem) {
                Fluid fluid = ((BucketItem) stack.getItem()).getFluid();
                if (fluid != Fluids.EMPTY) {
                    entity.setFluid(new FluidStack(fluid, 1000));
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                            "message.sourceblock.fluid_set",
                            fluid.getFluidType().getDescription()
                        ),
                        true
                    );
                    return InteractionResult.SUCCESS;
                }
            }
            
            // 检查是否是流体容器（如储罐等）
            var fluidHandlerOpt = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            if (fluidHandlerOpt.isPresent()) {
                var fluidHandler = fluidHandlerOpt.orElse(null);
                if (fluidHandler != null && fluidHandler.getTanks() > 0) {
                    FluidStack fluidInContainer = fluidHandler.getFluidInTank(0);
                    if (!fluidInContainer.isEmpty()) {
                        entity.setFluid(fluidInContainer.copy());
                        player.displayClientMessage(
                            net.minecraft.network.chat.Component.translatable(
                                "message.sourceblock.fluid_set",
                                fluidInContainer.getDisplayName()
                            ),
                            true
                        );
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            
            // 空手Shift右键清空
            if (stack.isEmpty() && player.isShiftKeyDown()) {
                entity.clearFluid();
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.sourceblock.fluid_cleared"),
                    true
                );
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CreativeSourceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, 
            com.sourceblock.block.entity.ModBlockEntities.CREATIVE_SOURCE_BLOCK_ENTITY.get(), 
            CreativeSourceBlockEntity::serverTick);
    }
}

