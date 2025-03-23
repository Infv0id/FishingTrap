package net.infvoid.fishingtrap.block.custom;

import com.mojang.serialization.MapCodec;
import net.infvoid.fishingtrap.block.ModBlockEntities;
import net.infvoid.fishingtrap.block.entity.FishingTrapBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;



public class FishingTrapBlock extends BlockWithEntity implements Waterloggable {


    public static final MapCodec<FishingTrapBlock> CODEC = BlockWithEntity.createCodec(FishingTrapBlock::new);



    // ✅ Create block entity
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FishingTrapBlockEntity(pos, state);
    }

    // ✅ Ticking method with correct casting (1.21.1)
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (type == ModBlockEntities.FISHING_TRAP_BLOCK_ENTITY
                ? (BlockEntityTicker<T>) (world1, pos, blockState, blockEntity) -> FishingTrapBlockEntity.tick(world1, pos, blockState, (FishingTrapBlockEntity) blockEntity)
                : null);
    }


    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public FishingTrapBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, true));
    }


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }


    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    private boolean isUnderwater(BlockView world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        return fluidState.getFluid() == Fluids.WATER;
    }

    // Placement condition
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isUnderwater(world, pos);
    }

    @Override
    public boolean canReplace(BlockState state, net.minecraft.item.ItemPlacementContext context) {
        return isUnderwater(context.getWorld(), context.getBlockPos()) && super.canReplace(state, context);
    }

    // Handle neighbor block updates
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        world.scheduleBlockTick(pos, this, 1); // still break if water removed
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // Break if not underwater
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!isUnderwater(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    // Place waterlogged if underwater
    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean water = fluidState.getFluid() == Fluids.WATER;
        return this.getDefaultState().with(WATERLOGGED, water);
    }

    // Controls water rendering
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    // Register WATERLOGGED property
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }


    // Match model parts:
// - net_base: from [0, 0, 3] to [16, 10, 13]
// - handle:   from [4, 10, 7] to [12, 12, 9]
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 3, 16, 10, 13),
            Block.createCuboidShape(4, 10, 7, 12, 12, 9)
    );



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE; // what shows when hovering with the mouse
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE; // what the player collides with
    }

}
