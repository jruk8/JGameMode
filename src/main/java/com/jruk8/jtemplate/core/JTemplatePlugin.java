package com.jruk8.jtemplate.core;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.CommandsRegistrar;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.MessageBootstrap;
import com.jruk8.jtemplate.core.messages.Messenger;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.jruk8.jtemplate.core.placeholders.PlaceholdersBootstrap;
import com.jruk8.jtemplate.core.sounds.SoundPlayer;
import com.jruk8.jtemplate.core.storage.StorageBootstrap;
import com.jruk8.jtemplate.core.update.UpdateCheckerBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class JTemplatePlugin extends JavaPlugin {

    private StorageBootstrap storageBootstrap;

    @Override
    public void onEnable() {
        // Set up all core modules
        setupCoreModules();
    }

    @Override
    public void onDisable() {
        if (this.storageBootstrap != null) {
            this.storageBootstrap.shutdown();
        }
    }

    private void setupCoreModules() {
        var logger = getLogger();

        // Initialize reloader
        Reloader reloader = new Reloader(logger);

        // Initialize all configs
        var configRegistrar = new ConfigRegistrar(this);
        configRegistrar.register();
        reloader.register(configRegistrar);

        // Initialize messenger
        var messageRegistrar = new MessageBootstrap(configRegistrar);
        messageRegistrar.register();
        var messenger = messageRegistrar.getMessenger();

        // Initialize sound player
        var soundPlayer = new SoundPlayer(logger);

        // Initialize update checker
        var updateChecker = new UpdateCheckerBootstrap(
                this,
                configRegistrar.getPluginConfig(),
                messenger,
                configRegistrar.getMessagesConfig(),
                logger
        );
        updateChecker.register();
        reloader.register(updateChecker);

        // Initialize storage and placeholders first: commands record usage stats
        CommandUsageTracker usageTracker = setupStorageAndPlaceholders(logger, reloader, configRegistrar);

        // Initialize commands
        setupCommands(reloader, configRegistrar, messenger, soundPlayer, updateChecker, usageTracker);

        // Initialize bStats unless anonymous statistics are disabled
        boolean metricsEnabled = configRegistrar.getPluginConfig().getMetrics().isEnabled();
        var metricsBootstrap = new MetricsBootstrap(this, metricsEnabled);
        metricsBootstrap.register();
    }

    private void setupCommands(
            Reloader reloader,
            ConfigRegistrar configRegistrar,
            Messenger messenger,
            SoundPlayer soundPlayer,
            UpdateCheckerBootstrap updateChecker,
            CommandUsageTracker usageTracker
    ) {
        var context = new CommandContext(
                configRegistrar.getMessagesConfig(),
                configRegistrar.getSoundsConfig(),
                messenger,
                soundPlayer
        );
        var commandRegistrar = new CommandsRegistrar(
                getLifecycleManager(), reloader, context, getPluginMeta(),
                configRegistrar.getPluginConfig(), updateChecker.getService(), usageTracker);
        commandRegistrar.register();
    }

    private CommandUsageTracker setupStorageAndPlaceholders(
            Logger logger, Reloader reloader, ConfigRegistrar configRegistrar) {
        // Initialize SQLite storage
        this.storageBootstrap = new StorageBootstrap(configRegistrar.getPluginConfig(), getDataFolder(), logger);
        this.storageBootstrap.register();
        reloader.register(this.storageBootstrap);
        var usageTracker = new CommandUsageTracker(this.storageBootstrap.getStorage());

        // Initialize PlaceholderAPI placeholders
        var placeholdersBootstrap = new PlaceholdersBootstrap(usageTracker, this, logger);
        placeholdersBootstrap.register();
        reloader.register(placeholdersBootstrap);
        return usageTracker;
    }
}
