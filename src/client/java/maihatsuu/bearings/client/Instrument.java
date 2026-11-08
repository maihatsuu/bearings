package maihatsuu.bearings.client;

import maihatsuu.bearings.Bearings;
import maihatsuu.bearings.client.compat.TrinketsCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/** An item type that unlocks information, carried in the inventory or worn on the belt. */
public enum Instrument {
	COMPASS(Bearings.SHOWS_COORDINATES, Items.COMPASS),
	CLOCK(Bearings.SHOWS_TIME, Items.CLOCK),
	RECOVERY_COMPASS(Bearings.SHOWS_DEATH_LOCATION, Items.RECOVERY_COMPASS),
	MAP(Bearings.SHOWS_BIOME, Items.MAP, Items.FILLED_MAP);

	private static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

	private final Predicate<ItemStack> matcher;

	Instrument(TagKey<Item> tag, Item... vanillaItems) {
		// Vanilla items are checked as well because a server without this mod doesn't send our tags.
		this.matcher = stack -> stack.is(tag) || Arrays.stream(vanillaItems).anyMatch(item -> stack.is(item));
	}

	public boolean isCarriedBy(LocalPlayer player) {
		Inventory inventory = player.getInventory();
		return anyMatch(inventory.items) || anyMatch(inventory.offhand)
			|| (TRINKETS_LOADED && TrinketsCompat.isWorn(player, matcher));
	}

	private boolean anyMatch(List<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (matcher.test(stack)) {
				return true;
			}
		}
		return false;
	}
}
