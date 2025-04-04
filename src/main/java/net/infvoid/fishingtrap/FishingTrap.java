package net.infvoid.fishingtrap;

import net.fabricmc.api.ModInitializer;
import net.infvoid.fishingtrap.block.ModBlockEntities;
import net.infvoid.fishingtrap.block.ModBlocks;
import net.infvoid.fishingtrap.screen.ModScreenHandlers;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingTrap implements ModInitializer {
	public static final String MOD_ID = "fishingtrap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();
		registerRecipes();
	}

	private void registerRecipes() {
		// Register the oxidized copper recipe with a ShapedRecipe.Serializer
		Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "fishing_trap_oxidized"), RecipeSerializer.SHAPED);

		// Register the waxed oxidized copper recipe with a ShapedRecipe.Serializer
		Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "fishing_trap_waxed_oxidized"), RecipeSerializer.SHAPED);

		LOGGER.info("Fishing Trap recipes registered successfully!");
	}
}
