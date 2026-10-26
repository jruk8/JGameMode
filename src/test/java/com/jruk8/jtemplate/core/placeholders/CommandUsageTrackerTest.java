package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.storage.SqliteStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandUsageTrackerTest {

    private final UUID player = UUID.randomUUID();

    private SqliteStorage storage;
    private CommandUsageTracker tracker;

    @BeforeEach
    void setUp() {
        storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("tracker-test"));
        assertTrue(storage.init());
        tracker = new CommandUsageTracker(storage);
    }

    @AfterEach
    void tearDown() {
        storage.close();
    }

    @Test
    @DisplayName("should start at zero")
    void shouldStartAtZero() {
        assertEquals(0, tracker.playerUses(player));
    }

    @Test
    @DisplayName("should record one use per execution")
    void shouldRecordUse() {
        tracker.recordUse(player);

        assertEquals(1, tracker.playerUses(player));
    }

    @Test
    @DisplayName("should accumulate usages per player over time")
    void shouldAccumulateUsages() {
        tracker.recordUse(player);
        tracker.recordUse(player);
        tracker.recordUse(UUID.randomUUID());

        assertEquals(2, tracker.playerUses(player));
    }
}
