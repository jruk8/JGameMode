package com.jruk8.jgamemode.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.function.Consumer;

/**
 * The one compact banner sent to the console when the plugin enables.
 * Lines use legacy section colors, deserialized to components so the
 * console actually colorizes them; plain logger calls print the codes
 * raw. No MiniMessage tags, so this never touches the message pipeline.
 */
public final class StartupBanner {

    private static final int PADDING_LINES = 2;

    /** Brand tone matching the default {#edca3b} brand color. */
    private static final String BRAND = "\u00A76";

    private static final String MODRINTH_URL = "https://modrinth.com/project/jgamemode";
    private static final String DISCORD_URL = "https://discord.com/invite/hkWmCVmWDC";
    private static final String GITHUB_URL = "https://github.com/jruk8/JGameMode";

    private StartupBanner() {
    }

    /**
     * Banner lines for the running plugin name and version: a brand
     * title, then Modrinth, Discord and GitHub bullets. Pure for tests.
     *
     * @param name the running plugin name
     * @param version the running plugin version
     * @return the banner lines in legacy section colors
     */
    public static List<String> lines(String name, String version) {
        return List.of(
                BRAND + "\u00A7l" + name + " \u00A78v" + version + " \u00A77enabled",
                "\u00A78\u00BB \u00A77Modrinth: \u00A7f" + MODRINTH_URL,
                "\u00A78\u00BB \u00A77Discord: \u00A7f" + DISCORD_URL,
                "\u00A78\u00BB \u00A77GitHub: \u00A7f" + GITHUB_URL);
    }

    /**
     * Sends the banner with blank padding around it.
     *
     * @param console the console sink, usually the console sender
     * @param name the running plugin name
     * @param version the running plugin version
     */
    public static void print(Consumer<Component> console, String name, String version) {
        for (int index = 0; index < PADDING_LINES; index++) {
            console.accept(Component.empty());
        }
        for (String line : lines(name, version)) {
            console.accept(LegacyComponentSerializer.legacySection().deserialize(line));
        }
        for (int index = 0; index < PADDING_LINES; index++) {
            console.accept(Component.empty());
        }
    }
}
