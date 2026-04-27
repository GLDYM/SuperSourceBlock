package dev.polaris_light.supersourceblock.block;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.supersourceblock.block.entity.EmptyFluidSourceBlockEntity;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmptyFluidSourceBlock extends BaseEntityBlock {
    public static final MapCodec<EmptyFluidSourceBlock> CODEC = simpleCodec(EmptyFluidSourceBlock::new);

    public EmptyFluidSourceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
        @NotNull ItemStack stack,
        @NotNull BlockState state,
        @NotNull Level level,
        @NotNull BlockPos pos,
        @NotNull Player player,
        @NotNull InteractionHand hand,
        @NotNull BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof EmptyFluidSourceBlockEntity blockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        Item item = stack.getItem();
        if (item instanceof BucketItem bucketItem) {
            Fluid fluid = bucketItem.content;
            if (fluid != Fluids.EMPTY) {
                int accepted = blockEntity.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                if (accepted == 1000) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        player.addItem(new ItemStack(Items.BUCKET));
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return ItemInteractionResult.FAIL;
            }
        }

        if (stack.is(Items.BUCKET)) {
            FluidStack drained = blockEntity.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            if (drained.getAmount() == 1000 && drained.getFluid().getBucket() != Items.AIR) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                    player.addItem(new ItemStack(drained.getFluid().getBucket()));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EmptyFluidSourceBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        @NotNull BlockState state,
        @NotNull BlockEntityType<T> blockEntityType
    ) {
        return null;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof EmptyFluidSourceBlockEntity emptySourceBlockEntity && builder.getLevel() != null) {
            emptySourceBlockEntity.saveToItem(stack, builder.getLevel().registryAccess());
        }
        return List.of(stack);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(
        BlockState state,
        HitResult target,
        LevelReader level,
        @NotNull BlockPos pos,
        @NotNull Player player
    ) {
        ItemStack stack = new ItemStack(ModItems.EMPTY_FLUID_SOURCE_BLOCK.get());
        if (level instanceof Level realLevel && realLevel.getBlockEntity(pos) instanceof EmptyFluidSourceBlockEntity blockEntity) {
            blockEntity.saveToItem(stack, realLevel.registryAccess());
        }
        return stack;
    }

}
