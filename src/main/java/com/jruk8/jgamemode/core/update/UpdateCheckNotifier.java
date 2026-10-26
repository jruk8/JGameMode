package com.jruk8.jgamemode.core.update;

import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.jruk8.jgamemode.core.messages.MessagesConfig;
import com.jruk8.jgamemode.core.messages.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Sends update notices to players holding {@code jgamemode.reload}.
 */
public final class UpdateCheckNotifier {

    private final Messenger messenger;
    private final MessagesConfig messages;

    public UpdateCheckNotifier(Messenger messenger, MessagesConfig messages) {
        this.messenger = messenger;
        this.messages = messages;
    }

    /**
     * Sends an update notice to every permitted online player.
     *
     * @param update the found update
     */
    public void notifyAdmins(UpdateAvailable update) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            notifyPlayer(player, update);
        }
    }

    /**
     * Sends an update notice to one player when permitted.
     *
     * @param player the player to notify
     * @param update the found update
     */
    public void notifyPlayer(Player player, UpdateAvailable update) {
        if (player == null || update == null || !player.hasPermission(GameModePermissions.RELOAD)) {
            return;
        }
        var tags = Map.of(
                "latest", update.latest(),
                "current", update.current(),
                "url", update.url(),
                "modrinth-url", update.modrinthUrl());
        messenger.send(player, messages.getCore().getUpdateAvailable(), tags);
    }
}
