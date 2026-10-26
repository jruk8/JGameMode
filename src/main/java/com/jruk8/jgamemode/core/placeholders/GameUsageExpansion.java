package com.jruk8.jgamemode.core.placeholders;

import com.jruk8.jgamemode.core.commands.mode.GameModeOption;
import com.jruk8.jgamemode.core.commands.mode.GameUsageTracker;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * The JGameMode PlaceholderAPI expansion.
 * <p>
 * Identifier {@code jgm}, with self, other-player and global variants per
 * counter: {@code uses}, {@code toggle_uses} and one per gamemode
 * ({@code survival_uses}, {@code creative_uses}, {@code adventure_uses},
 * {@code spectator_uses}), each optionally followed by {@code _<player>}
 * or {@code _global}. Unknown players read as {@code 0}; the self
 * variants need a player context.
 */
public final class GameUsageExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "jgm";
    public static final String USES = "uses";
    public static final String TOGGLE_USES = "toggle_uses";
    public static final String GLOBAL = "global";

    private final GameUsageTracker tracker;
    private final JavaPlugin plugin;
    private final Function<String, UUID> nameResolver;

    public GameUsageExpansion(
            GameUsageTracker tracker,
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
        List<String> placeholders = new ArrayList<>();
        for (String suffix : suffixes()) {
            placeholders.add("%" + IDENTIFIER + "_" + suffix + "%");
            placeholders.add("%" + IDENTIFIER + "_" + suffix + "_<player>%");
            placeholders.add("%" + IDENTIFIER + "_" + suffix + "_" + GLOBAL + "%");
        }
        return placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        for (String suffix : suffixes()) {
            if (suffix.equals(params)) {
                return selfUses(player, readSelf(suffix));
            }
            if ((suffix + "_" + GLOBAL).equals(params)) {
                return String.valueOf(readGlobal(suffix));
            }
            if (params.startsWith(suffix + "_")) {
                return namedUses(params.substring(suffix.length() + 1), readSelf(suffix));
            }
        }
        return null;
    }

    private static List<String> suffixes() {
        List<String> suffixes = new ArrayList<>(List.of(USES, TOGGLE_USES));
        for (GameModeOption option : GameModeOption.values()) {
            suffixes.add(option.key() + "_uses");
        }
        return suffixes;
    }

    private static GameModeOption modeFor(String suffix) {
        for (GameModeOption option : GameModeOption.values()) {
            if ((option.key() + "_uses").equals(suffix)) {
                return option;
            }
        }
        return null;
    }

    private ToIntFunction<UUID> readSelf(String suffix) {
        if (TOGGLE_USES.equals(suffix)) {
            return this.tracker::playerToggleUses;
        }
        GameModeOption option = modeFor(suffix);
        if (option != null) {
            return id -> this.tracker.playerModeUses(id, option);
        }
        return this.tracker::playerUses;
    }

    private int readGlobal(String suffix) {
        if (TOGGLE_USES.equals(suffix)) {
            return this.tracker.globalToggleUses();
        }
        GameModeOption option = modeFor(suffix);
        if (option != null) {
            return this.tracker.globalModeUses(option);
        }
        return this.tracker.globalUses();
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
