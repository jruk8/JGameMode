package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloadable;
import com.jruk8.jtemplate.core.commands.mode.GameUsageTracker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Registers and tears down the PlaceholderAPI expansion.
 * <p>
 * When PlaceholderAPI is not installed, registration is skipped gracefully.
 * {@link #reload()} re-registers everything.
 */
public final class PlaceholdersBootstrap implements Bootstrap, Reloadable {

    private static final String PLACEHOLDER_API_NAME = "PlaceholderAPI";

    private final GameUsageTracker tracker;
    private final JavaPlugin plugin;
    private final Logger logger;

    private GameUsageExpansion expansion;

    public PlaceholdersBootstrap(GameUsageTracker tracker, JavaPlugin plugin, Logger logger) {
        this.tracker = tracker;
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void register() {
        if (this.plugin.getServer().getPluginManager().getPlugin(PLACEHOLDER_API_NAME) == null) {
            this.logger.info("PlaceholderAPI is not installed; skipping placeholder registration.");
            return;
        }
        this.expansion = new GameUsageExpansion(
                this.tracker, this.plugin, name -> Bukkit.getOfflinePlayer(name).getUniqueId());
        if (this.expansion.register()) {
            this.logger.info("Registered placeholders with identifier '" + GameUsageExpansion.IDENTIFIER + "'.");
        } else {
            unregister();
            this.logger.warning("PlaceholderAPI rejected the expansion registration.");
        }
    }

    @Override
    public void reload() {
        unregister();
        register();
    }

    public boolean unregister() {
        if (this.expansion != null) {
            this.expansion.unregister();
            this.expansion = null;
        }
        return true;
    }
}
