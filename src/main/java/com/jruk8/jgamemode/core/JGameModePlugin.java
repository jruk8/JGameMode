package com.jruk8.jgamemode.core;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.CommandsRegistrar;
import com.jruk8.jgamemode.core.commands.mode.GameModeExecutor;
import com.jruk8.jgamemode.core.commands.mode.GameUsageTracker;
import com.jruk8.jgamemode.core.commands.mode.ToggleHistory;
import com.jruk8.jgamemode.core.configs.ConfigRegistrar;
import com.jruk8.jgamemode.core.messages.MessageBootstrap;
import com.jruk8.jgamemode.core.messages.Messenger;
import com.jruk8.jgamemode.core.placeholders.PlaceholdersBootstrap;
import com.jruk8.jgamemode.core.sounds.SoundPlayer;
import com.jruk8.jgamemode.core.storage.SqliteStorage;
import com.jruk8.jgamemode.core.storage.StorageBootstrap;
import com.jruk8.jgamemode.core.update.UpdateCheckerBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class JGameModePlugin extends JavaPlugin {

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
                logger,
                this::printBanner
        );
        updateChecker.register();
        reloader.register(updateChecker);

        // Initialize storage
        var storage = setupStorage(logger, reloader, configRegistrar);
        var usageTracker = new GameUsageTracker(storage);
        var toggleHistory = new ToggleHistory(storage);

        // Initialize PlaceholderAPI placeholders
        var placeholdersBootstrap = new PlaceholdersBootstrap(usageTracker, this, logger);
        placeholdersBootstrap.register();
        reloader.register(placeholdersBootstrap);

        // Initialize commands
        setupCommands(reloader, configRegistrar, messenger, soundPlayer,
                updateChecker, usageTracker, toggleHistory);

        // Initialize bStats unless anonymous statistics are disabled
        boolean metricsEnabled = configRegistrar.getPluginConfig().getMetrics().isEnabled();
        var metricsBootstrap = new MetricsBootstrap(this, metricsEnabled);
        metricsBootstrap.register();
    }

    private void printBanner(boolean updateAvailable) {
        StartupBanner.print(
                Bukkit.getConsoleSender()::sendMessage,
                getName(),
                getPluginMeta().getVersion(),
                updateAvailable);
    }

    private void setupCommands(
            Reloader reloader,
            ConfigRegistrar configRegistrar,
            Messenger messenger,
            SoundPlayer soundPlayer,
            UpdateCheckerBootstrap updateChecker,
            GameUsageTracker usageTracker,
            ToggleHistory toggleHistory
    ) {
        var context = new CommandContext(
                configRegistrar.getMessagesConfig(),
                configRegistrar.getSoundsConfig(),
                messenger,
                soundPlayer
        );
        var modeExecutor = new GameModeExecutor(context, toggleHistory, usageTracker);
        var commandRegistrar = new CommandsRegistrar(
                getLifecycleManager(), reloader, context, getPluginMeta(),
                configRegistrar.getPluginConfig(), modeExecutor, updateChecker.getService(), this);
        commandRegistrar.register();
    }

    private SqliteStorage setupStorage(
            Logger logger, Reloader reloader, ConfigRegistrar configRegistrar) {
        // Initialize SQLite storage
        this.storageBootstrap = new StorageBootstrap(configRegistrar.getPluginConfig(), getDataFolder(), logger);
        this.storageBootstrap.register();
        reloader.register(this.storageBootstrap);
        return this.storageBootstrap.getStorage();
    }
}
