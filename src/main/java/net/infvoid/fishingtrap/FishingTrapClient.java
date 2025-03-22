package net.infvoid.fishingtrap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.infvoid.fishingtrap.block.ModBlocks;


public class FishingTrapClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FISHING_TRAP, RenderLayer.getCutout());
    }
}
