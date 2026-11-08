package maihatsuu.bearings.client.compat;

import maihatsuu.bearings.client.DebugGating;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.modules.EmptyModule;
import me.cominixo.betterf3.modules.FpsModule;
import me.cominixo.betterf3.modules.HelpModule;
import me.cominixo.betterf3.modules.MinecraftModule;
import me.cominixo.betterf3.modules.MiscLeftModule;
import me.cominixo.betterf3.modules.MiscRightModule;
import me.cominixo.betterf3.modules.SystemModule;

import java.util.Set;

/** Maps BetterF3's line ids onto the same rules as vanilla's debug screen. */
public final class BetterF3Gating {
	private static final Set<String> COORDINATE_LINES = Set.of(
		"player_coords", "block_coords", "chunk_relative_coords", "chunk_coords",
		"facing", "rotation", "targeted_block", "targeted_fluid"
	);
	private static final Set<String> BASIC_COORDINATE_LINES = Set.of("player_coords", "facing", "rotation");
	private static final Set<String> TIME_LINES = Set.of("day_ticks", "days_played");

	private BetterF3Gating() {
	}

	/** Takes a snapshot of the rules, so a module checks the inventory once rather than once per line. */
	public static Rules currentRules() {
		return new Rules(DebugGating.coordinatesVisible(), DebugGating.timeVisible(), DebugGating.otherInfoHidden());
	}

	public record Rules(boolean coordinates, boolean time, boolean hideOtherInfo) {
		public boolean hides(BaseModule module, String lineId) {
			if (COORDINATE_LINES.contains(lineId)) {
				return !coordinates || (hideOtherInfo && !BASIC_COORDINATE_LINES.contains(lineId));
			}
			if (TIME_LINES.contains(lineId)) {
				return !time;
			}
			if (lineId.equals("biome")) {
				return hideOtherInfo && !coordinates;
			}
			return hideOtherInfo && !isBasic(module);
		}

		private static boolean isBasic(BaseModule module) {
			return module instanceof MinecraftModule || module instanceof FpsModule || module instanceof SystemModule
				|| module instanceof MiscLeftModule || module instanceof MiscRightModule
				|| module instanceof EmptyModule || module instanceof HelpModule;
		}
	}
}
