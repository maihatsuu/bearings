package maihatsuu.bearings.client.mixin;

import maihatsuu.bearings.client.DebugGating;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	/** F3+C copies the player's location, and F3+I copies a command containing the target's position. */
	@Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
	private void bearings$blockLocationCopy(int key, CallbackInfoReturnable<Boolean> cir) {
		if ((key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_I) && !DebugGating.coordinatesVisible()) {
			cir.setReturnValue(false);
		}
	}
}
