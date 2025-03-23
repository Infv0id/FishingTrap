package net.infvoid.fishingtrap.screen;

import net.infvoid.fishingtrap.block.entity.FishingTrapBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class FishingTrapScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public FishingTrapScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(ModScreenHandlers.FISHING_TRAP_SCREEN_HANDLER, syncId);

        if (!(playerInventory.player.getWorld().getBlockEntity(pos) instanceof FishingTrapBlockEntity blockEntity)) {
            throw new IllegalStateException("Block at position " + pos + " is not a FishingTrapBlockEntity!");
        }

        this.inventory = blockEntity;

        // Bait Slot (centered)
        this.addSlot(new Slot(inventory, 0, 81, 15)); // SLOT 0

        // 9 fish output slots (slots 1–9) in a horizontal row
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i + 1, 8 + i * 18, 48));
        }


        // Add player inventory (3 rows of 9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Add player hotbar (9 slots)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack newStack = slot.getStack();
            ItemStack originalStack = newStack.copy();

            if (index < 6) {
                // from trap to player
                if (!this.insertItem(newStack, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // from player to trap
                if (!this.insertItem(newStack, 0, 6, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (newStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            return originalStack;
        }

        return ItemStack.EMPTY;
    }
}