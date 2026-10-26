package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.storage.SqliteStorage;

import java.util.UUID;

/**
 * Domain service for the command usage statistics behind the
 * {@code jtemplate} placeholders. Keeps the DB stat key in one place and
 * exposes the read/write operations the expansion and commands need.
 */
public final class CommandUsageTracker {

    /**
     * Stat key for total tracked command executions per player.
     */
    public static final String USES_KEY = "jtemplate_uses";

    private final SqliteStorage storage;

    public CommandUsageTracker(SqliteStorage storage) {
        this.storage = storage;
    }

    /**
     * Records one execution of a tracked command.
     *
     * @param player the player that executed the command
     */
    public void recordUse(UUID player) {
        this.storage.increment(USES_KEY, player);
    }

    public int playerUses(UUID player) {
        return this.storage.playerTotal(USES_KEY, player);
    }
}
