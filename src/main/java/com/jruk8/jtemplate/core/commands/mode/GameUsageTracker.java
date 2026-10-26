package com.jruk8.jtemplate.core.commands.mode;

import com.jruk8.jtemplate.core.storage.SqliteStorage;

import java.util.List;
import java.util.UUID;

/**
 * Domain service for the gamemode usage statistics behind the
 * {@code jgm} placeholders. Keeps the DB stat keys in one place and exposes
 * the read/write operations the expansion and commands need.
 * <p>
 * Every successful switch records one overall use plus one use on the
 * resulting channel; every toggle additionally records one toggle use.
 * Console executions are not attributed to any player and are not counted.
 */
public final class GameUsageTracker {

    /**
     * Stat key for total gamemode command executions per player.
     */
    public static final String USES_KEY = "jgm_uses";

    /**
     * Stat key for toggle executions per player.
     */
    public static final String TOGGLE_USES_KEY = "jgm_toggle_uses";

    private final SqliteStorage storage;

    public GameUsageTracker(SqliteStorage storage) {
        this.storage = storage;
    }

    /**
     * Records one switch to the given mode.
     *
     * @param player the player that executed the command
     * @param option the resulting gamemode
     */
    public void recordSwitch(UUID player, GameModeOption option) {
        this.storage.increment(USES_KEY, player);
        this.storage.increment(option.statKey(), player);
        this.storage.incrementGlobal(USES_KEY);
        this.storage.incrementGlobal(option.statKey());
    }

    /**
     * Records one toggle use, on top of the switch it performs: one overall
     * use, one toggle use, and one use on each distinct resulting channel.
     *
     * @param player the player that executed the command
     * @param modes the distinct resulting gamemodes
     */
    public void recordToggle(UUID player, List<GameModeOption> modes) {
        this.storage.increment(USES_KEY, player);
        this.storage.incrementGlobal(USES_KEY);
        this.storage.increment(TOGGLE_USES_KEY, player);
        this.storage.incrementGlobal(TOGGLE_USES_KEY);
        for (GameModeOption mode : modes) {
            this.storage.increment(mode.statKey(), player);
            this.storage.incrementGlobal(mode.statKey());
        }
    }

    public int playerUses(UUID player) {
        return this.storage.playerTotal(USES_KEY, player);
    }

    public int playerToggleUses(UUID player) {
        return this.storage.playerTotal(TOGGLE_USES_KEY, player);
    }

    public int playerModeUses(UUID player, GameModeOption option) {
        return this.storage.playerTotal(option.statKey(), player);
    }

    public int globalUses() {
        return this.storage.globalTotal(USES_KEY);
    }

    public int globalToggleUses() {
        return this.storage.globalTotal(TOGGLE_USES_KEY);
    }

    public int globalModeUses(GameModeOption option) {
        return this.storage.globalTotal(option.statKey());
    }
}
