package com.jruk8.jgamemode.core.sounds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Handles the resolution and playback of namespaced sounds.
 * <p>
 * Acts as a bridge between {@link SoundsConfig.Sound} definitions and
 * Adventure's sound API. Unknown or invalid sound keys are logged and
 * skipped rather than throwing.
 */
public final class SoundPlayer {

    private final Logger logger;

    /**
     * Constructs a new {@code SoundPlayer} with the given logger.
     *
     * @param logger the logger used to report unresolvable sound keys
     */
    public SoundPlayer(Logger logger) {
        this.logger = logger;
    }

    /**
     * Plays the given sound to a recipient, if enabled.
     * <p>
     * Sounds require a player to hear them; senders that are not a
     * {@link Player} (e.g. console) are silently ignored.
     *
     * @param sender the recipient of the sound; if null or not a {@link Player}, the operation is ignored
     * @param sound  the sound definition to play; if null or disabled, the operation is ignored
     */
    public void play(CommandSender sender, SoundsConfig.Sound sound) {
        if (!(sender instanceof Player player)) {
            return;
        }
        play(player, sound);
    }

    /**
     * Plays the given sound to a player, if enabled.
     *
     * @param player the recipient of the sound; if null, operation is ignored
     * @param sound  the sound definition to play; if null or disabled, the operation is ignored
     */
    public void play(Player player, SoundsConfig.Sound sound) {
        if (player == null || sound == null || !sound.isEnabled()) {
            return;
        }

        Sound cue = resolveCue(sound);
        if (cue == null) {
            return;
        }

        player.playSound(cue);
    }

    /**
     * Plays the given sound to every online player, if enabled.
     *
     * @param sound the sound definition to play; if null or disabled, the operation is ignored
     */
    public void broadcast(SoundsConfig.Sound sound) {
        if (sound == null || !sound.isEnabled()) {
            return;
        }

        Sound cue = resolveCue(sound);
        if (cue == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(cue);
        }
    }

    /**
     * Resolves a {@link SoundsConfig.Sound} definition to an Adventure cue.
     *
     * @param sound the sound definition to resolve
     * @return the Adventure sound, or {@code null} if the key is invalid or unknown
     */
    private Sound resolveCue(SoundsConfig.Sound sound) {
        String key = sound.getSound();
        if (key == null || key.isBlank()) {
            logger.warning("Sound key is null or empty.");
            return null;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key.toLowerCase(Locale.ROOT));
        if (namespacedKey == null) {
            logger.warning("Invalid sound key: '" + key + "'");
            return null;
        }
        if (Registry.SOUNDS.get(namespacedKey) == null) {
            logger.warning("Unknown sound key: '" + key + "'");
            return null;
        }

        return Sound.sound(
                Key.key(namespacedKey.asString()),
                Sound.Source.MASTER,
                sound.getVolume(),
                sound.getPitch()
        );
    }
}
