package maihatsuu.bearings.client.compat;

import maihatsuu.bearings.client.BearingsClient;
import maihatsuu.bearings.client.BearingsConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Only load this class when Cloth Config is installed. */
public final class BearingsConfigScreen {
	private BearingsConfigScreen() {
	}

	public static Screen create(Screen parent) {
		BearingsConfig config = BearingsClient.config;
		BearingsConfig defaults = new BearingsConfig();

		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Component.translatable("config.bearings.title"))
			.setSavingRunnable(config::save);
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.bearings.general"));
		ConfigEntryBuilder entries = builder.entryBuilder();

		toggle(general, entries, "requireCompassForCoordinates", config.requireCompassForCoordinates,
			defaults.requireCompassForCoordinates, value -> config.requireCompassForCoordinates = value);
		toggle(general, entries, "requireClockForTime", config.requireClockForTime,
			defaults.requireClockForTime, value -> config.requireClockForTime = value);
		toggle(general, entries, "showDeathLocation", config.showDeathLocation,
			defaults.showDeathLocation, value -> config.showDeathLocation = value);
		toggle(general, entries, "onlyBasicInfo", config.onlyBasicInfo,
			defaults.onlyBasicInfo, value -> config.onlyBasicInfo = value);
		toggle(general, entries, "opsSeeEverything", config.opsSeeEverything,
			defaults.opsSeeEverything, value -> config.opsSeeEverything = value);
		toggle(general, entries, "opsOnlyInCreative", config.opsOnlyInCreative,
			defaults.opsOnlyInCreative, value -> config.opsOnlyInCreative = value);

		return builder.build();
	}

	private static void toggle(ConfigCategory category, ConfigEntryBuilder entries, String key, boolean value,
			boolean defaultValue, Consumer<Boolean> saver) {
		category.addEntry(entries.startBooleanToggle(Component.translatable("config.bearings." + key), value)
			.setDefaultValue(defaultValue)
			.setTooltip(Component.translatable("config.bearings." + key + ".tooltip"))
			.setSaveConsumer(saver)
			.build());
	}
}
