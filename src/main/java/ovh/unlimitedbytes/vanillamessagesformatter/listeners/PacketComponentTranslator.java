package ovh.unlimitedbytes.vanillamessagesformatter.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.unlimitedbytes.vanillamessagesformatter.VanillaMessagesFormatter;
import ovh.unlimitedbytes.vanillamessagesformatter.config.SettingsConfig;

import java.util.ArrayList;
import java.util.List;


public class PacketComponentTranslator implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SYSTEM_CHAT_MESSAGE)
            return;

        WrapperPlayServerSystemChatMessage packet = new WrapperPlayServerSystemChatMessage(event);
        Component component = packet.getMessage();

        if(!(component instanceof TranslatableComponent translatableComponent)) {
            return;
        }

        String key = translatableComponent.key();
        String mapping = VanillaMessagesFormatter.getInstance().getSettingsConfig().getMapping(key);
        if (mapping == null) {
            return;
        }

        SettingsConfig.Format format = VanillaMessagesFormatter.getInstance().getSettingsConfig().getFormat(mapping);
        packet.setMessage(
            MiniMessage.miniMessage().deserialize(format.prefix())
                .append(
                    translatableComponent
                        .arguments(colorizeArguments(translatableComponent.arguments(), TextColor.color(format.secondaryColor())))
                        .color(TextColor.color(format.primaryColor()))
                )
                .append(MiniMessage.miniMessage().deserialize(format.suffix()))
                .colorIfAbsent(TextColor.color(format.primaryColor()))
        );
    }

    private List<TranslationArgument> colorizeArguments(List<TranslationArgument> arguments, TextColor color) {
        List<TranslationArgument> colorizedArguments = new ArrayList<>();

        for (TranslationArgument argument : arguments) {
            colorizedArguments.add(
                TranslationArgument.component(argument.asComponent().colorIfAbsent(color))
            );
        }

        return colorizedArguments;
    }
}
