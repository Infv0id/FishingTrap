package net.infvoid.fishingtrap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.infvoid.fishingtrap.screen.FishingTrapScreen;
import net.infvoid.fishingtrap.screen.ModScreenHandlers;
import net.minecraft.client.render.RenderLayer;
import net.infvoid.fishingtrap.block.ModBlocks;
import net.minecraft.client.gui.screen.ingame.HandledScreens;



public class FishingTrapClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FISHING_TRAP, RenderLayer.getCutout());
        HandledScreens.register(
                ModScreenHandlers.FISHING_TRAP_SCREEN_HANDLER,
                FishingTrapScreen::new // ✅ constructor reference instead of lambda
        );
    }
}
