package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.jruk8.jgamemode.core.commands.selectors.TargetSelector;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;

/**
 * A direct gamemode root ({@code gms}, {@code gmc}, {@code gma},
 * {@code gmsp}, or a single-letter root): bare switches the sender, an
 * optional selector switches those targets.
 */
public class DirectModeCommand {

    private final CommandContext ctx;
    private final GameModeExecutor exec;
    private final GameModeOption option;

    public DirectModeCommand(CommandContext ctx, GameModeExecutor exec, GameModeOption option) {
        this.ctx = ctx;
        this.exec = exec;
        this.option = option;
    }

    /**
     * Builds the node for the given label.
     *
     * @param label the root label
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node(String label) {
        return Commands.literal(label)
                .requires(CommandContext.requiresPermission(
                        GameModePermissions.USE, GameModePermissions.modeNode(option)))
                .executes(context -> {
                    var targets = TargetSelector.self(
                            context.getSource(), ctx, m -> m.getCore().getConsoleNeedsTarget());
                    return targets.map(players -> exec.set(context.getSource(), option, players)).orElse(0);
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
                                    .map(players -> exec.set(context.getSource(), option, players))
                                    .orElse(0);
                        }));
    }
}
