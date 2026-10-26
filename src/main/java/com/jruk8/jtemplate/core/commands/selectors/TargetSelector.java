package com.jruk8.jtemplate.core.commands.selectors;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.TemplatePermissions;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves which players a command acts on.
 * <p>
 * An omitted selector means the executor themselves (console gets an error).
 * An explicit selector resolving to anyone but the sender additionally
 * requires {@code jtemplate.other}; a selector resolving only to the sender
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
        if (targetsOthers(source, targets) && !source.getSender().hasPermission(TemplatePermissions.OTHER)) {
            var tags = Map.of("permission", TemplatePermissions.OTHER);
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
}
