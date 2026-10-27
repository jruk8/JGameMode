package com.jruk8.jgamemode.core.messages;

import java.util.Map;

/**
 * The plugin's link buttons ({@code {modrinth}}, {@code {github}},
 * {@code {discord}}, {@code {donate}}), resolved by the message parser.
 * <p>
 * Links live in code, never in messages.yml, so the message defaults stay
 * readable and every link shares one clickable shape: the label in its
 * own color, opening the page on click, with a fully colored tooltip.
 */
public final class PluginLinks {

    public static final String MODRINTH_URL = "https://modrinth.com/project/jgamemode";
    public static final String GITHUB_URL = "https://github.com/jruk8/JGameMode";
    public static final String DISCORD_URL = "https://discord.com/invite/hkWmCVmWDC";
    public static final String DONATE_URL = "https://ko-fi.com/jruk";

    private static final String MODRINTH_COLOR = "<#5ae663>";
    private static final String GITHUB_COLOR = "<#5a88e6>";
    private static final String DISCORD_COLOR = "<#6258f2>";
    private static final String DONATE_COLOR = "<#ff5e5b>";

    private PluginLinks() {
    }

    /**
     * @return the link placeholders and their raw MiniMessage buttons
     */
    public static Map<String, String> placeholders() {
        return Map.of(
                "{modrinth}", link(MODRINTH_URL, MODRINTH_COLOR, "Modrinth", "Open on Modrinth"),
                "{github}", link(GITHUB_URL, GITHUB_COLOR, "GitHub", "Open on GitHub"),
                "{discord}", link(DISCORD_URL, DISCORD_COLOR, "Discord", "Join the Discord"),
                "{donate}", link(DONATE_URL, DONATE_COLOR, "Donate", "Support on Ko-fi"));
    }

    private static String link(String url, String color, String label, String tip) {
        return "<click:open_url:'" + url + "'>"
                + "<hover:show_text:'" + color + tip + "'>"
                + color + label + "</hover></click>";
    }
}
