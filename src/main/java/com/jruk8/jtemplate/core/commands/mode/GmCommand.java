package com.jruk8.jtemplate.core.commands.mode;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.GameModePermissions;
import com.jruk8.jtemplate.core.commands.selectors.TargetSelector;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The {@code gm} mode command: bare toggles the sender, one token sets the
 * sender's mode, toggles the sender, or toggles the given targets, and a
 * mode-or-toggle token plus a selector acts on those targets.
 */
public class GmCommand {

    private final CommandContext ctx;
    private final GameModeExecutor exec;

    public GmCommand(CommandContext ctx, GameModeExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the node for the given label ({@code gm} or {@code gamemode}).
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
                .then(Commands.argument("input", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            suggestInput(context.getSource(), builder.getRemainingLowerCase(), builder);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String token = StringArgumentType.getString(context, "input");
                            return onSingle(context.getSource(), token);
                        })
                        .then(Commands.argument("targets", ArgumentTypes.players())
                                .suggests((context, builder) -> {
                                    suggestTargets(
                                            context.getSource(), builder.getRemainingLowerCase(), builder);
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String token = StringArgumentType.getString(context, "input");
                                    PlayerSelectorArgumentResolver selector = context.getArgument(
                                            "targets", PlayerSelectorArgumentResolver.class);
                                    return onDouble(context.getSource(), token, selector);
                                })));
    }

    private int onSingle(CommandSourceStack source, String token) {
        if (ModeResolver.parse(token) instanceof ModeResolver.Result.Mode mode) {
            var targets = TargetSelector.self(source, ctx, m -> m.getCore().getConsoleNeedsTarget());
            return targets.map(players -> exec.set(source, mode.option(), players)).orElse(0);
        }
        if (ModeResolver.parse(token) instanceof ModeResolver.Result.Toggle) {
            var targets = TargetSelector.self(source, ctx, m -> m.getCore().getConsoleNeedsTarget());
            return targets.map(players -> exec.toggle(source, players)).orElse(0);
        }
        PlayerSelectorArgumentResolver selector;
        try {
            selector = ArgumentTypes.players().parse(new StringReader(token));
        } catch (CommandSyntaxException invalid) {
            return invalid(source, token);
        }
        var targets = TargetSelector.fromSelector(source, selector, ctx);
        return targets.map(players -> exec.toggle(source, players)).orElse(0);
    }

    private int onDouble(
            CommandSourceStack source,
            String token,
            PlayerSelectorArgumentResolver selector
    ) {
        var targets = TargetSelector.fromSelector(source, selector, ctx);
        if (targets.isEmpty()) {
            return 0;
        }
        if (ModeResolver.parse(token) instanceof ModeResolver.Result.Mode mode) {
            return exec.set(source, mode.option(), targets.get());
        }
        if (ModeResolver.parse(token) instanceof ModeResolver.Result.Toggle) {
            return exec.toggle(source, targets.get());
        }
        return invalid(source, token);
    }

    private int invalid(CommandSourceStack source, String token) {
        ctx.error(source, m -> m.getMode().getInvalid(), Map.of("input", token));
        return 0;
    }

    private void suggestInput(
            CommandSourceStack source,
            String prefix,
            SuggestionsBuilder builder
    ) {
        for (String candidate : ModeSuggestions.suggest(source, exec, prefix)) {
            builder.suggest(candidate);
        }
        if (prefix.startsWith("@") || matchesOnlineName(prefix)) {
            for (String candidate : TargetSelector.suggestTargets(source, prefix)) {
                builder.suggest(candidate);
            }
        }
    }

    private boolean matchesOnlineName(String prefix) {
        if (prefix.isEmpty()) {
            return false;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void suggestTargets(
            CommandSourceStack source,
            String prefix,
            SuggestionsBuilder builder
    ) {
        for (String candidate : TargetSelector.suggestTargets(source, prefix)) {
            builder.suggest(candidate);
        }
    }

    /**
     * Selector-only suggestions shared by the single-argument roots.
     *
     * @param source the command source completing the argument
     * @param prefix the already-typed input used for filtering (may be null)
     * @return the permitted selector candidates
     */
    public static List<String> selectorOnly(CommandSourceStack source, String prefix) {
        return TargetSelector.suggestTargets(source, prefix);
    }

    /**
     * Mode-argument suggestions shared by the override root.
     *
     * @param source the command source completing the argument
     * @param exec the gamemode executor, used to resolve toggle targets
     * @param prefix the already-typed input used for filtering (may be null)
     * @return full names first, then toggle, then shorthands, all filtered
     */
    public static List<String> modeOnly(CommandSourceStack source, GameModeExecutor exec, String prefix) {
        return ModeSuggestions.suggest(source, exec, prefix);
    }
}
