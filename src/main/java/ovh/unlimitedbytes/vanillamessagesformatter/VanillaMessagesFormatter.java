package ovh.unlimitedbytes.vanillamessagesformatter;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.github.retrooper.packetevents.util.TimeStampMode;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.unlimitedbytes.vanillamessagesformatter.command.VanillaMessagesFormatterCommand;
import ovh.unlimitedbytes.vanillamessagesformatter.config.SettingsConfig;
import ovh.unlimitedbytes.vanillamessagesformatter.listeners.PacketComponentTranslator;

import java.io.IOException;
import java.util.Objects;

public final class VanillaMessagesFormatter extends JavaPlugin {

    @Getter private static VanillaMessagesFormatter instance;
    @Getter private final SettingsConfig settingsConfig = new SettingsConfig(this);

    public VanillaMessagesFormatter() throws IOException, InvalidConfigurationException {
        instance = this;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();
        settings.checkForUpdates(false).debug(false).timeStampMode(TimeStampMode.MILLIS);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        reload();

        Objects.requireNonNull(getCommand("vanillamessagesformatter"))
            .setExecutor(new VanillaMessagesFormatterCommand());

        if (settingsConfig.isUsingTranslator()) {
            GlobalTranslator.translator().addSource(new ComponentTranslator());
        } else {
            PacketEvents.getAPI().getEventManager().registerListener(
                new PacketComponentTranslator(),
                PacketListenerPriority.NORMAL
            );
        }

        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
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
