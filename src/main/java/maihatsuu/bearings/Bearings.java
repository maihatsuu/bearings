package maihatsuu.bearings;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bearings implements ModInitializer {
	public static final String MOD_ID = "bearings";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final TagKey<Item> SHOWS_COORDINATES = itemTag("shows_coordinates");
	public static final TagKey<Item> SHOWS_TIME = itemTag("shows_time");
	public static final TagKey<Item> SHOWS_DEATH_LOCATION = itemTag("shows_death_location");
	public static final TagKey<Item> SHOWS_BIOME = itemTag("shows_biome");

	@Override
	public void onInitialize() {
	}

	private static TagKey<Item> itemTag(String path) {
		return TagKey.create(Registries.ITEM, id(path));
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
