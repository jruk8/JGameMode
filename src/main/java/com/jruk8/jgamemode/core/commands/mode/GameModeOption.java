package com.jruk8.jgamemode.core.commands.mode;

import org.bukkit.GameMode;

/**
 * The four gamemodes, with their display names, short aliases, numeric ids,
 * permission suffixes and stat keys.
 * <p>
 * Order matters: dynamic substring resolution picks the first match in this
 * order, and tab completion lists full names in this order.
 */
public enum GameModeOption {

    SURVIVAL(GameMode.SURVIVAL, "Survival", "s", "0", "survival", "jgm_survival_uses"),
    CREATIVE(GameMode.CREATIVE, "Creative", "c", "1", "creative", "jgm_creative_uses"),
    ADVENTURE(GameMode.ADVENTURE, "Adventure", "a", "2", "adventure", "jgm_adventure_uses"),
    SPECTATOR(GameMode.SPECTATOR, "Spectator", "sp", "3", "spectator", "jgm_spectator_uses");

    private final GameMode gameMode;
    private final String display;
    private final String shorthand;
    private final String number;
    private final String key;
    private final String statKey;

    GameModeOption(GameMode gameMode, String display, String shorthand, String number, String key, String statKey) {
        this.gameMode = gameMode;
        this.display = display;
        this.shorthand = shorthand;
        this.number = number;
        this.key = key;
        this.statKey = statKey;
    }

    public GameMode gameMode() {
        return gameMode;
    }

    /**
     * @return the display name, first letter upper, e.g. {@code "Creative"}
     */
    public String display() {
        return display;
    }

    /**
     * @return the one- or two-letter shorthand, e.g. {@code "sp"}
     */
    public String shorthand() {
        return shorthand;
    }

    /**
     * @return the vanilla numeric id, e.g. {@code "1"}
     */
    public String number() {
        return number;
    }

    /**
     * @return the lowercase full name, e.g. {@code "creative"}
     */
    public String key() {
        return key;
    }

    /**
     * @return the per-player/global stat key for switches to this mode
     */
    public String statKey() {
        return statKey;
    }

    /**
     * @return whether this is one of the three toggle-storable modes
     */
    public boolean storable() {
        return gameMode != GameMode.SPECTATOR;
    }

    public static GameModeOption fromGameMode(GameMode gameMode) {
        for (GameModeOption option : values()) {
            if (option.gameMode == gameMode) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown gamemode: " + gameMode);
    }

}
