package com.jruk8.jgamemode.core.update;

import com.jruk8.jgamemode.core.Bootstrap;
import com.jruk8.jgamemode.core.Reloadable;
import com.jruk8.jgamemode.core.configs.PluginConfig;
import com.jruk8.jgamemode.core.messages.MessagesConfig;
import com.jruk8.jgamemode.core.messages.Messenger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Wires the update checker: registers the join listener and runs the
 * first check asynchronously. Reloading re-reads the toggles and
 * checks again.
 */
public class UpdateCheckerBootstrap implements Bootstrap, Reloadable {

    @Getter
    private final UpdateCheckService service;
    private final UpdateCheckNotifier notifier;
    private final JavaPlugin plugin;
    private final PluginConfig pluginConfig;
    private final String currentVersion;
    private final Consumer<Boolean> firstCheckCallback;

    private volatile boolean firstCheckDone;

    public UpdateCheckerBootstrap(
            JavaPlugin plugin,
            PluginConfig pluginConfig,
            Messenger messenger,
            MessagesConfig messagesConfig,
            Logger logger,
            Consumer<Boolean> firstCheckCallback
    ) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.currentVersion = plugin.getPluginMeta().getVersion();
        this.notifier = new UpdateCheckNotifier(messenger, messagesConfig);
        this.service = new UpdateCheckService(new GitHubReleaseHttp(currentVersion), logger);
        this.firstCheckCallback = firstCheckCallback;
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(
                new UpdateCheckJoinListener(service, notifier), plugin);
        checkAsync();
    }

    @Override
    public void reload() {
        checkAsync();
    }

    private void checkAsync() {
        Bukkit.getAsyncScheduler().runNow(plugin, async -> {
            service.checkNow(
                    UpdateCheckSettings.fromConfig(pluginConfig.getUpdateChecker()),
                    currentVersion
            );
            if (!firstCheckDone) {
                firstCheckDone = true;
                firstCheckCallback.accept(service.pendingUpdate().isPresent());
            }
            service.pendingUpdate().ifPresent(update ->
                    Bukkit.getGlobalRegionScheduler().run(plugin, sync -> notifier.notifyAdmins(update)));
        });
    }
}
