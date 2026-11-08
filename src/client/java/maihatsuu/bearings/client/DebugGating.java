package maihatsuu.bearings.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

import java.util.List;
import java.util.Locale;

/** Decides what the player may see on the debug screen, and edits vanilla's lines to match. */
public final class DebugGating {
	private static final List<String> COORDINATE_LINES = List.of(
		"XYZ: ", "Block: ", "Chunk: ", "Facing: ", "Chunk-relative: ",
		ChatFormatting.UNDERLINE + "Targeted Block: ",
		ChatFormatting.UNDERLINE + "Targeted Fluid: "
	);
	private static final String TARGETED_SECTION = ChatFormatting.UNDERLINE + "Targeted ";
	private static final List<String> ITEM_LINES = List.of("XYZ: ", "Facing: ", "Time: ", "Last Death: ");

	private DebugGating() {
	}

	/** Permission level 2 covers server operators and singleplayer worlds with cheats enabled. */
	private static boolean isExempt(LocalPlayer player) {
		BearingsConfig config = BearingsClient.config;
		return config.opsSeeEverything && player != null && player.hasPermissions(2)
			&& (!config.opsOnlyInCreative || player.isCreative());
	}

	public static boolean coordinatesVisible() {
		LocalPlayer player = Minecraft.getInstance().player;
		return !BearingsClient.config.requireCompassForCoordinates || isExempt(player)
			|| (player != null && Instrument.COMPASS.isCarriedBy(player));
	}

	public static boolean timeVisible() {
		LocalPlayer player = Minecraft.getInstance().player;
		return !BearingsClient.config.requireClockForTime || isExempt(player)
			|| (player != null && Instrument.CLOCK.isCarriedBy(player));
	}

	public static boolean deathLocationVisible() {
		LocalPlayer player = Minecraft.getInstance().player;
		return BearingsClient.config.showDeathLocation
			&& player != null && (isExempt(player) || Instrument.RECOVERY_COMPASS.isCarriedBy(player));
	}

	public static boolean otherInfoHidden() {
		return BearingsClient.config.onlyBasicInfo && !isExempt(Minecraft.getInstance().player);
	}

	public static void filterGameInformation(List<String> lines) {
		boolean showTime = timeVisible();

		// "Facing" is absent on the reduced debug screen, which should stay reduced.
		int facing = indexOfPrefix(lines, "Facing: ");
		if (facing >= 0) {
			int insertAt = facing + 1;
			if (showTime) {
				lines.add(insertAt++, timeLine());
			}
			if (deathLocationVisible()) {
				String death = deathLine();
				if (death != null) {
					lines.add(insertAt, death);
				}
			}
		}

		filterPosition(lines);

		if (!showTime) {
			lines.replaceAll(line -> line.startsWith("Local Difficulty: ") ? line.replaceFirst(" \\(Day \\d+\\)$", "") : line);
		}
		if (otherInfoHidden()) {
			keepBasicGameInformation(lines);
		}
	}

	private static void keepBasicGameInformation(List<String> lines) {
		boolean showBiome = coordinatesVisible();
		lines.removeIf(line -> !line.startsWith("Minecraft ") && !line.matches("\\d+ fps.*")
			&& !startsWithAny(line, ITEM_LINES) && !(showBiome && line.startsWith("Biome: ")));
		if (lines.size() > 2) {
			lines.add(2, "");
		}
	}

	public static void filterSystemInformation(List<String> lines) {
		if (otherInfoHidden()) {
			int firstTarget = indexOfPrefix(lines, TARGETED_SECTION);
			if (firstTarget >= 0) {
				// Also removes the blank line above the first section.
				lines.subList(firstTarget - 1, lines.size()).clear();
			}
		} else {
			filterPosition(lines);
		}
	}

	private static void filterPosition(List<String> lines) {
		if (!coordinatesVisible()) {
			lines.removeIf(line -> startsWithAny(line, COORDINATE_LINES));
		}
	}

	private static String timeLine() {
		ClientLevel level = Minecraft.getInstance().level;
		long dayTime = level.getDayTime();
		long ticksIntoDay = dayTime % 24000L;
		// Tick 0 of a Minecraft day is 06:00.
		long hours = (ticksIntoDay / 1000L + 6L) % 24L;
		long minutes = (ticksIntoDay % 1000L) * 60L / 1000L;
		return String.format(Locale.ROOT, "Time: %02d:%02d (Day %d)", hours, minutes, dayTime / 24000L);
	}

	private static String deathLine() {
		return Minecraft.getInstance().player.getLastDeathLocation().map(DebugGating::formatDeath).orElse(null);
	}

	private static String formatDeath(GlobalPos death) {
		BlockPos pos = death.pos();
		return String.format(Locale.ROOT, "Last Death: %s %d %d %d", death.dimension().location(), pos.getX(), pos.getY(), pos.getZ());
	}

	private static int indexOfPrefix(List<String> lines, String prefix) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith(prefix)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean startsWithAny(String line, List<String> prefixes) {
		for (String prefix : prefixes) {
			if (line.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}
