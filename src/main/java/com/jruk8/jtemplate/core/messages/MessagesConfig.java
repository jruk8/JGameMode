package com.jruk8.jtemplate.core.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

/**
 * Messages shown by the plugin.
 * <p>
 * Every message starts with {prefix} and uses the config.yml color palette:
 * {plain} for body text, {brand} for headers and the brand name, {hl} for
 * numbers, player names and similar values, {error} for errors. Commands
 * insert runtime values through {bracket} placeholders: {version},
 * {authors}, {time}, {syntax}, {description}, {latest}, {current}, {url},
 * {modrinth-url} and {permission}. Angle brackets are reserved for raw
 * MiniMessage, and \n breaks the line.
 */
@Getter
@Setter
public class MessagesConfig extends OkaeriConfig {

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Core extends OkaeriConfig {

        @Comment("Sent when a reload succeeds (use {time})")
        private String reloadSuccess = "\\n{prefix}{plain}Configuration reloaded in {hl}{time}ms{plain}.";

        @Comment("Sent when a reload fails (use {time})")
        private String reloadFailure = "{prefix}{error}Reload failed in {time}ms. "
                + "Check the console for details.";

        @Comment("Sent when changed labels need a restart")
        private String reloadRestartRequired = "{prefix}{hl}Reload complete. "
                + "{plain}Command label changes need a server restart.";

        @Comment("Help header (use {version})")
        private String helpHeader = "\\n{prefix}{brand}JTemplate {version} help:";

        @Comment("One help entry (use {syntax} and {description})")
        private String helpEntryFormat = "{brand}» {hl}{syntax}{plain}: {description}";

        @Comment("Version command output (use {version} and {authors})")
        private String versionSuccess = "\n{prefix}{brand}Version {hl}{version}"
                + "\n{brand}» {hl}Authors: {plain}{authors}"
                + "\n{brand}» {hl}Links: "
                + "<click:open_url:'https://modrinth.com/project/jtemplate'>"
                + "<hover:show_text:'{plain}Open on Modrinth'><#5ae663>Modrinth</hover></click>"
                + "{plain} | "
                + "<click:open_url:'https://github.com/jruk8/JPaperTemplate'>"
                + "<hover:show_text:'{plain}Open on GitHub'><#5a88e6>GitHub</hover></click>"
                + "{plain} | "
                + "<click:open_url:'https://ko-fi.com/jruk'>"
                + "<hover:show_text:'{plain}Support on Ko-fi'><#ff5e5b>Donate</hover></click>";

        @Comment("Update notice (use {latest}, {current}, {url} and {modrinth-url})")
        private String updateAvailable = "{prefix}{hl}JTemplate {latest} is available"
                + "{plain} (running {hl}{current}{plain}): {url} / {modrinth-url}";

        @Comment("Sent when the sender lacks a permission (use {permission})")
        private String noPermission = "{prefix}{error}You do not have the permission "
                + "{hl}'{permission}'{error} to use this command.";

        @Comment("Sent when a target selector matched no players")
        private String noTargets = "{prefix}{error}No players matched that target selector.";

        @Comment("Sent when console runs a command without a target selector")
        private String consoleNeedsTarget = "{prefix}{error}Console must specify a target.";
    }

    @Comment("Prefix prepended to every message, trailing space included")
    private String prefix = "<#a6a6a6>[<gradient:#8fdde3:#84b8db><bold>J</bold>Template</gradient>]</#a6a6a6> ";

    @Comment("Core plugin messages")
    private Core core = new Core();
}
