package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.commands.user.help.HelpEntry;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpEntryTest {

    private static final List<HelpEntry> ENTRIES = List.of(
            new HelpEntry("/jtemplate help [query]", "Shows this help menu."),
            new HelpEntry("/jtemplate version", "Shows the plugin version.", "jtemplate.version"),
            new HelpEntry("/jtemplate reload", "Reloads the plugin configuration.", "jtemplate.reload")
    );

    @Nested
    @DisplayName("matching() without a query")
    class BlankQueryTests {

        @Test
        @DisplayName("should return every entry when the query is null")
        void shouldReturnAllWhenQueryIsNull() {
            assertEquals(ENTRIES, HelpEntry.matching(ENTRIES, null));
        }

        @Test
        @DisplayName("should return every entry when the query is blank")
        void shouldReturnAllWhenQueryIsBlank() {
            assertEquals(ENTRIES, HelpEntry.matching(ENTRIES, "   "));
        }
    }

    @Nested
    @DisplayName("matching() with a query")
    class QueryTests {

        @Test
        @DisplayName("should match syntax case-insensitively")
        void shouldMatchSyntaxCaseInsensitively() {
            var matches = HelpEntry.matching(ENTRIES, "VERSION");

            assertEquals(1, matches.size());
            assertEquals("/jtemplate version", matches.getFirst().syntax());
        }

        @Test
        @DisplayName("should match descriptions")
        void shouldMatchDescriptions() {
            var matches = HelpEntry.matching(ENTRIES, "reloads");

            assertEquals(1, matches.size());
            assertEquals("/jtemplate reload", matches.getFirst().syntax());
        }

        @Test
        @DisplayName("should return no entries when nothing matches")
        void shouldReturnEmptyWhenNothingMatches() {
            assertTrue(HelpEntry.matching(ENTRIES, "nothing-like-this").isEmpty());
        }
    }

    @Nested
    @DisplayName("visibleTo()")
    class VisibilityTests {

        @Test
        @DisplayName("should show permission-less entries to everyone")
        void shouldShowOpenEntriesToEveryone() {
            CommandSender sender = mock(CommandSender.class);

            assertTrue(ENTRIES.getFirst().visibleTo(sender));
        }

        @Test
        @DisplayName("should gate entries on any of their permissions")
        void shouldGateOnPermissions() {
            CommandSender sender = mock(CommandSender.class);
            when(sender.hasPermission("jtemplate.version")).thenReturn(true);
            var entry = new HelpEntry("/jtemplate speed reset", "Resets both.",
                    "jtemplate.flyspeed", "jtemplate.version");

            assertTrue(entry.visibleTo(sender));
            assertTrue(ENTRIES.get(1).visibleTo(sender));
            assertFalse(ENTRIES.get(2).visibleTo(sender));
        }
    }
}
