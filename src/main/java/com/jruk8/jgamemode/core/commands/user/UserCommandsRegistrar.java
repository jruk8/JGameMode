package com.jruk8.jgamemode.core.commands.user;

import com.jruk8.jgamemode.core.Bootstrap;
import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.user.help.HelpCommand;
import com.jruk8.jgamemode.core.commands.user.version.AuthorFormatter;
import com.jruk8.jgamemode.core.commands.user.version.VersionCommand;
import com.jruk8.jgamemode.core.update.UpdateCheckService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;

/**
 * Registers all user-facing subcommands on the plugin's root command node.
 */
public class UserCommandsRegistrar implements Bootstrap {

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;
    private final UpdateCheckService updateChecks;
    private final HelpCommand help;

    public UserCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                 CommandContext commandContext,
                                 PluginMeta pluginMeta,
                                 UpdateCheckService updateChecks,
                                 HelpCommand help) {
        this.root = root;
        this.ctx = commandContext;
        this.pluginMeta = pluginMeta;
        this.updateChecks = updateChecks;
        this.help = help;
    }

    @Override
    public void register() {
        root.then(help.node());
        root.then(new VersionCommand(new AuthorFormatter(), ctx, pluginMeta, updateChecks).node());
    }
}
