package com.jruk8.jtemplate.core.commands.user.version;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.TemplatePermissions;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.jruk8.jtemplate.core.update.UpdateAvailable;
import com.jruk8.jtemplate.core.update.UpdateCheckService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Version subcommand that displays the plugin version and authors.
 */
public class VersionCommand {

    private final AuthorFormatter authorFormatter;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;
    private final UpdateCheckService updateChecks;
    private final CommandUsageTracker usageTracker;

    public VersionCommand(
            AuthorFormatter authorFormatter,
            CommandContext ctx,
            PluginMeta pluginMeta,
            UpdateCheckService updateChecks,
            CommandUsageTracker usageTracker
    ) {
        this.authorFormatter = authorFormatter;
        this.ctx = ctx;
        this.pluginMeta = pluginMeta;
        this.updateChecks = updateChecks;
        this.usageTracker = usageTracker;
    }

    /**
     * Builds the {@code version} Brigadier node, attached to the {@code jtemplate} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("version")
                .requires(CommandContext.requiresPermission(TemplatePermissions.VERSION))
                .executes(context -> {
                    onVersion(context.getSource());
                    return Command.SINGLE_SUCCESS;
                });
    }

    public void onVersion(CommandSourceStack sender) {
        String version = pluginMeta.getVersion();
        List<String> rawAuthors = pluginMeta.getAuthors();
        String authors = authorFormatter.formatAuthors(rawAuthors);

        if (sender.getSender() instanceof Player player) {
            usageTracker.recordUse(player.getUniqueId());
        }

        ctx.sendVersion(
                sender,
                m -> m.getCore().getVersionSuccess(),
                Map.of("version", version, "authors", authors)
        );
        if (sender.getSender().hasPermission(TemplatePermissions.RELOAD)) {
            updateChecks.pendingUpdate().ifPresent(update -> sendUpdateNotice(sender, update));
        }
    }

    private void sendUpdateNotice(CommandSourceStack sender, UpdateAvailable update) {
        var tags = Map.of(
                "latest", update.latest(),
                "current", update.current(),
                "url", update.url(),
                "modrinth-url", update.modrinthUrl());
        ctx.sendMessage(sender, m -> m.getCore().getUpdateAvailable(), tags);
    }
}
