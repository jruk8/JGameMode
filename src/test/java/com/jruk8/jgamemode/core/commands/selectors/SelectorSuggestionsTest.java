package com.jruk8.jgamemode.core.commands.selectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectorSuggestionsTest {

    private static final List<String> ONLINE = List.of("Alex", "Steve", "Zed");

    @Nested
    @DisplayName("senders with the other permission")
    class OthersTests {

        @Test
        @DisplayName("should list selectors first, then names with the executor first")
        void shouldOrderSelectorsThenNames() {
            var suggestions = SelectorSuggestions.suggest(true, "Steve", ONLINE, "");

            assertEquals(List.of("@p", "@a", "@r", "@s", "Steve", "Alex", "Zed"), suggestions);
        }

        @Test
        @DisplayName("should filter case-insensitively by prefix")
        void shouldFilterByPrefix() {
            assertEquals(List.of("Steve"), SelectorSuggestions.suggest(true, "Steve", ONLINE, "s"));
            assertEquals(List.of("@p", "@a", "@r", "@s"), SelectorSuggestions.suggest(true, "Steve", ONLINE, "@"));
        }

        @Test
        @DisplayName("should keep online order when the executor has no name")
        void shouldKeepOrderForConsole() {
            var suggestions = SelectorSuggestions.suggest(true, "", ONLINE, "");

            assertEquals(List.of("@p", "@a", "@r", "@s", "Alex", "Steve", "Zed"), suggestions);
        }
    }

    @Nested
    @DisplayName("senders without the other permission")
    class SelfTests {

        @Test
        @DisplayName("should only suggest @s and the sender's own name")
        void shouldSuggestSelfOnly() {
            var suggestions = SelectorSuggestions.suggest(false, "Steve", ONLINE, "");

            assertEquals(List.of("@s", "Steve"), suggestions);
        }

        @Test
        @DisplayName("should filter the self candidates by prefix")
        void shouldFilterSelfByPrefix() {
            assertEquals(List.of("@s"), SelectorSuggestions.suggest(false, "Steve", ONLINE, "@"));
            assertEquals(List.of("Steve"), SelectorSuggestions.suggest(false, "Steve", ONLINE, "st"));
            assertEquals(List.of(), SelectorSuggestions.suggest(false, "Steve", ONLINE, "alex"));
        }
    }
}
