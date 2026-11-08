package maihatsuu.bearings.client.mixin;

import maihatsuu.bearings.client.DebugGating;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	/** Vanilla's F3+Q list without the location, inspect, profiling and game mode entries. */
	@Unique
	private static final List<String> BEARINGS$BASIC_HELP = List.of(
		"debug.reload_chunks.help",
		"debug.show_hitboxes.help",
		"debug.clear_chat.help",
		"debug.chunk_boundaries.help",
		"debug.advanced_tooltips.help",
		"debug.pause_focus.help",
		"debug.help.help",
		"debug.dump_dynamic_textures.help",
		"debug.reload_resourcepacks.help",
		"debug.pause.help"
	);

	@Shadow
	protected abstract void debugFeedbackTranslated(String key, Object... args);

	@Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
	private void bearings$restrictDebugKeys(int key, CallbackInfoReturnable<Boolean> cir) {
		boolean basicOnly = DebugGating.otherInfoHidden();

		if (key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_I) {
			if (basicOnly || !DebugGating.coordinatesVisible()) {
				cir.setReturnValue(true);
			}
		} else if (basicOnly) {
			switch (key) {
				case GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3,
					GLFW.GLFW_KEY_L, GLFW.GLFW_KEY_N, GLFW.GLFW_KEY_F4 -> cir.setReturnValue(true);
				case GLFW.GLFW_KEY_Q -> {
					bearings$showBasicHelp();
					cir.setReturnValue(true);
				}
				default -> {
				}
			}
		}
	}

	@Unique
	private void bearings$showBasicHelp() {
		debugFeedbackTranslated("debug.help.message");
		ChatComponent chat = Minecraft.getInstance().gui.getChat();
		for (String key : BEARINGS$BASIC_HELP) {
			chat.addMessage(Component.translatable(key));
		}
	}
}
