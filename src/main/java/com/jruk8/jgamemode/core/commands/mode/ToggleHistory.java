package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.storage.SqliteStorage;
import org.bukkit.GameMode;

import java.util.Optional;
import java.util.UUID;

/**
 * Toggle history: each player's last non-spectator gamemode, set only by
 * this plugin's own switches. Switching to spectator never touches the
 * cache; switching from spectator leaves it unchanged (spectator is not a
 * storable mode). Absent means the player has no toggle target.
 * <p>
 * Stored as the vanilla numeric id ({@code 0}-{@code 3}) under one stat
 * key, so the value survives restarts next to the usage counters.
 */
public final class ToggleHistory {

    /**
     * Stat key for the last non-spectator gamemode per player.
     */
    public static final String HISTORY_KEY = "jgm_toggle_history";

    private final SqliteStorage storage;

    public ToggleHistory(SqliteStorage storage) {
        this.storage = storage;
    }

    /**
     * Reads a player's toggle target.
     *
     * @param player the player
     * @return the last non-spectator gamemode, or empty when none is stored
     */
    public Optional<GameMode> lastMode(UUID player) {
        return storage.playerValue(HISTORY_KEY, player)
                .flatMap(ToggleHistory::toMode)
                .filter(mode -> mode != GameMode.SPECTATOR);
    }

    /**
     * Records a direct switch, caching the previous mode when both ends are
     * storable. Switching to spectator never overrides a stored return
     * ticket, but seeds an empty cache so the next toggle has somewhere to
     * go; same-mode switches and switches from spectator leave the cache
     * alone.
     *
     * @param player   the player that switched
     * @param previous the gamemode they switched from
     * @param current  the gamemode they switched to
     */
    public void recordSwitch(UUID player, GameMode previous, GameMode current) {
        if (previous == current || previous == GameMode.SPECTATOR) {
            return;
        }
        if (current == GameMode.SPECTATOR && lastMode(player).isPresent()) {
            return;
        }
        storage.setPlayerValue(HISTORY_KEY, player, previous.getValue());
    }

    /**
     * Records a toggle swap, caching the previous mode when it is storable.
     * Unlike a direct switch, leaving for spectator via toggle always
     * refreshes the return ticket; coming back from spectator never does.
     *
     * @param player   the player that toggled
     * @param previous the gamemode they toggled from
     */
    public void recordSwap(UUID player, GameMode previous) {
        if (previous == GameMode.SPECTATOR) {
            return;
        }
        storage.setPlayerValue(HISTORY_KEY, player, previous.getValue());
    }

    private static Optional<GameMode> toMode(int value) {
        for (GameMode mode : GameMode.values()) {
            if (mode.getValue() == value) {
                return Optional.of(mode);
            }
        }
        return Optional.empty();
    }
}
