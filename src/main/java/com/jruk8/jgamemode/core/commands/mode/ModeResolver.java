package com.jruk8.jgamemode.core.commands.mode;

import java.util.Locale;
import java.util.Optional;

/**
 * Parses a raw mode token into a gamemode, a toggle request, or invalid.
 * <p>
 * Resolution order: exact shorthand ({@code s}, {@code c}, {@code a},
 * {@code sp}), exact number ({@code 0}-{@code 3}), exact full name
 * ({@code survival}, case-insensitive), toggle prefix ({@code t},
 * {@code tog}, {@code toggle}), then any case-insensitive substring of a
 * full name ({@code creat} to creative). Ambiguous substrings pick the
 * first mode in {@link GameModeOption} order. Genuine gibberish resolves
 * to invalid.
 */
public final class ModeResolver {

    /**
     * A parsed mode token.
     */
    public sealed interface Result permits Result.Mode, Result.Toggle, Result.Invalid {

        /**
         * @param option the resolved gamemode
         */
        record Mode(GameModeOption option) implements Result {
        }

        /**
         * The token asked for a toggle.
         */
        record Toggle() implements Result {
        }

        /**
         * The token matched nothing.
         */
        record Invalid() implements Result {
        }
    }

    private ModeResolver() {
    }

    /**
     * Parses one mode token.
     *
     * @param raw the raw token, may be null or blank
     * @return the resolution: a mode, a toggle, or invalid
     */
    public static Result parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Result.Invalid();
        }
        String token = raw.trim().toLowerCase(Locale.ROOT);
        for (GameModeOption option : GameModeOption.values()) {
            if (token.equals(option.shorthand()) || token.equals(option.number()) || token.equals(option.key())) {
                return new Result.Mode(option);
            }
        }
        if ("toggle".startsWith(token)) {
            return new Result.Toggle();
        }
        for (GameModeOption option : GameModeOption.values()) {
            if (option.key().contains(token)) {
                return new Result.Mode(option);
            }
        }
        return new Result.Invalid();
    }

    /**
     * Parses one mode token, expecting a gamemode (never a toggle).
     *
     * @param raw the raw token, may be null or blank
     * @return the resolved gamemode, or empty for toggles and gibberish
     */
    public static Optional<GameModeOption> parseMode(String raw) {
        if (parse(raw) instanceof Result.Mode mode) {
            return Optional.of(mode.option());
        }
        return Optional.empty();
    }
}
