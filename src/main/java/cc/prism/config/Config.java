package cc.prism.config;

import cc.prism.Prism;
import cc.prism.module.Module;
import cc.prism.property.*;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Represents a single named config that stores module enabled states, keybinds,
 * and all registered property values. Serialized as JSON via Gson.
 *
 * File location: ~/.prism/configs/<name>.json
 */
public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String name;

    public Config(String name) {
        this.name = name;
    }

    // â”€â”€ Accessors â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String getName() {
        return name;
    }

    // â”€â”€ File path â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Path getFilePath() {
        return Paths.get(System.getProperty("user.home"), ".prism", "configs", name + ".json");
    }

    // â”€â”€ Save â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Serializes all modules from {@link Prism#MODULE_MANAGER} into JSON and
     * writes the result to the config file, creating parent directories as needed.
     */
    public void save() {
        JsonObject root = new JsonObject();
        JsonObject modulesObj = new JsonObject();

        for (Module module : Prism.MODULE_MANAGER.getModules()) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("enabled", module.isEnabled());
            moduleObj.addProperty("keybind", module.getKeybind());

            // Properties
            JsonObject propsObj = new JsonObject();
            for (Property<?> property : module.getProperties()) {
                String propName = property.getName();

                if (property instanceof BooleanProperty bp) {
                    propsObj.addProperty(propName, bp.getValue());
                } else if (property instanceof NumberProperty np) {
                    propsObj.addProperty(propName, np.getValue());
                } else if (property instanceof ModeProperty mp) {
                    propsObj.addProperty(propName, mp.getValue());
                } else if (property instanceof ColorProperty cp) {
                    propsObj.addProperty(propName, cp.getValue());
                }
            }
            moduleObj.add("properties", propsObj);
            modulesObj.add(module.getName(), moduleObj);
        }

        root.add("modules", modulesObj);

        try {
            Path filePath = getFilePath();
            Files.createDirectories(filePath.getParent());
            try (Writer writer = new OutputStreamWriter(
                    Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            System.err.println("[Prism] Failed to save config '" + name + "': " + e.getMessage());
        }
    }

    // â”€â”€ Load â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Reads the config JSON file and applies module enabled states, keybinds,
     * and property values to all registered modules.
     */
    public void load() {
        Path filePath = getFilePath();
        if (!Files.exists(filePath)) {
            System.err.println("[Prism] Config file not found: " + filePath);
            return;
        }

        try (Reader reader = new InputStreamReader(
                Files.newInputStream(filePath), StandardCharsets.UTF_8)) {

            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("modules")) return;

            JsonObject modulesObj = root.getAsJsonObject("modules");

            for (Module module : Prism.MODULE_MANAGER.getModules()) {
                if (!modulesObj.has(module.getName())) continue;

                JsonObject moduleObj = modulesObj.getAsJsonObject(module.getName());

                // enabled state
                if (moduleObj.has("enabled")) {
                    boolean shouldEnable = moduleObj.get("enabled").getAsBoolean();
                    if (shouldEnable != module.isEnabled()) {
                        module.setEnabled(shouldEnable);
                    }
                }

                // keybind
                if (moduleObj.has("keybind")) {
                    module.setKeybind(moduleObj.get("keybind").getAsInt());
                }

                // properties
                if (!moduleObj.has("properties")) continue;
                JsonObject propsObj = moduleObj.getAsJsonObject("properties");

                for (Property<?> property : module.getProperties()) {
                    String propName = property.getName();
                    if (!propsObj.has(propName)) continue;

                    JsonElement element = propsObj.get(propName);
                    try {
                        if (property instanceof BooleanProperty bp) {
                            bp.setValue(element.getAsBoolean());
                        } else if (property instanceof NumberProperty np) {
                            np.setValue(element.getAsDouble());
                        } else if (property instanceof ModeProperty mp) {
                            mp.setValue(element.getAsString());
                        } else if (property instanceof ColorProperty cp) {
                            cp.setValue(element.getAsInt());
                        }
                    } catch (Exception e) {
                        System.err.println("[Prism] Failed to load property '" + propName
                                + "' for module '" + module.getName() + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Prism] Failed to load config '" + name + "': " + e.getMessage());
        }
    }
}






