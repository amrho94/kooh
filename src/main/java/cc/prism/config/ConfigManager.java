package cc.prism.config;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages multiple {@link Config} instances. Responsible for discovering
 * existing config files on disk, creating new ones on demand, and delegating
 * save/load operations.
 *
 * Config directory: ~/.prism/configs/
 */
public class ConfigManager {

    private static final Path CONFIGS_DIR =
            Paths.get(System.getProperty("user.home"), ".prism", "configs");

    private final List<Config> configs = new ArrayList<>();
    private String currentConfig = "default";

    // â”€â”€ Constructor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Scans the configs directory on disk and populates the {@link #configs} list.
     * Creates the directory if it does not yet exist.
     */
    public ConfigManager() {
        try {
            Files.createDirectories(CONFIGS_DIR);
        } catch (IOException e) {
            System.err.println("[Prism] Could not create configs directory: " + e.getMessage());
        }

        // Populate from existing files
        for (String configName : getConfigNames()) {
            configs.add(new Config(configName));
        }
    }

    // â”€â”€ Save / Load â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Saves the config with the given name. If no matching {@link Config} exists
     * in memory, a new one is created and added to the list.
     *
     * @param name config name (without .json extension)
     */
    public void save(String name) {
        Config config = findOrCreate(name);
        config.save();
        currentConfig = name;
    }

    /**
     * Loads the config with the given name if it exists in the managed list.
     *
     * @param name config name (without .json extension)
     */
    public void load(String name) {
        Config config = getConfig(name);
        if (config != null) {
            config.load();
            currentConfig = name;
        } else {
            System.err.println("[Prism] Config '" + name + "' not found.");
        }
    }

    // â”€â”€ File-level helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Returns all {@code .json} files present in the configs directory.
     *
     * @return list of {@link Path} objects for each config file
     */
    public List<Path> getConfigs() {
        try (Stream<Path> stream = Files.list(CONFIGS_DIR)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("[Prism] Could not list configs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns config names (file names without the {@code .json} extension).
     *
     * @return list of config name strings
     */
    public List<String> getConfigNames() {
        return getConfigs().stream()
                .map(p -> {
                    String fileName = p.getFileName().toString();
                    return fileName.substring(0, fileName.length() - ".json".length());
                })
                .collect(Collectors.toList());
    }

    /**
     * Deletes the config file for the given name from disk and removes it from
     * the in-memory list.
     *
     * @param name config name (without .json extension)
     */
    public void deleteConfig(String name) {
        Path filePath = CONFIGS_DIR.resolve(name + ".json");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("[Prism] Could not delete config '" + name + "': " + e.getMessage());
        }
        configs.removeIf(c -> c.getName().equals(name));
    }

    // â”€â”€ Accessors â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /** Returns all in-memory {@link Config} instances. */
    public List<Config> getLoadedConfigs() {
        return configs;
    }

    /** Returns the name of the currently active config. */
    public String getCurrentConfig() {
        return currentConfig;
    }

    // â”€â”€ Internal helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Finds an existing {@link Config} by name, or creates and registers a new
     * one if no match is found.
     */
    private Config findOrCreate(String name) {
        Config existing = getConfig(name);
        if (existing != null) return existing;
        Config created = new Config(name);
        configs.add(created);
        return created;
    }

    /** Returns the first {@link Config} whose name matches, or {@code null}. */
    private Config getConfig(String name) {
        return configs.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}






