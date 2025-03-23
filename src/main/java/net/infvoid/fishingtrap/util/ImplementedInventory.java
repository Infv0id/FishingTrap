package net.infvoid.fishingtrap.util;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public interface ImplementedInventory extends SidedInventory {
    DefaultedList<ItemStack> getItems();

    @Override default int size() { return getItems().size(); }
    @Override default boolean isEmpty() { return getItems().stream().allMatch(ItemStack::isEmpty); }
    @Override default ItemStack getStack(int slot) { return getItems().get(slot); }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack stack = getStack(slot);
        if (!stack.isEmpty()) {
            result = stack.split(count);
            if (stack.isEmpty()) setStack(slot, ItemStack.EMPTY);
        }
        return result;
    }

    @Override default ItemStack removeStack(int slot) { return getItems().set(slot, ItemStack.EMPTY); }
    @Override default void setStack(int slot, ItemStack stack) { getItems().set(slot, stack); }
    @Override default void clear() { getItems().clear(); }
    @Override default boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) { return true; }
    @Override default int[] getAvailableSlots(Direction side) { return java.util.stream.IntStream.range(0, size()).toArray(); }
    @Override default boolean canInsert(int slot, ItemStack stack, Direction dir) { return true; }
    @Override default boolean canExtract(int slot, ItemStack stack, Direction dir) { return true; }
}
