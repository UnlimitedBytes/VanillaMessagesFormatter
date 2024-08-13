package ovh.unlimitedbytes.vanillamessagesformatter;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.unlimitedbytes.vanillamessagesformatter.config.SettingsConfig;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComponentTranslator implements Translator {
    @Override
    public @NotNull Key name() {
        return Key.key("vanillamessagesformatter", "component_translator");
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        String mapping = VanillaMessagesFormatter.getInstance().getSettingsConfig().getMapping(component.key());
        if (mapping != null) {
            SettingsConfig.Format format = VanillaMessagesFormatter.getInstance().getSettingsConfig().getFormat(mapping);

            return MiniMessage.miniMessage().deserialize(format.prefix())
                .append(renderTranslatableComponent(
                    component
                        .args(colorizeArguments(component.args(), TextColor.color(format.secondaryColor())))
                        .color(TextColor.color(format.primaryColor()))
                ))
                .append(MiniMessage.miniMessage().deserialize(format.suffix()))
                .colorIfAbsent(TextColor.color(format.primaryColor()));
        }

        return null;
    }

    private Component renderTranslatableComponent(TranslatableComponent component) {
        Iterable<? extends Translator> sources = GlobalTranslator.translator().sources();

        for (Translator source : sources) {
            if (source.equals(this)) continue; // Skip this translator to avoid infinite recursion

            Component translatedComponent = source.translate(component, Locale.getDefault());
            if (translatedComponent != null) {
                return translatedComponent;
            }
        }

        for (Translator source : sources) {
            if (source.equals(this)) continue; // Skip this translator to avoid infinite recursion

            MessageFormat messageFormat = source.translate(component.key(), Locale.getDefault());
            if (messageFormat != null) {
                if (component.args().isEmpty()) {
                    return Component.text(messageFormat.format(null));
                }

                return Component.text(messageFormat.format(component.args()));
            }
        }

        return component;
    }

    private List<Component> colorizeArguments(List<Component> arguments, TextColor color) {
        List<Component> colorizedArguments = new ArrayList<>();

        for (Component argument : arguments) {
            colorizedArguments.add(
                argument.asComponent().colorIfAbsent(color)
            );
        }

        return colorizedArguments;
    }
}
