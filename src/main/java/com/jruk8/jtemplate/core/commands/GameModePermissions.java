package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.commands.mode.GameModeOption;

/**
 * Permission nodes used by the JGameMode commands, declared in
 * {@code paper-plugin.yml}.
 */
public final class GameModePermissions {

    /**
     * Switch gamemodes. Master switch: required for every switch, together
     * with the scope ({@link #SELF} or {@link #OTHER}) and the mode node.
     */
    public static final String USE = "jgamemode.use";

    /**
     * Switch to survival. Opt-out style: denied when explicitly set false.
     */
    public static final String USE_SURVIVAL = "jgamemode.use.survival";

    /**
     * Switch to creative. Opt-out style: denied when explicitly set false.
     */
    public static final String USE_CREATIVE = "jgamemode.use.creative";

    /**
     * Switch to adventure. Opt-out style: denied when explicitly set false.
     */
    public static final String USE_ADVENTURE = "jgamemode.use.adventure";

    /**
     * Switch to spectator. Opt-out style: denied when explicitly set false.
     */
    public static final String USE_SPECTATOR = "jgamemode.use.spectator";

    /**
     * Switch your own gamemode. Needs {@link #USE} as well.
     */
    public static final String SELF = "jgamemode.self";

    /**
     * Switch other players' gamemode. Needs {@link #USE} as well.
     */
    public static final String OTHER = "jgamemode.other";

    /**
     * Reload the plugin configuration.
     */
    public static final String RELOAD = "jgamemode.reload";

    /**
     * View the plugin help listing. Entries are filtered by permission.
     */
    public static final String HELP = "jgamemode.help";

    /**
     * View the plugin version.
     */
    public static final String VERSION = "jgamemode.version";

    private GameModePermissions() {
    }

    /**
     * Builds the per-mode node for a gamemode option.
     *
     * @param option the gamemode option
     * @return the {@code jgamemode.use.<mode>} node
     */
    public static String modeNode(GameModeOption option) {
        return USE + "." + option.key();
    }
}
