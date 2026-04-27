package dev.polaris_light.supersourceblock.block;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.supersourceblock.block.entity.ModBlockEntities;
import dev.polaris_light.supersourceblock.block.entity.SuperFluidSourceBlockEntity;
import dev.polaris_light.supersourceblock.item.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuperFluidSourceBlock extends BaseEntityBlock {
    public static final MapCodec<SuperFluidSourceBlock> CODEC = simpleCodec(SuperFluidSourceBlock::new);

    public SuperFluidSourceBlock(BlockBehaviour.Properties properties) {
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
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SuperFluidSourceBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        @NotNull BlockState state,
        @NotNull BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.SUPER_FLUID_SOURCE_BLOCK_ENTITY.get(), SuperFluidSourceBlockEntity::serverTick);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof SuperFluidSourceBlockEntity superSourceBlockEntity && builder.getLevel() != null) {
            superSourceBlockEntity.saveToItem(stack, builder.getLevel().registryAccess());
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
        ItemStack stack = new ItemStack(ModItems.SUPER_FLUID_SOURCE_BLOCK.get());
        if (level instanceof Level realLevel && realLevel.getBlockEntity(pos) instanceof SuperFluidSourceBlockEntity blockEntity) {
            blockEntity.saveToItem(stack, realLevel.registryAccess());
        }
        return stack;
    }
}

