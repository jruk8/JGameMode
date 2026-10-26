package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.jruk8.jgamemode.core.commands.selectors.ValueSuggestions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Builds the tab-completion candidates for the mode argument.
 * <p>
 * Full names come first (only modes the sender may switch to), then
 * {@code toggle} when a toggle is plausible, then the shorthands.
 * Candidates are always prefix-filtered, so typing {@code surv} narrows
 * the list to survival alone.
 */
public final class ModeSuggestions {

    /**
     * The toggle keyword, suggested like a full name.
     */
    public static final String TOGGLE = "toggle";

    private ModeSuggestions() {
    }

    /**
     * Returns the completion candidates for a mode argument.
     *
     * @param source the command source completing the argument
     * @param executor the gamemode executor, used to resolve toggle targets
     * @param prefix the already-typed input used for filtering (may be null)
     * @return full names first, then toggle, then shorthands, all filtered
     */
    public static List<String> suggest(
            CommandSourceStack source,
            GameModeExecutor executor,
            String prefix
    ) {
        CommandSender sender = source.getSender();
        if (!sender.hasPermission(GameModePermissions.USE)) {
            return List.of();
        }
        boolean self = sender.hasPermission(GameModePermissions.SELF);
        boolean other = sender.hasPermission(GameModePermissions.OTHER);
        if (!self && !other) {
            return List.of();
        }
        List<GameModeOption> permitted = new ArrayList<>();
        for (GameModeOption option : GameModeOption.values()) {
            if (sender.hasPermission(GameModePermissions.modeNode(option))) {
                permitted.add(option);
            }
        }
        List<String> candidates = new ArrayList<>();
        for (GameModeOption option : permitted) {
            candidates.add(option.key());
        }
        if (includeToggle(sender, self, other, executor)) {
            candidates.add(TOGGLE);
        }
        for (GameModeOption option : permitted) {
            candidates.add(option.shorthand());
        }
        return ValueSuggestions.suggest(candidates, prefix);
    }

    private static boolean includeToggle(
            CommandSender sender,
            boolean self,
            boolean other,
            GameModeExecutor executor
    ) {
        if (other) {
            return true;
        }
        if (self && sender instanceof Player player) {
            Optional<GameModeOption> target = executor.toggleTarget(player, sender);
            return target.isPresent() && sender.hasPermission(GameModePermissions.modeNode(target.get()));
        }
        return false;
    }
}
