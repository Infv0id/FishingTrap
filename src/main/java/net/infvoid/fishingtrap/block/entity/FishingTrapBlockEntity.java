package net.infvoid.fishingtrap.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.infvoid.fishingtrap.block.ModBlockEntities;
import net.infvoid.fishingtrap.screen.FishingTrapScreenHandler;
import net.infvoid.fishingtrap.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FishingTrapBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6  , ItemStack.EMPTY); // One bait slot

    public FishingTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISHING_TRAP_BLOCK_ENTITY, pos, state);
    }

    // Called every tick
    public static void tick(World world, BlockPos pos, BlockState state, FishingTrapBlockEntity entity) {
        if (world.isClient()) return;

        ItemStack bait = entity.getStack(0);

        if (!bait.isEmpty() && (bait.isOf(Items.ROTTEN_FLESH) || bait.isOf(Items.SPIDER_EYE))) {
            if (world.getTime() % 100 == 0) { // Every 5 seconds
                bait.decrement(1);
                entity.setStack(0, bait.copy());

                ItemStack fish = new ItemStack(Items.COD); // You can replace with loot table logic
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, fish));

                entity.markDirty(); // Save to disk
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        super.writeNbt(nbt, registryLookup);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }





    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FishingTrapScreenHandler(syncId, playerInventory, this.getPos());
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Fishing Trap");
    }


    @Override
    public Object getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.getPos();
    }
}


