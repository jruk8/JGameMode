package com.jruk8.jgamemode.core.commands.user.help;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;
import java.util.Map;

/**
 * Help subcommand that lists the plugin's commands, optionally filtered by a search query.
 * <p>
 * Senders with {@code jgamemode.help} may run it (everyone by default);
 * entries are filtered by permission. Entries use the main {@code /jgm}
 * alias for hub commands and {@code /gm} for mode commands.
 */
public class HelpCommand {

    private final CommandContext ctx;
    private final String version;

    public HelpCommand(CommandContext ctx, String version) {
        this.ctx = ctx;
        this.version = version;
    }

    /**
     * Builds the {@code help} Brigadier node, attached to the {@code jgamemode} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("help")
                .requires(CommandContext.requiresPermission(GameModePermissions.HELP))
                .executes(context -> {
                    showHelp(context.getSource(), null);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("query", StringArgumentType.greedyString())
                        .executes(context -> {
                            showHelp(context.getSource(), StringArgumentType.getString(context, "query"));
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    /**
     * Shows the help listing, optionally filtered by a search query.
     *
     * @param sender the sender to show the listing to
     * @param query the search query, may be null or blank
     */
    public void showHelp(CommandSourceStack sender, String query) {
        ctx.playHelp(sender);
        ctx.sendMessage(sender, m -> m.getCore().getHelpHeader(), Map.of("version", version));
        HelpEntry.matching(entries(), query).stream()
                .filter(entry -> entry.visibleTo(sender.getSender()))
                .forEach(entry -> ctx.sendMessage(
                        sender,
                        m -> m.getCore().getHelpEntryFormat(),
                        Map.of("syntax", entry.syntax(), "description", entry.description())
                ));
    }

    private List<HelpEntry> entries() {
        return List.of(
                new HelpEntry("/jgm help [query]", "Shows this help menu."),
                new HelpEntry("/jgm version", "Shows the plugin version.", GameModePermissions.VERSION),
                new HelpEntry("/gm [<mode>|toggle] [targets]",
                        "Sets gamemode, toggles without a mode.", GameModePermissions.USE),
                new HelpEntry("/gms|c|a|sp [targets]",
                        "Switches directly to a gamemode.", GameModePermissions.USE),
                new HelpEntry("/gmt [targets]",
                        "Toggles between spectator and the last mode.", GameModePermissions.USE),
                new HelpEntry("/gamemode [<mode>|toggle] [targets]",
                        "Vanilla gamemode through this plugin.", GameModePermissions.USE),
                new HelpEntry("/jgm reload", "Reloads the plugin configuration.", GameModePermissions.RELOAD));
    }
}
