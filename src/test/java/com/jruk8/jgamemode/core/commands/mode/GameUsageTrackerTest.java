package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.storage.SqliteStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameUsageTrackerTest {

    private final UUID player = UUID.randomUUID();

    private SqliteStorage storage;
    private GameUsageTracker tracker;

    @BeforeEach
    void setUp() {
        storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("usage-test"));
        assertTrue(storage.init());
        tracker = new GameUsageTracker(storage);
    }

    @AfterEach
    void tearDown() {
        storage.close();
    }

    @Test
    @DisplayName("should start at zero on every counter")
    void shouldStartAtZero() {
        assertEquals(0, tracker.playerUses(player));
        assertEquals(0, tracker.playerToggleUses(player));
        assertEquals(0, tracker.playerModeUses(player, GameModeOption.CREATIVE));
        assertEquals(0, tracker.globalUses());
        assertEquals(0, tracker.globalToggleUses());
        assertEquals(0, tracker.globalModeUses(GameModeOption.SURVIVAL));
    }

    @Test
    @DisplayName("should record a switch on the total, the mode and the globals")
    void shouldRecordSwitch() {
        tracker.recordSwitch(player, GameModeOption.CREATIVE);

        assertEquals(1, tracker.playerUses(player));
        assertEquals(1, tracker.playerModeUses(player, GameModeOption.CREATIVE));
        assertEquals(0, tracker.playerModeUses(player, GameModeOption.SURVIVAL));
        assertEquals(1, tracker.globalUses());
        assertEquals(1, tracker.globalModeUses(GameModeOption.CREATIVE));
    }

    @Test
    @DisplayName("should record a toggle on the total, the toggle and each distinct mode")
    void shouldRecordToggle() {
        tracker.recordToggle(player, List.of(GameModeOption.SPECTATOR, GameModeOption.CREATIVE));

        assertEquals(1, tracker.playerUses(player));
        assertEquals(1, tracker.playerToggleUses(player));
        assertEquals(1, tracker.playerModeUses(player, GameModeOption.SPECTATOR));
        assertEquals(1, tracker.playerModeUses(player, GameModeOption.CREATIVE));
        assertEquals(0, tracker.playerModeUses(player, GameModeOption.SURVIVAL));
        assertEquals(1, tracker.globalUses());
        assertEquals(1, tracker.globalToggleUses());
        assertEquals(1, tracker.globalModeUses(GameModeOption.CREATIVE));
    }

    @Test
    @DisplayName("should accumulate usages per player over time")
    void shouldAccumulateUsages() {
        tracker.recordSwitch(player, GameModeOption.SURVIVAL);
        tracker.recordSwitch(player, GameModeOption.SURVIVAL);
        tracker.recordToggle(player, List.of(GameModeOption.CREATIVE));

        assertEquals(3, tracker.playerUses(player));
        assertEquals(1, tracker.playerToggleUses(player));
        assertEquals(2, tracker.globalModeUses(GameModeOption.SURVIVAL));
        assertEquals(3, tracker.globalUses());
    }
}
