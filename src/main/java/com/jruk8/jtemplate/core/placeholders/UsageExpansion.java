package com.jruk8.jtemplate.core.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * The JTemplate PlaceholderAPI expansion.
 * <p>
 * Identifier {@code jtemplate}, with a self and an other-player variant of
 * the {@code uses} counter. Unknown players read as {@code 0}; the self
 * variant needs a player context.
 */
public final class UsageExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "jtemplate";
    public static final String USES = "uses";

    private final CommandUsageTracker tracker;
    private final JavaPlugin plugin;
    private final Function<String, UUID> nameResolver;

    public UsageExpansion(
            CommandUsageTracker tracker,
            JavaPlugin plugin,
            Function<String, UUID> nameResolver
    ) {
        this.tracker = tracker;
        this.plugin = plugin;
        this.nameResolver = nameResolver;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getAuthor() {
        List<String> authors = this.plugin.getPluginMeta().getAuthors();
        return authors.isEmpty() ? this.plugin.getName() : authors.getFirst();
    }

    @Override
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return this.plugin.getName();
    }

    @Override
    public List<String> getPlaceholders() {
        return List.of(
                "%" + IDENTIFIER + "_" + USES + "%",
                "%" + IDENTIFIER + "_" + USES + "_<player>%"
        );
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (USES.equals(params)) {
            return selfUses(player, this.tracker::playerUses);
        }
        if (params.startsWith(USES + "_")) {
            return namedUses(params.substring(USES.length() + 1), this.tracker::playerUses);
        }
        return null;
    }

    private static String selfUses(OfflinePlayer player, ToIntFunction<UUID> read) {
        return player == null ? null : String.valueOf(read.applyAsInt(player.getUniqueId()));
    }

    private String namedUses(String name, ToIntFunction<UUID> read) {
        if (name.isEmpty()) {
            return null;
        }
        return String.valueOf(read.applyAsInt(this.nameResolver.apply(name)));
    }
}
