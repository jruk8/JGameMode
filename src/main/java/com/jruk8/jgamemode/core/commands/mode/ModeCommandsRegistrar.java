package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.Bootstrap;
import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.configs.PluginConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Registers the gamemode subcommands on the hub root and the standalone
 * roots. Single-letter roots and the vanilla override are only registered
 * when their label is free; taken labels are skipped with a console
 * warning so one contested alias can never break the plugin.
 */
public class ModeCommandsRegistrar implements Bootstrap {

    private static final Set<String> HUB_COMMANDS = Set.of(
            "gm", "gms", "gmc", "gma", "gmsp", "gmt", "reload", "help", "version");

    private static final List<SingleRoot> SINGLES = List.of(
            new SingleRoot("s", GameModeOption.SURVIVAL),
            new SingleRoot("c", GameModeOption.CREATIVE),
            new SingleRoot("a", GameModeOption.ADVENTURE),
            new SingleRoot("sp", GameModeOption.SPECTATOR));

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final GameModeExecutor exec;
    private final PluginConfig pluginConfig;
    private final Plugin plugin;
    private final Logger logger;

    public ModeCommandsRegistrar(
            LiteralArgumentBuilder<CommandSourceStack> root,
            CommandContext commandContext,
            GameModeExecutor executor,
            PluginConfig pluginConfig,
            Plugin plugin,
            Logger logger
    ) {
        this.root = root;
        this.ctx = commandContext;
        this.exec = executor;
        this.pluginConfig = pluginConfig;
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void register() {
        var used = new HashSet<>(HUB_COMMANDS);
        var gm = new GmCommand(ctx, exec);
        root.then(gm.node("gm"));
        for (String alias : hubAliases(channel().getGm().getAliases(), used)) {
            root.then(gm.node(alias));
        }
        registerDirect("gms", GameModeOption.SURVIVAL, channel().getSurvival().getAliases(), used);
        registerDirect("gmc", GameModeOption.CREATIVE, channel().getCreative().getAliases(), used);
        registerDirect("gma", GameModeOption.ADVENTURE, channel().getAdventure().getAliases(), used);
        registerDirect("gmsp", GameModeOption.SPECTATOR, channel().getSpectator().getAliases(), used);
        var toggle = new ToggleCommand(ctx, exec);
        root.then(toggle.node("gmt"));
        for (String alias : hubAliases(channel().getToggle().getAliases(), used)) {
            root.then(toggle.node(alias));
        }
        if (used.add("t")) {
            root.then(toggle.node("t"));
        }
    }

    /**
     * Registers every standalone root: the mode roots, the toggle root,
     * the single-letter roots and the vanilla override.
     *
     * @param registrar the Paper commands registrar
     * @param hubLabels labels already taken by the hub root and its aliases
     */
    public void registerStandalone(Commands registrar, List<String> hubLabels) {
        var taken = new HashSet<>(hubLabels);
        taken.addAll(HUB_COMMANDS);
        taken.add("t");
        if (channel().getGm().isEnabled()) {
            registerRoot(registrar, new GmCommand(ctx, exec).node("gm").build(),
                    "JGameMode gm command", channelAliases(channel().getGm().getAliases(), taken));
        }
        registerChannelRoot(registrar, "gms", GameModeOption.SURVIVAL,
                channel().getSurvival(), taken);
        registerChannelRoot(registrar, "gmc", GameModeOption.CREATIVE,
                channel().getCreative(), taken);
        registerChannelRoot(registrar, "gma", GameModeOption.ADVENTURE,
                channel().getAdventure(), taken);
        registerChannelRoot(registrar, "gmsp", GameModeOption.SPECTATOR,
                channel().getSpectator(), taken);
        if (channel().getToggle().isEnabled()) {
            registerRoot(registrar, new ToggleCommand(ctx, exec).node("t").build(),
                    "JGameMode t command", List.of());
            registerRoot(registrar, new ToggleCommand(ctx, exec).node("gmt").build(),
                    "JGameMode toggle command", channelAliases(channel().getToggle().getAliases(), taken));
        }
        if (pluginConfig.getSingles().isEnabled()) {
            for (SingleRoot single : SINGLES) {
                registerRoot(registrar, new DirectModeCommand(ctx, exec, single.option()).node(single.label()).build(),
                        "JGameMode " + single.label() + " command", List.of());
            }
        }
        if (pluginConfig.isOverrideVanillaCommand()) {
            if (claimLabel("gamemode", true)) {
                registrar.register(new GmCommand(ctx, exec).node("gamemode").build(),
                        "JGameMode gamemode override", List.of());
                logger.info("Vanilla /gamemode is routed through JGameMode.");
            }
        }
    }

    private void registerDirect(
            String label,
            GameModeOption option,
            List<String> aliases,
            Set<String> used
    ) {
        var command = new DirectModeCommand(ctx, exec, option);
        root.then(command.node(label));
        for (String alias : hubAliases(aliases, used)) {
            root.then(command.node(alias));
        }
    }

    private void registerChannelRoot(
            Commands registrar,
            String label,
            GameModeOption option,
            PluginConfig.Channel channel,
            Set<String> taken
    ) {
        if (!channel.isEnabled()) {
            return;
        }
        registerRoot(registrar, new DirectModeCommand(ctx, exec, option).node(label).build(),
                "JGameMode " + label + " command", channelAliases(channel.getAliases(), taken));
    }

    private void registerRoot(
            Commands registrar,
            com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> node,
            String description,
            List<String> aliases
    ) {
        if (!claimLabel(node.getLiteral(), false)) {
            return;
        }
        List<String> free = new ArrayList<>();
        for (String alias : aliases) {
            if (claimLabel(alias, false)) {
                free.add(alias);
            }
        }
        registrar.register(node, description, free);
    }

    /**
     * Checks a label against the server command map. Taken labels are
     * skipped with a warning; the vanilla {@code gamemode} label may be
     * claimed deliberately when the override is enabled.
     *
     * @param label the label to claim
     * @param vanillaOk whether taking the label from vanilla is intended
     * @return {@code true} when the label is free (or vanilla and allowed)
     */
    private boolean claimLabel(String label, boolean vanillaOk) {
        Command existing = Bukkit.getCommandMap().getCommand(label);
        if (existing == null) {
            return true;
        }
        if (existing instanceof PluginCommand pluginCommand && pluginCommand.getPlugin().equals(plugin)) {
            return true;
        }
        if (vanillaOk && !(existing instanceof PluginCommand)) {
            return true;
        }
        logger.warning("Command label '/" + label + "' is already registered by "
                + describe(existing) + "; skipping JGameMode's '/" + label + "' root.");
        return false;
    }

    private String describe(Command existing) {
        if (existing instanceof PluginCommand pluginCommand) {
            return "the plugin '" + pluginCommand.getPlugin().getName() + "'";
        }
        return "another command source";
    }

    private PluginConfig.Commands channel() {
        return pluginConfig.getCommands();
    }

    private List<String> hubAliases(List<String> configured, Set<String> used) {
        List<String> accepted = new ArrayList<>();
        for (String raw : configured) {
            String alias = raw.trim().toLowerCase(Locale.ROOT);
            if (!alias.isEmpty() && used.add(alias)) {
                accepted.add(alias);
            }
        }
        return accepted;
    }

    private List<String> channelAliases(List<String> configured, Set<String> taken) {
        List<String> accepted = new ArrayList<>();
        for (String raw : configured) {
            String alias = raw.trim().toLowerCase(Locale.ROOT);
            if (!alias.isEmpty() && taken.add(alias)) {
                accepted.add(alias);
            }
        }
        return accepted;
    }

    private record SingleRoot(String label, GameModeOption option) {
    }
}
