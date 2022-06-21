package net.flytre.aim_plus;

import net.flytre.flytre_lib.api.base.util.KeyBindUtils;
import net.flytre.flytre_lib.api.config.ConfigUtils;
import net.flytre.flytre_lib.api.event.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Key {
    public static KeyBinding TOGGLE_ENABLED;
    public static KeyBinding TOGGLER_SNIPER_MODE;
    public static KeyBinding OPEN_CONFIG;

    public static void init() {
        TOGGLE_ENABLED = KeyBindUtils.register(new KeyBinding(
                "key.aim_plus.toggle_enabled",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.aim_plus.main"
        ));
        TOGGLER_SNIPER_MODE = KeyBindUtils.register(new KeyBinding(
                "key.aim_plus.toggle_sniper",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.aim_plus.main"
        ));
        OPEN_CONFIG = KeyBindUtils.register(new KeyBinding(
                "key.aim_plus.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                "category.aim_plus.main"
        ));
    }

    public static void keyBindCode() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_ENABLED.wasPressed()) {
                AimPlus.CONFIG.getConfig().enabled = !AimPlus.CONFIG.getConfig().enabled;
                String msg = AimPlus.CONFIG.getConfig().enabled ? "§aTrue" : "§cFalse";
                assert client.player != null;
                client.player.sendMessage(Text.of("§2AimPlus Enabled: " + msg), false);
                AimPlus.CONFIG.save(AimPlus.CONFIG.getConfig());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLER_SNIPER_MODE.wasPressed()) {
                AimPlus.CONFIG.getConfig().sniperMode = !AimPlus.CONFIG.getConfig().sniperMode;
                String msg = AimPlus.CONFIG.getConfig().sniperMode ? "§aTrue" : "§cFalse";
                assert client.player != null;
                client.player.sendMessage(Text.of("§2Sniper Mode Enabled: " + msg), false);
                AimPlus.CONFIG.save(AimPlus.CONFIG.getConfig());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_CONFIG.wasPressed()) {
                client.setScreen(ConfigUtils.createGui(null,null,AimPlus.CONFIG));
            }
        });

    }
}
