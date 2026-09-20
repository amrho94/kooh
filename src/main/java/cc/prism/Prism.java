package cc.prism;

import cc.prism.config.ConfigManager;
import cc.prism.event.EventBus;
import cc.prism.module.ModuleManager;
import cc.prism.ui.clickgui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prism implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("prism");

    public static EventBus EVENT_BUS;
    public static ModuleManager MODULE_MANAGER;
    public static ConfigManager CONFIG_MANAGER;

    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Prism Client...");

        EVENT_BUS = new EventBus();
        MODULE_MANAGER = new ModuleManager();
        CONFIG_MANAGER = new ConfigManager();

        // Register ClickGUI keybind (Right Shift)
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.prism.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.prism"
        ));

        // Module keybinds + ClickGUI open
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // ClickGUI open
            while (guiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }

            // Module keybinds (handled by input events from module manager)
        });

        // Load default config
        try {
            CONFIG_MANAGER.load("default");
            LOGGER.info("Loaded default config.");
        } catch (Exception e) {
            LOGGER.warn("No default config found, starting fresh.");
        }

        LOGGER.info("Prism Client initialized. {} modules loaded.", MODULE_MANAGER.getModules().size());
    }

    /** Convenience getter for accessing the client instance statically. */
    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
