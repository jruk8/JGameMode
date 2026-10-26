package com.jruk8.jtemplate.core.commands.admin.reload;

import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.GameModePermissions;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;
import java.util.Map;

/**
 * Reload subcommand that re-reads every config and re-opens storage and placeholders.
 * <p>
 * Paper registers Brigadier commands once during enable and offers no
 * unregister API, so changed roots, aliases and the vanilla override still
 * need a restart; the command says so instead of pretending they applied.
 */
public class ReloadCommand {

    private final Reloader reloader;
    private final CommandContext ctx;
    private final PluginConfig pluginConfig;

    public ReloadCommand(Reloader reloader, CommandContext ctx, PluginConfig pluginConfig) {
        this.reloader = reloader;
        this.ctx = ctx;
        this.pluginConfig = pluginConfig;
    }

    /**
     * Builds the {@code reload} Brigadier node, attached to the {@code jgamemode} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("reload")
                .requires(CommandContext.requiresPermission(GameModePermissions.RELOAD))
                .executes(context -> {
                    onReload(context.getSource());
                    return Command.SINGLE_SUCCESS;
                });
    }

    public void onReload(CommandSourceStack sender) {
        RegistrationSnapshot before = snapshot();
        long start = System.currentTimeMillis();
        boolean ok = reloader.reloadAll();
        long time = System.currentTimeMillis() - start;
        if (ok) {
            ctx.sendReload(
                    sender,
                    m -> m.getCore().getReloadSuccess(),
                    Map.of("time", String.valueOf(time))
            );
        } else {
            ctx.error(
                    sender,
                    m -> m.getCore().getReloadFailure(),
                    Map.of("time", String.valueOf(time))
            );
        }
        if (!snapshot().equals(before)) {
            ctx.sendMessage(sender, m -> m.getCore().getReloadRestartRequired(), Map.of());
        }
    }

    private RegistrationSnapshot snapshot() {
        var commands = pluginConfig.getCommands();
        return new RegistrationSnapshot(
                List.copyOf(pluginConfig.getAliases()),
                pluginConfig.isOverrideVanillaCommand(),
                channel(commands.getGm()),
                channel(commands.getSurvival()),
                channel(commands.getCreative()),
                channel(commands.getAdventure()),
                channel(commands.getSpectator()),
                channel(commands.getToggle()),
                pluginConfig.getSingles().isEnabled()
        );
    }

    private static String channel(PluginConfig.Channel channel) {
        return channel.isEnabled() + "|" + List.copyOf(channel.getAliases());
    }

    private record RegistrationSnapshot(
            List<String> aliases,
            boolean overrideVanilla,
            String gm,
            String survival,
            String creative,
            String adventure,
            String spectator,
            String toggle,
            boolean singles
    ) {
    }
}
