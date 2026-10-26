package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.admin.AdminCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.mode.GameModeExecutor;
import com.jruk8.jtemplate.core.commands.mode.ModeCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.user.UserCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.user.help.HelpCommand;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.jruk8.jtemplate.core.update.UpdateCheckService;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Registers all plugin commands through Paper's native Brigadier support.
 * <p>
 * The {@code jgamemode} root (plus its configured aliases) carries the hub
 * subcommands; the standalone gamemode roots are registered only when their
 * config toggle is on and their label is free, each with its configured
 * aliases.
 */
public class CommandsRegistrar implements Bootstrap {

    private final LifecycleEventManager<Plugin> lifecycle;
    private final Reloader reloader;
    private final CommandContext commandContext;
    private final PluginMeta pluginMeta;
    private final PluginConfig pluginConfig;
    private final GameModeExecutor modeExecutor;
    private final UpdateCheckService updateChecks;
    private final Plugin plugin;

    public CommandsRegistrar(LifecycleEventManager<Plugin> lifecycle,
                             Reloader reloader,
                             CommandContext commandContext,
                             PluginMeta pluginMeta,
                             PluginConfig pluginConfig,
                             GameModeExecutor modeExecutor,
                             UpdateCheckService updateChecks,
                             Plugin plugin) {
        this.lifecycle = lifecycle;
        this.reloader = reloader;
        this.commandContext = commandContext;
        this.pluginMeta = pluginMeta;
        this.pluginConfig = pluginConfig;
        this.modeExecutor = modeExecutor;
        this.updateChecks = updateChecks;
        this.plugin = plugin;
    }

    @Override
    public void register() {
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var root = Commands.literal("jgamemode");
            var hubAliases = rootAliases();
            var help = new HelpCommand(commandContext, pluginMeta.getVersion());
            root.executes(context -> {
                if (!context.getSource().getSender().hasPermission(GameModePermissions.HELP)) {
                    var tags = Map.of("permission", GameModePermissions.HELP);
                    commandContext.error(context.getSource(), m -> m.getCore().getNoPermission(), tags);
                    return 0;
                }
                help.showHelp(context.getSource(), null);
                return Command.SINGLE_SUCCESS;
            });
            new UserCommandsRegistrar(root, commandContext, pluginMeta, updateChecks, help).register();
            new AdminCommandsRegistrar(root, commandContext, reloader, pluginConfig).register();
            var modes = new ModeCommandsRegistrar(
                    root, commandContext, modeExecutor, pluginConfig, plugin, plugin.getLogger());
            modes.register();
            event.registrar().register(root.build(), "JGameMode commands", hubAliases);
            modes.registerStandalone(event.registrar(), hubAliases);
        });
    }

    private List<String> rootAliases() {
        return pluginConfig.getAliases().stream()
                .map(String::trim)
                .map(alias -> alias.toLowerCase(Locale.ROOT))
                .filter(alias -> !alias.isEmpty() && !"jgamemode".equals(alias))
                .distinct()
                .toList();
    }
}
