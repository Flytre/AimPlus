package net.flytre.aim_plus;

import net.fabricmc.api.ClientModInitializer;
import net.flytre.aim_plus.config.Config;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AimPlus implements ClientModInitializer {


    public static int sneakTime = 0;
    public static ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "aim_plus","config.aim_plus");

    @Override
    public void onInitializeClient() {


        Schedulers.everyMillisecond();

        ClientTickEvents.END_CLIENT_TICK.register(event -> {
            if (!CONFIG.getConfig().enabled)
                return;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null)
                return;
            Logic.updateTargetedEntity();
            Schedulers.startClientTick();

            if (player.isSneaking())
                sneakTime++;
            else
                sneakTime = 0;
        });
        ConfigRegistry.registerClientConfig(CONFIG);
        CONFIG.handle();

        Key.init();
        Key.keyBindCode();
    }
}
