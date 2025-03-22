package net.infvoid.fishingtrap.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.infvoid.fishingtrap.FishingTrap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block FISHING_TRAP = registerBlock("fishing_trap",new Block(AbstractBlock.Settings.create()
            .strength(2.0f)
            .nonOpaque()
            .sounds(BlockSoundGroup.WOOD)


    ));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(FishingTrap.MOD_ID, name), block);
    }



    private static void registerBlockItem(String name, Block block) {

        Registry.register(Registries.ITEM, Identifier.of(FishingTrap.MOD_ID, name),
        new BlockItem(block, new Item.Settings()));
    }




    public static void registerModBlocks() {

        FishingTrap.LOGGER.info("Registering ModBlocks for " + FishingTrap.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((entries) -> {
            entries.add(ModBlocks.FISHING_TRAP);
        });




    }

}
