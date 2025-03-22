package net.infvoid.fishingtrap;

import net.fabricmc.api.ModInitializer;

import net.infvoid.fishingtrap.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingTrap implements ModInitializer {
	public static final String MOD_ID = "fishingtrap";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlocks.registerModBlocks();


	}
}