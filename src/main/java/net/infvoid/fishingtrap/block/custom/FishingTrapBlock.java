package net.infvoid.fishingtrap.block.custom;

import com.mojang.serialization.MapCodec;
import net.infvoid.fishingtrap.block.ModBlockEntities;
import net.infvoid.fishingtrap.block.entity.FishingTrapBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;






public class FishingTrapBlock extends BlockWithEntity implements Waterloggable {



    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;



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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        // Trigger a recheck of support (chain above or solid block below)
        world.scheduleBlockTick(pos, this, 1);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }



    // Break if not underwater
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos belowPos = pos.down();
        BlockPos abovePos = pos.up();

        BlockState belowState = world.getBlockState(belowPos);
        BlockState aboveState = world.getBlockState(abovePos);

        boolean isWaterlogged = isUnderwater(world, pos);
        boolean hasSupportBelow = belowState.isOpaque(); // solid block
        boolean hasChainAbove = aboveState.isOf(Blocks.CHAIN); // hanging support

        if (!isWaterlogged || (!hasSupportBelow && !hasChainAbove)) {
            world.breakBlock(pos, true); // breaks and drops item
        }
    }



    // Place waterlogged if underwater
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean water = fluidState.getFluid() == Fluids.WATER;
        Direction playerFacing = ctx.getHorizontalPlayerFacing();
        return this.getDefaultState()
                .with(WATERLOGGED, water)
                .with(FACING, playerFacing.getOpposite());
    }

    // Controls water rendering
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    // Register WATERLOGGED property
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
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
        return rotateShape(state.get(FACING), SHAPE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return rotateShape(state.get(FACING), SHAPE);
    }



    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NamedScreenHandlerFactory screenHandlerFactory) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FishingTrapBlockEntity fishingTrapEntity) {
                // Drop all items in the inventory
                for (int i = 0; i < fishingTrapEntity.getItems().size(); i++) {
                    ItemStack stack = fishingTrapEntity.getStack(i);
                    if (!stack.isEmpty()) {
                        dropStack(world, pos, stack);
                    }
                }
                world.updateComparators(pos, this); // Optional: redstone comparator support
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    private static VoxelShape rotateShape(Direction direction, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    buffer[1] = VoxelShapes.union(
                            buffer[1],
                            VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
                    );
                });
                buffer[0] = buffer[1];
                buffer[1] = VoxelShapes.empty();
            }

            if (direction == Direction.fromHorizontal(i)) {
                return buffer[0];
            }
        }

        return shape;
    }


}