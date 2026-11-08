package maihatsuu.bearings.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import maihatsuu.bearings.Bearings;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Client settings, stored in {@code config/bearings.json}. */
public final class BearingsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public boolean requireCompassForCoordinates = true;
	public boolean requireClockForTime = true;
	public boolean showDeathLocation = true;
	public boolean onlyBasicInfo = true;
	public boolean opsSeeEverything = true;
	public boolean opsOnlyInCreative = false;

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("bearings.json");
	}

	/** Reads the config, then writes it back so new options appear in the file. */
	public static BearingsConfig load() {
		Path path = path();
		BearingsConfig config = null;

		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				config = GSON.fromJson(reader, BearingsConfig.class);
			} catch (IOException | JsonParseException e) {
				// Keep the broken file so the player's edits aren't overwritten by defaults.
				Bearings.LOGGER.error("Could not read {}, using default settings", path, e);
				return new BearingsConfig();
			}
		}

		if (config == null) {
			config = new BearingsConfig();
		}

		config.save();
		return config;
	}

	public void save() {
		Path path = path();
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			Bearings.LOGGER.error("Could not write {}", path, e);
		}
	}
}
