package ovh.unlimitedbytes.vanillamessagesformatter.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.unlimitedbytes.vanillamessagesformatter.VanillaMessagesFormatter;
import ovh.unlimitedbytes.vanillamessagesformatter.config.SettingsConfig;

import java.util.List;

public class VanillaMessagesFormatterCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (args.length == 0) {
            String message = "VanillaMessagesFormatter v" +
                             VanillaMessagesFormatter.getInstance().getDescription().getVersion()
                             +
                             "\n"
                             +
                             "Usage: /vanillamessagesformatter [reload]";
            this.sendMessage("info", commandSender, message);
            return true;
        }

        if (args[0].equals("reload")) {
            try {
                VanillaMessagesFormatter.getInstance().reload();
            } catch (Exception exception) {
                this.sendMessage("error", commandSender, "Failed to reload the configuration.");
                VanillaMessagesFormatter.getInstance().getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to reload the plugin.",
                    exception
                );
                return true;
            }

            this.sendMessage("success", commandSender, "Configuration reloaded.");
            return true;
        }

        this.sendMessage("warning", commandSender, "Unknown command.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("reload");
        }

        return List.of();
    }

    private void sendMessage(
        @NotNull String formatKey,
        @NotNull CommandSender commandSender,
        @NotNull String message
    ) {
        SettingsConfig.Format format = VanillaMessagesFormatter.getInstance().getSettingsConfig().getFormat(formatKey);
        Component prefix = MiniMessage.miniMessage().deserialize(format.prefix());
        Component suffix = MiniMessage.miniMessage().deserialize(format.suffix());

        commandSender.sendMessage(
            prefix
                .append(MiniMessage.miniMessage().deserialize(message))
                .append(suffix)
                .colorIfAbsent(TextColor.color(format.primaryColor()))
        );
    }
}
