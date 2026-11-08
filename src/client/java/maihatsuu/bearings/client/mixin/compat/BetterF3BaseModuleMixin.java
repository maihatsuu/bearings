package maihatsuu.bearings.client.mixin.compat;

import maihatsuu.bearings.client.compat.BetterF3Gating;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.utils.DebugLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Lines are switched off only while BetterF3 formats them, then restored, because not every module
 * sets {@code active} again on its next update.
 */
@Mixin(value = BaseModule.class, remap = false)
public class BetterF3BaseModuleMixin {
	@Unique
	private final List<DebugLine> bearings$hiddenLines = new ArrayList<>();

	@Inject(method = "linesFormatted", at = @At("HEAD"))
	private void bearings$hideLines(boolean reducedDebug, CallbackInfoReturnable<?> cir) {
		BetterF3Gating.Rules rules = BetterF3Gating.currentRules();
		for (DebugLine line : ((BaseModule) (Object) this).lines()) {
			if (line.active && rules.hides((BaseModule) (Object) this, line.id())) {
				line.active = false;
				bearings$hiddenLines.add(line);
			}
		}
	}

	@Inject(method = "linesFormatted", at = @At("RETURN"))
	private void bearings$restoreLines(boolean reducedDebug, CallbackInfoReturnable<?> cir) {
		for (DebugLine line : bearings$hiddenLines) {
			line.active = true;
		}
		bearings$hiddenLines.clear();
	}
}
