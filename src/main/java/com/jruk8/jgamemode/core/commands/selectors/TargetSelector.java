package com.jruk8.jgamemode.core.commands.selectors;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.jruk8.jgamemode.core.messages.MessagesConfig;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves which players a command acts on.
 * <p>
 * An omitted selector means the executor themselves (console gets an error).
 * An explicit selector resolving to anyone but the sender additionally
 * requires {@code jgamemode.other}; a selector resolving only to the sender
 * (e.g. {@code @s}) needs just the command's own permission.
 */
public final class TargetSelector {

    private TargetSelector() {
    }

    /**
     * Resolves an omitted selector to the executing player.
     *
     * @param source the command source
     * @param ctx the shared command services
     * @param consoleMessage selects the error sent when the sender is not a player
     * @return the executor alone, or empty when the sender is not a player (error already sent)
     */
    public static Optional<List<Player>> self(
            CommandSourceStack source,
            CommandContext ctx,
            Function<MessagesConfig, String> consoleMessage
    ) {
        if (source.getSender() instanceof Player player) {
            return Optional.of(List.of(player));
        }
        ctx.error(source, consoleMessage, Map.of());
        return Optional.empty();
    }

    /**
     * Resolves an explicit selector, enforcing the {@code other} permission.
     *
     * @param source the command source
     * @param selector the parsed selector argument
     * @param ctx the shared command services
     * @return the resolved players, or empty when resolution fails, resolves to
     *         nobody, or the sender lacks permission (an error is already sent in
     *         all cases)
     */
    public static Optional<List<Player>> fromSelector(
            CommandSourceStack source,
            PlayerSelectorArgumentResolver selector,
            CommandContext ctx
    ) {
        List<Player> targets;
        try {
            targets = selector.resolve(source);
        } catch (CommandSyntaxException unmatched) {
            ctx.error(source, m -> m.getCore().getNoTargets(), Map.of());
            return Optional.empty();
        }
        if (targets.isEmpty()) {
            ctx.error(source, m -> m.getCore().getNoTargets(), Map.of());
            return Optional.empty();
        }
        if (targetsOthers(source, targets) && !source.getSender().hasPermission(GameModePermissions.OTHER)) {
            var tags = Map.of("permission", GameModePermissions.OTHER);
            ctx.error(source, m -> m.getCore().getNoPermission(), tags);
            return Optional.empty();
        }
        return Optional.of(targets);
    }

    private static boolean targetsOthers(CommandSourceStack source, List<Player> targets) {
        if (!(source.getSender() instanceof Player self)) {
            return true;
        }
        return targets.stream().anyMatch(target -> !target.getUniqueId().equals(self.getUniqueId()));
    }

    /**
     * Returns the tab-completion candidates for a target selector argument,
     * gated by permission: without the {@code other} permission only the
     * {@code @s} and own-name entries appear, and without the {@code self}
     * permission those self entries are removed as well.
     *
     * @param source the command source completing the argument
     * @param prefix the already-typed, lower-cased input used for filtering (may be null)
     * @return the permitted selector candidates
     */
    public static List<String> suggestTargets(CommandSourceStack source, String prefix) {
        CommandSender sender = source.getSender();
        boolean other = sender.hasPermission(GameModePermissions.OTHER);
        boolean self = sender.hasPermission(GameModePermissions.SELF);
        String name = sender instanceof Player player ? player.getName() : "";
        List<String> online = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            online.add(player.getName());
        }
        List<String> candidates = new ArrayList<>(SelectorSuggestions.suggest(other, name, online, prefix));
        if (!self) {
            candidates.removeIf(candidate -> candidate.equals("@s") || candidate.equals(name));
        }
        return candidates;
    }
}
