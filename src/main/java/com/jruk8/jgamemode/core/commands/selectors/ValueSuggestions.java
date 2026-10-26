package com.jruk8.jgamemode.core.commands.selectors;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Builds the tab-completion candidates for a command's value argument.
 * <p>
 * The configured order is kept; blank entries are dropped.
 * Candidates are always prefix-filtered.
 */
public final class ValueSuggestions {

    private ValueSuggestions() {
    }

    /**
     * Returns the completion candidates for a value argument.
     *
     * @param configured the configured defaults, in suggestion order
     * @param prefix the already-typed, lower-cased input used for filtering (may be null)
     * @return the filtered candidates
     */
    public static List<String> suggest(List<String> configured, String prefix) {
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return configured.stream()
                .filter(Objects::nonNull)
                .filter(candidate -> !candidate.isBlank())
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(needle))
                .distinct()
                .toList();
    }
}
