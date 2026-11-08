package maihatsuu.bearings.client.mixin;

import maihatsuu.bearings.client.DebugGating;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void bearings$filterGameInformation(CallbackInfoReturnable<List<String>> cir) {
		DebugGating.filterGameInformation(cir.getReturnValue());
	}

	@Inject(method = "getSystemInformation", at = @At("RETURN"))
	private void bearings$filterSystemInformation(CallbackInfoReturnable<List<String>> cir) {
		DebugGating.filterSystemInformation(cir.getReturnValue());
	}
}
