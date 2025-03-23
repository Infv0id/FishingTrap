package net.infvoid.fishingtrap.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.infvoid.fishingtrap.block.ModBlockEntities;
import net.infvoid.fishingtrap.screen.FishingTrapScreenHandler;
import net.infvoid.fishingtrap.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FishingTrapBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private static boolean isValidBait(ItemStack bait) {
        return VALID_BAIT.contains(bait.getItem());
    }


    private int fishingCooldown = getNewCooldown(); // ticks left until next catch

    private static int getNewCooldown() {
        return 600 + new Random().nextInt(1801); // 600 to 2400 ticks (30s to 2min)
    }


    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY); // 0 = bait, 1-9 = fish

    private static final Set<Item> VALID_BAIT = Set.of(
            Items.ROTTEN_FLESH, Items.SWEET_BERRIES, Items.CARROT,
            Items.BEETROOT, Items.MELON_SLICE, Items.BREAD
    );

    private static final List<Item> POSSIBLE_FISH = new ArrayList<>();

    static {
        for (int i = 0; i < 96; i++) {
            POSSIBLE_FISH.add(Items.COD);
            POSSIBLE_FISH.add(Items.SALMON);
        }
        for (int i = 0; i < 1; i++) {
            POSSIBLE_FISH.add(Items.TROPICAL_FISH);
        }
    }



    public FishingTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISHING_TRAP_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FishingTrapBlockEntity entity) {
        if (world.isClient()) return;

        ItemStack bait = entity.getStack(0);

        if (!bait.isEmpty() && isValidBait(bait)) {
            entity.fishingCooldown--;

            if (entity.fishingCooldown <= 0) {
                ItemStack fish = new ItemStack(POSSIBLE_FISH.get(world.getRandom().nextInt(POSSIBLE_FISH.size())));
                boolean inserted = false;

                for (int i = 1; i <= 9; i++) {
                    ItemStack slot = entity.getStack(i);

                    if (slot.isEmpty()) {
                        entity.setStack(i, fish.copy());
                        inserted = true;
                        break;
                    }

                    if (slot.getItem() == fish.getItem() && slot.getCount() < slot.getMaxCount()) {
                        slot.increment(1);
                        inserted = true;
                        break;
                    }
                }

                if (inserted) {
                    bait.decrement(1);
                    entity.setStack(0, bait);
                    entity.fishingCooldown = getNewCooldown(); // 🎣 Reset timer!
                    entity.markDirty();
                }
            }
        }

    }

    @Override
    public void readNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
        fishingCooldown = nbt.getInt("FishingCooldown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("FishingCooldown", fishingCooldown);

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
    public Object getScreenOpeningData(ServerPlayerEntity player) {
        return this.getPos();
    }
}
