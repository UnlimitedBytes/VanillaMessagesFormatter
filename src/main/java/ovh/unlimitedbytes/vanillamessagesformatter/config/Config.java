package ovh.unlimitedbytes.vanillamessagesformatter.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ovh.unlimitedbytes.vanillamessagesformatter.VanillaMessagesFormatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class Config {
    protected final File file;
    protected final String filePath;
    protected final VanillaMessagesFormatter plugin;
    protected final YamlConfiguration config = new YamlConfiguration();

    public Config(VanillaMessagesFormatter plugin, String filePath) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        this.filePath = filePath;
        this.file = new File(plugin.getDataFolder(), filePath);
    }

    protected abstract void loadProperties();

    public void loadFromResource() {
        try {
            loadFromResourceOrThrow();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to load configuration " + filePath + " from the plugin jar.");
        }
    }

    public void loadFromResourceOrThrow() throws IOException, InvalidConfigurationException {
        URL url = getClass().getClassLoader().getResource(filePath);

        if (url == null) {
            throw new IOException(filePath + " file not found in the plugin jar.");
        }

        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            config.load(reader);
        }
    }

    public boolean existsFile() {
        return file.exists();
    }

    public void createFile() {
        try {
            createFileOrThrow();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create the " + file.getPath() + " file.");
        }
    }

    public void createFileOrThrow() throws IOException {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Failed to create the " + file.getParentFile().getPath() + " directory.");
        }

        if (file.exists()) {
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                File backupFile = file.toPath().resolveSibling(file.getName() + "." + i).toFile();

                if (!backupFile.exists()) {
                    if (!file.renameTo(backupFile)) {
                        throw new IOException("Failed to create a backup of the " + file.getPath() + " file.");
                    }

                    break;
                }
            }
        }

        saveOrThrow();
    }

    public void loadFromFile() {
        try {
            loadFromFileOrThrow();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to load configuration from " + file.getPath() + " file.");
        }
    }

    public void loadFromFileOrThrow() throws IOException, InvalidConfigurationException {
        config.load(file);
        loadProperties();
    }

    public void save() {
        try {
            saveOrThrow();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to " + file.getPath() + " file.");
        }
    }

    public void saveOrThrow() throws IOException {
        config.save(file);
    }
}
