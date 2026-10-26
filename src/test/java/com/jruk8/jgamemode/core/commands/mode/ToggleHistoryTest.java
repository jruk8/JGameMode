package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.storage.SqliteStorage;
import org.bukkit.GameMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToggleHistoryTest {

    private final UUID player = UUID.randomUUID();

    private SqliteStorage storage;
    private ToggleHistory history;

    @BeforeEach
    void setUp() {
        storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("toggle-test"));
        assertTrue(storage.init());
        history = new ToggleHistory(storage);
    }

    @AfterEach
    void tearDown() {
        storage.close();
    }

    @Test
    @DisplayName("should start with no history")
    void shouldStartEmpty() {
        assertTrue(history.lastMode(player).isEmpty());
    }

    @Test
    @DisplayName("should cache the previous non-spectator mode")
    void shouldCachePreviousMode() {
        history.recordSwitch(player, GameMode.CREATIVE, GameMode.SURVIVAL);

        assertEquals(GameMode.CREATIVE, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should overwrite the cache on every real switch")
    void shouldOverwriteCache() {
        history.recordSwitch(player, GameMode.CREATIVE, GameMode.SPECTATOR);
        history.recordSwitch(player, GameMode.SURVIVAL, GameMode.ADVENTURE);

        assertEquals(GameMode.SURVIVAL, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should never cache switches from spectator")
    void shouldIgnoreSwitchesFromSpectator() {
        history.recordSwitch(player, GameMode.CREATIVE, GameMode.SURVIVAL);
        history.recordSwitch(player, GameMode.SPECTATOR, GameMode.ADVENTURE);

        assertEquals(GameMode.CREATIVE, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should never cache direct switches to spectator")
    void shouldIgnoreSwitchesToSpectator() {
        history.recordSwitch(player, GameMode.SURVIVAL, GameMode.SPECTATOR);
        history.recordSwitch(player, GameMode.ADVENTURE, GameMode.SPECTATOR);

        assertTrue(history.lastMode(player).isEmpty());
    }

    @Test
    @DisplayName("should keep a stored ticket across a direct switch to spectator")
    void shouldKeepTicketAcrossSpectating() {
        history.recordSwitch(player, GameMode.SURVIVAL, GameMode.CREATIVE);
        history.recordSwitch(player, GameMode.CREATIVE, GameMode.SPECTATOR);

        assertEquals(GameMode.SURVIVAL, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should refresh the ticket on a toggle swap to spectator")
    void shouldRefreshTicketOnToggleSwap() {
        history.recordSwap(player, GameMode.CREATIVE);

        assertEquals(GameMode.CREATIVE, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should never cache a toggle swap from spectator")
    void shouldIgnoreToggleSwapFromSpectator() {
        history.recordSwap(player, GameMode.CREATIVE);
        history.recordSwap(player, GameMode.SPECTATOR);

        assertEquals(GameMode.CREATIVE, history.lastMode(player).orElseThrow());
    }

    @Test
    @DisplayName("should ignore same-mode switches")
    void shouldIgnoreSameMode() {
        history.recordSwitch(player, GameMode.CREATIVE, GameMode.CREATIVE);

        assertTrue(history.lastMode(player).isEmpty());
    }
}
