package com.jruk8.jgamemode.core.commands.selectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueSuggestionsTest {

    @Test
    @DisplayName("should keep the configured order and drop blanks")
    void shouldKeepConfiguredOrder() {
        var configured = List.of("on", "off", "");

        assertEquals(List.of("on", "off"), ValueSuggestions.suggest(configured, null));
    }

    @Test
    @DisplayName("should filter by the typed prefix case-insensitively")
    void shouldFilterByPrefix() {
        var configured = List.of("on", "off", "survival");

        assertEquals(List.of(), ValueSuggestions.suggest(configured, "x"));
        assertEquals(List.of("off"), ValueSuggestions.suggest(configured, "OF"));
    }

    @Test
    @DisplayName("should treat a null prefix as no filter")
    void shouldTreatNullPrefixAsNoFilter() {
        var configured = List.of("on", "off");

        assertEquals(configured, ValueSuggestions.suggest(configured, null));
    }
}
