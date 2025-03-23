package net.infvoid.fishingtrap.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.infvoid.fishingtrap.FishingTrap;
import net.infvoid.fishingtrap.screen.FishingTrapScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<FishingTrapScreenHandler> FISHING_TRAP_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(FishingTrap.MOD_ID, "fishing_trap_screen_handler"),
                    new ExtendedScreenHandlerType<>(FishingTrapScreenHandler::new, BlockPos.PACKET_CODEC)
            );

    public static void registerAllScreenHandlers() {
        FishingTrap.LOGGER.info("Registering Screen Handlers for " + FishingTrap.MOD_ID);
    }
}
