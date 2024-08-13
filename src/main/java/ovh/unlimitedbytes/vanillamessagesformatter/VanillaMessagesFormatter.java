package ovh.unlimitedbytes.vanillamessagesformatter;

import lombok.Getter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.unlimitedbytes.vanillamessagesformatter.command.VanillaMessagesFormatterCommand;
import ovh.unlimitedbytes.vanillamessagesformatter.config.SettingsConfig;

import java.io.IOException;
import java.util.Objects;

public final class VanillaMessagesFormatter extends JavaPlugin {

    @Getter private static VanillaMessagesFormatter instance;
    @Getter private final SettingsConfig settingsConfig = new SettingsConfig(this);

    public VanillaMessagesFormatter() throws IOException, InvalidConfigurationException {
        instance = this;
    }

    @Override
    public void onEnable() {
        reload();

        Objects.requireNonNull(getCommand("vanillamessagesformatter"))
            .setExecutor(new VanillaMessagesFormatterCommand());

        GlobalTranslator.translator().addSource(new VanillaMessagesTranslator());
        GlobalTranslator.translator().addSource(new ComponentTranslator());
    }

    @Override
    public void onDisable() {
        settingsConfig.save();
    }

    public void reload() {
        if (!settingsConfig.existsFile()) {
            settingsConfig.loadFromResource();
            settingsConfig.createFile();
        }

        settingsConfig.loadFromFile();
    }
}
