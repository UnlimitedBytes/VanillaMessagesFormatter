package ovh.unlimitedbytes.vanillamessagesformatter.config;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.unlimitedbytes.vanillamessagesformatter.VanillaMessagesFormatter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class SettingsConfig extends Config {
    private final HashMap<@NotNull String, @NotNull Format> formats = new HashMap<>();
    private final HashMap<@NotNull String, @NotNull String> mappings = new HashMap<>();
    private boolean useTranslator = false;

    public record Format(
        String prefix,
        String suffix,
        Integer primaryColor,
        Integer secondaryColor
    ) {
    }

    public SettingsConfig(VanillaMessagesFormatter plugin) throws IOException, InvalidConfigurationException {
        super(plugin, "settings.yml");
    }

    @Override
    protected void loadProperties() {
        loadFormatMessages();
        loadMappings();
    }

    private void loadFormatMessages() {
        if (!config.contains("useTranslator")) {
            VanillaMessagesFormatter.getInstance().getLogger().warning(
                "useTranslator not found in the settings.yml file. Falling back to default value (false)."
            );
        } else {
            useTranslator = config.getBoolean("useTranslator");
        }

        formats.clear();
        ConfigurationSection formatsSection = Objects.requireNonNull(
            config.getConfigurationSection("formats"),
            "Formats section not found in the settings.yml file."
        );

        for (String key : formatsSection.getKeys(false)) {
            ConfigurationSection format = formatsSection.getConfigurationSection(key);
            if (format == null) {
                VanillaMessagesFormatter.getInstance().getLogger().warning(
                    "Format with key '" + key + "' not in correct format."
                );
                continue;
            }

            String prefix = format.getString("prefix");
            String suffix = format.getString("suffix");
            String primaryColor = format.getString("primaryColor");
            String secondaryColor = format.getString("secondaryColor");

            if (prefix == null || suffix == null || primaryColor == null || secondaryColor == null) {
                VanillaMessagesFormatter.getInstance().getLogger().warning(
                    "Format with key '" + key + "' not in correct format."
                );
                continue;
            }

            formats.put(key, new Format(prefix, suffix, hexToInt(primaryColor), hexToInt(secondaryColor)));
        }
    }

    public void loadMappings() {
        mappings.clear();
        List<?> mappingsSection = Objects.requireNonNull(
            config.getList("mappings"),
            "Mappings list not found in the settings.yml file."
        );

        for (Object mapping : mappingsSection) {
            if (mapping instanceof LinkedHashMap<?, ?> mappingMap) {
                for (Object key : mappingMap.keySet()) {
                    Object value = mappingMap.get(key);

                    if (key instanceof String && value instanceof String) {
                        mappings.put((String) key, (String) value);
                    }
                }
            }
        }
    }

    /**
     * Get the format message from the settings.yml file.
     *
     * @param key The key of the format message.
     *
     * @return The format message.
     *
     * @throws IllegalArgumentException If the format message with the given key is not found in the settings.yml file.
     */
    public @NotNull Format getFormat(String key) throws IllegalArgumentException {
        if (!formats.containsKey(key)) {
            throw new IllegalArgumentException("Format with key '" + key + "' not found in the settings.yml file.");
        }

        return Objects.requireNonNull(formats.get(key));
    }

    public @Nullable String getMapping(String key) {
        return mappings.get(key);
    }

    public ImmutableList<String> getMappingsKeys() {
        return ImmutableList.copyOf(mappings.keySet());
    }

    private int hexToInt(String hex) {
        String hexString = hex.replace("#", "");
        return Integer.parseInt(hexString, 16);
    }

    public boolean isUsingTranslator() {
        return useTranslator;
    }
}
