package ovh.unlimitedbytes.vanillamessagesformatter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public final class VanillaMessagesTranslator implements Translator {

    @Override
    public @NotNull Key name() {
        return Key.key("vanillamessagesformatter", "vanillamessagesformatter");
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return switch (key) {
            case "commands.vanillamessagesformatter.usage" -> new MessageFormat(
                "Usage: /vanillamessagesformatter <reload|mapping>"
            );
            case "commands.vanillamessagesformatter.mapping.usage" -> new MessageFormat(
                "Usage: /vanillamessagesformatter mapping <key>"
            );
            case "commands.vanillamessagesformatter.info.success" -> new MessageFormat(
                "VanillaMessagesFormatter plugin by UnlimitedBytes."
            );
            case "commands.vanillamessagesformatter.reload.error" -> new MessageFormat(
                "Failed to reload the plugin."
            );
            case "commands.vanillamessagesformatter.reload.success" -> new MessageFormat(
                "VanillaMessagesFormatter plugin reloaded."
            );
            case "commands.vanillamessagesformatter.mapping.success" -> new MessageFormat(
                "Mapping for \"{0}\" is: {1}"
            );
            case "commands.vanillamessagesformatter.mapping.not_found" -> new MessageFormat(
                "No mapping found for: {0}"
            );
            default -> null;
        };

    }
}