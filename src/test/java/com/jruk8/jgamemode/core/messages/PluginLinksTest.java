package com.jruk8.jgamemode.core.messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginLinksTest {

    @Test
    @DisplayName("should expose all four link buttons")
    void shouldExposeLinks() {
        Map<String, String> links = PluginLinks.placeholders();

        assertEquals(4, links.size());
        assertTrue(links.containsKey("{modrinth}"));
        assertTrue(links.containsKey("{github}"));
        assertTrue(links.containsKey("{discord}"));
        assertTrue(links.containsKey("{donate}"));
    }

    @Test
    @DisplayName("should click through with fully colored tooltips")
    void shouldColorLinks() {
        Map<String, String> links = PluginLinks.placeholders();

        assertEquals(
                "<click:open_url:'https://modrinth.com/project/jgamemode'>"
                        + "<hover:show_text:'<#5ae663>Open on Modrinth'>"
                        + "<#5ae663>Modrinth</hover></click>",
                links.get("{modrinth}"));
        assertEquals(
                "<click:open_url:'https://discord.com/invite/hkWmCVmWDC'>"
                        + "<hover:show_text:'<#6258f2>Join the Discord'>"
                        + "<#6258f2>Discord</hover></click>",
                links.get("{discord}"));
    }
}
