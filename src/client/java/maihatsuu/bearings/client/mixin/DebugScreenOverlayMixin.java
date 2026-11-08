package maihatsuu.bearings.client.mixin;

import maihatsuu.bearings.client.DebugGating;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Shadow
	private boolean renderProfilerChart;
	@Shadow
	private boolean renderFpsCharts;
	@Shadow
	private boolean renderNetworkCharts;

	/** Also closes charts opened while the player still had permission. */
	@Inject(method = "render", at = @At("HEAD"))
	private void bearings$closeCharts(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (DebugGating.otherInfoHidden()) {
			renderProfilerChart = false;
			renderFpsCharts = false;
			renderNetworkCharts = false;
		}
	}

	@ModifyArg(
		method = "drawGameInformation",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"),
		index = 1
	)
	private List<String> bearings$removeChartHint(List<String> lines) {
		if (DebugGating.otherInfoHidden()) {
			lines.removeIf(line -> line.startsWith("Debug charts: "));
		}
		return lines;
	}

	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void bearings$filterGameInformation(CallbackInfoReturnable<List<String>> cir) {
		DebugGating.filterGameInformation(cir.getReturnValue());
	}

	@Inject(method = "getSystemInformation", at = @At("RETURN"))
	private void bearings$filterSystemInformation(CallbackInfoReturnable<List<String>> cir) {
		DebugGating.filterSystemInformation(cir.getReturnValue());
	}
}
