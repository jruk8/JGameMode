package com.jruk8.jgamemode.core;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartupBannerTest {

    @Test
    @DisplayName("should list modrinth, discord and github after the title")
    void shouldOrderLinks() {
        List<String> lines = StartupBanner.lines("JGameMode", "1.0.0");

        assertEquals(4, lines.size());
        assertEquals("\u00A76\u00A7lJGameMode \u00A78v1.0.0 \u00A77enabled", lines.get(0));
        assertEquals("\u00A78\u00BB \u00A77Modrinth: \u00A7fhttps://modrinth.com/project/jgamemode", lines.get(1));
        assertEquals("\u00A78\u00BB \u00A77Discord: \u00A7fhttps://discord.com/invite/hkWmCVmWDC", lines.get(2));
        assertEquals("\u00A78\u00BB \u00A77GitHub: \u00A7fhttps://github.com/jruk8/JGameMode", lines.get(3));
    }

    @Test
    @DisplayName("should pad the banner with blank lines")
    void shouldPadBanner() {
        List<Component> sent = new ArrayList<>();

        StartupBanner.print(sent::add, "JGameMode", "1.0.0");

        assertEquals(8, sent.size());
        assertEquals(Component.empty(), sent.get(0));
        assertEquals(Component.empty(), sent.get(1));
        assertEquals(Component.empty(), sent.get(6));
        assertEquals(Component.empty(), sent.get(7));
    }
}
