package com.jruk8.jtemplate.core.commands.mode;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.GameModePermissions;
import com.jruk8.jtemplate.core.commands.selectors.TargetSelector;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;

/**
 * A toggle root ({@code gmt}, {@code t}): bare toggles the sender between
 * spectator and their last non-spectator mode, an optional selector toggles
 * those targets.
 */
public class ToggleCommand {

    private final CommandContext ctx;
    private final GameModeExecutor exec;

    public ToggleCommand(CommandContext ctx, GameModeExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the node for the given label.
     *
     * @param label the root label
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node(String label) {
        return Commands.literal(label)
                .requires(CommandContext.requiresPermission(GameModePermissions.USE))
                .executes(context -> {
                    var targets = TargetSelector.self(
                            context.getSource(), ctx, m -> m.getCore().getConsoleNeedsTarget());
                    return targets.map(players -> exec.toggle(context.getSource(), players)).orElse(0);
                })
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .suggests((context, builder) -> {
                            for (String candidate : TargetSelector.suggestTargets(
                                    context.getSource(), builder.getRemainingLowerCase())) {
                                builder.suggest(candidate);
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            PlayerSelectorArgumentResolver selector = context.getArgument(
                                    "targets", PlayerSelectorArgumentResolver.class);
                            var targets = TargetSelector.fromSelector(context.getSource(), selector, ctx);
                            return targets
                                    .map(players -> exec.toggle(context.getSource(), players))
                                    .orElse(0);
                        }));
    }
}
