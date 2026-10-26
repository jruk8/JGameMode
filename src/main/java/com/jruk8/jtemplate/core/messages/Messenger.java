package com.jruk8.jtemplate.core.messages;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Handles the parsing and dispatching of MiniMessage-formatted text to command senders.
 * <p>
 * Acts as a bridge between raw message strings, {bracket} placeholders,
 * and Bukkit's {@link CommandSender} interface.
 */
public final class Messenger {

    private final MessageParser messageParser;

    /**
     * Constructs a new {@code Messenger} with the given parser.
     *
     * @param messageParser the parser used to process raw strings into components
     */
    public Messenger(MessageParser messageParser) {
        this.messageParser = messageParser;
    }

    /**
     * Parses a raw string with {bracket} placeholders, then sends it to the target recipient.
     *
     * @param sender             the recipient of the message; if null, operation is ignored
     * @param rawMessage         the unparsed message string; if null or empty, operation is ignored
     * @param stringPlaceholders key-value pairs for {bracket} text replacements
     */
    public void send(CommandSender sender,
                     String rawMessage,
                     Map<String, String> stringPlaceholders) {
        if (sender == null || rawMessage == null || rawMessage.isEmpty()) {
            return;
        }

        Component text = messageParser.parse(rawMessage, stringPlaceholders);
        sender.sendMessage(text);
    }

    /**
     * Parses a raw string with no placeholders and sends it to the target recipient.
     *
     * @param sender     the recipient of the message
     * @param rawMessage the unparsed message string
     */
    public void send(CommandSender sender, String rawMessage) {
        send(sender, rawMessage, Map.of());
    }
}
