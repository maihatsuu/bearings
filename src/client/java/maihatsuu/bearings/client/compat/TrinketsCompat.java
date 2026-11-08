package maihatsuu.bearings.client.compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/** Only load this class when Trinkets is installed. */
public final class TrinketsCompat {
	private TrinketsCompat() {
	}

	public static boolean isWorn(LivingEntity entity, Predicate<ItemStack> matcher) {
		return TrinketsApi.getTrinketComponent(entity)
			.map(component -> component.isEquipped(matcher))
			.orElse(false);
	}
}
