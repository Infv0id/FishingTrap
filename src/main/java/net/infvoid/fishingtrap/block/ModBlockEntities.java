package net.infvoid.fishingtrap.block;

import net.infvoid.fishingtrap.block.entity.FishingTrapBlockEntity;
import net.infvoid.fishingtrap.FishingTrap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class ModBlockEntities {

    public static BlockEntityType<FishingTrapBlockEntity> FISHING_TRAP_BLOCK_ENTITY;

    public static void registerModBlockEntities() {
        FISHING_TRAP_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                 Identifier.of(FishingTrap.MOD_ID, "fishing_trap"),
                FabricBlockEntityTypeBuilder
                        .create(FishingTrapBlockEntity::new, ModBlocks.FISHING_TRAP)
                        .build()

        );
    }
}
