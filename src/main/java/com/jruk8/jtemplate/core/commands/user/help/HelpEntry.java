package com.jruk8.jtemplate.core.commands.user.help;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

/**
 * One row of the {@code /jgamemode help} listing: the syntax players type, a short description,
 * and the permissions that gate its visibility.
 *
 * @param syntax the command syntax, e.g. {@code "/jgamemode version"}
 * @param description the short help text for the command
 * @param permissions the permissions, one of which a sender must hold to see this entry;
 *                    empty means visible to everyone
 */
public record HelpEntry(String syntax, String description, String... permissions) {

    /**
     * Returns whether a sender may see this entry (holds any of its permissions, or it has none).
     *
     * @param sender the sender to check
     * @return {@code true} when the entry should be listed
     */
    public boolean visibleTo(CommandSender sender) {
        if (permissions.length == 0) {
            return true;
        }
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters entries by an optional search query, matching case-insensitively against syntax and description.
     * A null or blank query matches every entry.
     *
     * @param entries the entries to filter
     * @param query the search query, may be null or blank
     * @return the matching entries, in their original order
     */
    public static List<HelpEntry> matching(List<HelpEntry> entries, String query) {
        if (query == null || query.isBlank()) {
            return List.copyOf(entries);
        }
        String needle = query.toLowerCase(Locale.ROOT);
        return entries.stream()
                .filter(entry -> entry.syntax().toLowerCase(Locale.ROOT).contains(needle)
                        || entry.description().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
