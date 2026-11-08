package maihatsuu.bearings.client;

import net.fabricmc.api.ClientModInitializer;

public class BearingsClient implements ClientModInitializer {
	public static BearingsConfig config = new BearingsConfig();

	@Override
	public void onInitializeClient() {
		config = BearingsConfig.load();
	}
}