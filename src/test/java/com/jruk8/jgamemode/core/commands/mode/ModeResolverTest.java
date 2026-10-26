package com.jruk8.jgamemode.core.commands.mode;

import org.bukkit.GameMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeResolverTest {

    private GameModeOption mode(String token) {
        assertInstanceOf(ModeResolver.Result.Mode.class, ModeResolver.parse(token));
        return ((ModeResolver.Result.Mode) ModeResolver.parse(token)).option();
    }

    @Test
    @DisplayName("should resolve shorthands")
    void shouldResolveShorthands() {
        assertEquals(GameModeOption.SURVIVAL, mode("s"));
        assertEquals(GameModeOption.CREATIVE, mode("c"));
        assertEquals(GameModeOption.ADVENTURE, mode("a"));
        assertEquals(GameModeOption.SPECTATOR, mode("sp"));
    }

    @Test
    @DisplayName("should resolve numbers")
    void shouldResolveNumbers() {
        assertEquals(GameModeOption.SURVIVAL, mode("0"));
        assertEquals(GameModeOption.CREATIVE, mode("1"));
        assertEquals(GameModeOption.ADVENTURE, mode("2"));
        assertEquals(GameModeOption.SPECTATOR, mode("3"));
    }

    @Test
    @DisplayName("should resolve full names case-insensitively")
    void shouldResolveFullNames() {
        assertEquals(GameModeOption.SURVIVAL, mode("survival"));
        assertEquals(GameModeOption.CREATIVE, mode("Creative"));
        assertEquals(GameModeOption.ADVENTURE, mode("ADVENTURE"));
        assertEquals(GameModeOption.SPECTATOR, mode("spectator"));
    }

    @Test
    @DisplayName("should resolve toggle prefixes")
    void shouldResolveTogglePrefixes() {
        for (String token : new String[]{"t", "to", "tog", "togg", "toggle", "TOGGLE"}) {
            assertInstanceOf(ModeResolver.Result.Toggle.class, ModeResolver.parse(token), token);
        }
    }

    @Test
    @DisplayName("should resolve dynamic substrings")
    void shouldResolveSubstrings() {
        assertEquals(GameModeOption.SURVIVAL, mode("surv"));
        assertEquals(GameModeOption.CREATIVE, mode("creat"));
        assertEquals(GameModeOption.ADVENTURE, mode("vent"));
        assertEquals(GameModeOption.SPECTATOR, mode("spec"));
    }

    @Test
    @DisplayName("should resolve ambiguous substrings to the first mode in order")
    void shouldResolveAmbiguousToFirst() {
        assertEquals(GameModeOption.SURVIVAL, mode("r"));
    }

    @Test
    @DisplayName("should reject gibberish")
    void shouldRejectGibberish() {
        for (String token : new String[]{"aslkdl", "xyz", "@a", "Steve"}) {
            assertInstanceOf(ModeResolver.Result.Invalid.class, ModeResolver.parse(token), token);
        }
    }

    @Test
    @DisplayName("should reject null and blank input")
    void shouldRejectBlank() {
        assertInstanceOf(ModeResolver.Result.Invalid.class, ModeResolver.parse(null));
        assertInstanceOf(ModeResolver.Result.Invalid.class, ModeResolver.parse("   "));
    }

    @Test
    @DisplayName("should parse modes but never toggles via parseMode")
    void shouldParseModeOnly() {
        assertTrue(ModeResolver.parseMode("sp").isPresent());
        assertEquals(GameMode.SPECTATOR, ModeResolver.parseMode("sp").get().gameMode());
        assertTrue(ModeResolver.parseMode("toggle").isEmpty());
        assertTrue(ModeResolver.parseMode("aslkdl").isEmpty());
    }
}
