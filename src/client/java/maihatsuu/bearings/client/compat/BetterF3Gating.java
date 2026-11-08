package maihatsuu.bearings.client.compat;

import maihatsuu.bearings.client.BearingsClient;
import maihatsuu.bearings.client.DebugGating;

import java.util.Set;

/** Maps BetterF3's line ids onto the same rules as vanilla's debug screen. */
public final class BetterF3Gating {
	private static final Set<String> COORDINATE_LINES = Set.of(
		"player_coords", "block_coords", "chunk_relative_coords", "chunk_coords",
		"facing", "rotation", "targeted_block", "targeted_fluid"
	);
	private static final Set<String> TIME_LINES = Set.of("day_ticks", "days_played");
	private static final Set<String> OTHER_INFO_LINES = Set.of(
		"light", "light_server", "highest_block", "highest_block_server", "biome", "local_difficulty", "slime_chunk"
	);

	private BetterF3Gating() {
	}

	/** Takes a snapshot of the rules, so a module checks the inventory once rather than once per line. */
	public static Rules currentRules() {
		return new Rules(DebugGating.coordinatesVisible(), DebugGating.timeVisible(), BearingsClient.config.hideOtherInfo);
	}

	public record Rules(boolean coordinates, boolean time, boolean hideOtherInfo) {
		public boolean hides(String lineId) {
			return (!coordinates && COORDINATE_LINES.contains(lineId))
				|| (!time && TIME_LINES.contains(lineId))
				|| (hideOtherInfo && OTHER_INFO_LINES.contains(lineId));
		}
	}
}
