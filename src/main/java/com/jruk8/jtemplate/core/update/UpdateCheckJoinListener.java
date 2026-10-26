package com.jruk8.jtemplate.core.update;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Repeats a pending update notice to joining permitted players.
 */
public final class UpdateCheckJoinListener implements Listener {

    private final UpdateCheckService service;
    private final UpdateCheckNotifier notifier;

    public UpdateCheckJoinListener(UpdateCheckService service, UpdateCheckNotifier notifier) {
        this.service = service;
        this.notifier = notifier;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        service.pendingUpdate()
                .ifPresent(update -> notifier.notifyPlayer(event.getPlayer(), update));
    }
}
