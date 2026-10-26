package com.jruk8.jtemplate.core;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsBootstrap implements Bootstrap {

    private static final int PLUGIN_ID = 12345678; // Replace it with your actual plugin ID from bStats
    private final JavaPlugin plugin;
    private final boolean enabled;

    public MetricsBootstrap(JavaPlugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
    }

    @Override
    public void register() {
        // Read once at startup: bStats cannot be toggled at runtime, so the
        // config flag takes effect on server restart.
        if (enabled) {
            new Metrics(plugin, PLUGIN_ID);
        }
    }
}
