package dev.polaris_light.supersourceblock.block;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.supersourceblock.block.entity.EmptyItemSourceBlockEntity;
import dev.polaris_light.supersourceblock.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmptyItemSourceBlock extends BaseEntityBlock {
    public static final MapCodec<EmptyItemSourceBlock> CODEC = simpleCodec(EmptyItemSourceBlock::new);

    public EmptyItemSourceBlock(BlockBehaviour.Properties properties) {
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
        if (!(level.getBlockEntity(pos) instanceof EmptyItemSourceBlockEntity blockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!stack.isEmpty()) {
            ItemStack input = stack.copy();
            ItemStack remainder = blockEntity.insertItem(0, input, false);
            if (remainder.getCount() < input.getCount()) {
                if (!player.isCreative()) {
                    player.setItemInHand(hand, remainder);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            ItemStack extracted = blockEntity.extractItem(0, 64, false);
            if (!extracted.isEmpty()) {
                if (!player.addItem(extracted)) {
                    player.drop(extracted, false);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EmptyItemSourceBlockEntity(pos, state);
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
        ItemStack stack = new ItemStack(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof EmptyItemSourceBlockEntity emptySourceBlockEntity && builder.getLevel() != null) {
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
        ItemStack stack = new ItemStack(ModItems.EMPTY_ITEM_SOURCE_BLOCK.get());
        if (level instanceof Level realLevel && realLevel.getBlockEntity(pos) instanceof EmptyItemSourceBlockEntity blockEntity) {
            blockEntity.saveToItem(stack, realLevel.registryAccess());
        }
        return stack;
    }
}

