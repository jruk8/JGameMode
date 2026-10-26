package com.jruk8.jgamemode.core.commands.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Builds the tab-completion candidates for a command's target selector argument.
 * <p>
 * Senders with the {@code other} permission get the vanilla selectors first,
 * then online player names with their own name first; senders without it only
 * get {@code @s} and their own name. Candidates are always prefix-filtered.
 */
public final class SelectorSuggestions {

    /**
     * Vanilla selectors, in the order they are suggested.
     */
    public static final List<String> SELECTORS = List.of("@p", "@a", "@r", "@s");

    private SelectorSuggestions() {
    }

    /**
     * Returns the completion candidates for a target selector argument.
     *
     * @param canTargetOthers whether the sender may target other players
     * @param executorName the sender's own player name, or blank when the sender is not a player
     * @param onlineNames the names of all online players
     * @param prefix the already-typed, lower-cased input used for filtering (may be null)
     * @return the filtered candidates: selectors first, then names with the executor first
     */
    public static List<String> suggest(
            boolean canTargetOthers,
            String executorName,
            Collection<String> onlineNames,
            String prefix
    ) {
        List<String> candidates = new ArrayList<>();
        if (canTargetOthers) {
            candidates.addAll(SELECTORS);
            candidates.addAll(orderNames(executorName, onlineNames));
        } else {
            candidates.add("@s");
            if (executorName != null && !executorName.isBlank()) {
                candidates.add(executorName);
            }
        }
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return candidates.stream()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(needle))
                .toList();
    }

    private static List<String> orderNames(String executorName, Collection<String> onlineNames) {
        List<String> names = new ArrayList<>();
        if (executorName != null && onlineNames.contains(executorName)) {
            names.add(executorName);
        }
        for (String name : onlineNames) {
            if (!name.equals(executorName)) {
                names.add(name);
            }
        }
        return names;
    }
}
