package com.jruk8.jtemplate.core.messages;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.Map;

/**
 * Handles the raw-string -> Component pipeline:
 * 1. Internal {prefix} placeholder
 * 2. Runtime {bracket} placeholders ({mode}, {old}, etc.)
 * 3. Color palette placeholders ({plain}, ...) from config.yml
 * 4. PlaceholderAPI %percent% placeholders (%player_name%, etc.)
 * 5. Backslash-n escapes -> real newlines
 * 6. MiniMessage formatting
 * <p>
 * Plugin placeholders always use {braces}. Angle brackets are reserved
 * for raw MiniMessage: anything in &lt;&gt; passes through to step 6
 * untouched, so literal brackets in messages must be escaped
 * ({@code \<br>} renders a literal {@code <br>}).
 */
public final class MessageParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final PluginConfig pluginConfig;
    private final MessagesConfig messagesConfig;

    public MessageParser(PluginConfig pluginConfig, MessagesConfig messagesConfig) {
        this.pluginConfig = pluginConfig;
        this.messagesConfig = messagesConfig;
    }

    public Component parse(String rawString, Map<String, String> stringPlaceholders) {
        if (rawString == null || rawString.isEmpty()) {
            return Component.text("");
        }

        String text = rawString;

        // 1. Replace internal {prefix}
        text = text.replace("{prefix}", messagesConfig.getPrefix());

        // 2. Replace runtime {bracket} placeholders
        if (stringPlaceholders != null && !stringPlaceholders.isEmpty()) {
            for (Map.Entry<String, String> entry : stringPlaceholders.entrySet()) {
                String key = entry.getKey();
                if (!key.startsWith("{") || !key.endsWith("}")) {
                    key = "{" + key + "}";
                }
                text = text.replace(key, entry.getValue());
            }
        }

        // 3. Resolve the config.yml color palette (after values, so composed
        // text passed through placeholders picks the colors up too)
        text = resolveColors(text);

        // 4. Process PlaceholderAPI %placeholders%
        if (isPapiEnabled()) {
            text = PlaceholderAPI.setPlaceholders(null, text);
        }

        // 5. Backslash-n escapes become real newlines (single-quoted and
        // plain YAML scalars keep them literal, unlike double-quoted ones)
        text = text.replace("\\n", "\n");

        // 6. MiniMessage parses the string; <tags> are MiniMessage's own
        return MINI_MESSAGE.deserialize(text);
    }

    private String resolveColors(String text) {
        var colors = pluginConfig.getColors();
        if (colors == null) {
            return text;
        }
        return text
                .replace("{plain}", colors.getPlainColor())
                .replace("{brand}", colors.getBrandColor())
                .replace("{hl}", colors.getHighlightColor())
                .replace("{error}", colors.getErrorColor());
    }

    private boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}
