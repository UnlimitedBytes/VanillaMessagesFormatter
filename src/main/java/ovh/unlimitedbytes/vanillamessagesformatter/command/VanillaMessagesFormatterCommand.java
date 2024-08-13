package ovh.unlimitedbytes.vanillamessagesformatter.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.unlimitedbytes.vanillamessagesformatter.VanillaMessagesFormatter;

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
            commandSender.sendMessage(Component.translatable("commands.vanillamessagesformatter.info.success"));
            return true;
        }

        if (args[0].equals("reload")) {
            try {
                VanillaMessagesFormatter.getInstance().reload();
            } catch (Exception exception) {
                commandSender.sendMessage(Component.translatable("commands.vanillamessagesformatter.reload.error"));
                VanillaMessagesFormatter.getInstance().getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to reload the plugin.",
                    exception
                );
                return true;
            }

            commandSender.sendMessage(Component.translatable("commands.vanillamessagesformatter.reload.success"));
            return true;
        }

        if (args[0].equals("mapping")) {
            if (args.length == 2) {
                String mapping = VanillaMessagesFormatter.getInstance().getSettingsConfig().getMapping(args[1]);

                if (mapping == null) {
                    commandSender.sendMessage(Component.translatable(
                        "commands.vanillamessagesformatter.mapping.not_found",
                        Component.text(args[1])
                    ));
                    return true;
                }

                commandSender.sendMessage(Component.translatable(
                    "commands.vanillamessagesformatter.mapping.success",
                    Component.text(args[1]),
                    Component.text(mapping)
                ));
                return true;
            }

            commandSender.sendMessage(Component.translatable("commands.vanillamessagesformatter.mapping.usage"));
            return true;
        }

        commandSender.sendMessage(Component.translatable("commands.vanillamessagesformatter.usage"));
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
            return List.of("reload", "mapping");
        }

        if (args.length == 2 && args[0].equals("mapping")) {
            return VanillaMessagesFormatter.getInstance().getSettingsConfig().getMappingsKeys();
        }

        return List.of();
    }
}
