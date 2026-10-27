package com.jruk8.jgamemode.core.messages;

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
 * {modrinth-url}, {permission}, {mode}, {targets}, {count} and {input}.
 * Angle brackets are reserved for raw MiniMessage, and \n breaks the line.
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
        private String helpHeader = "\\n{prefix}{brand}JGameMode {version} help:";

        @Comment("One help entry (use {syntax} and {description})")
        private String helpEntryFormat = "{brand}» {hl}{syntax}{plain}: {description}";

        @Comment("Version command output (use {version} and {authors})")
        private String versionSuccess = "\n{prefix}{brand}Version {hl}{version}"
                + "\n{brand}» {hl}Authors: {plain}{authors}"
                + "\n{brand}» {hl}Links: "
                + "<click:open_url:'https://modrinth.com/project/jgamemode'>"
                + "<hover:show_text:'{plain}Open on Modrinth'><#5ae663>Modrinth</hover></click>"
                + "{plain} | "
                + "<click:open_url:'https://github.com/jruk8/JGameMode'>"
                + "<hover:show_text:'{plain}Open on GitHub'><#5a88e6>GitHub</hover></click>"
                + "{plain} | "
                + "<click:open_url:'https://ko-fi.com/jruk'>"
                + "<hover:show_text:'{plain}Support on Ko-fi'><#ff5e5b>Donate</hover></click>";

        @Comment("Update notice (use {latest}, {current}, {url} and {modrinth-url})")
        private String updateAvailable = "{prefix}{hl}JGameMode {latest} is available"
                + "{plain} (running {hl}{current}{plain}): {url} / {modrinth-url}";

        @Comment("Sent when the sender lacks a permission (use {permission})")
        private String noPermission = "{prefix}{error}You do not have the permission "
                + "{hl}'{permission}'{error} to use this command.";

        @Comment("Sent when a target selector matched no players")
        private String noTargets = "{prefix}{error}No players matched that target selector.";

        @Comment("Sent when console runs a command without a target selector")
        private String consoleNeedsTarget = "{prefix}{error}Console must specify a target.";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Mode extends OkaeriConfig {

        @Comment("Sent to yourself when your gamemode is set (use {mode})")
        private String setSelf = "{prefix}{plain}Your gamemode is now {hl}{mode}{plain}.";

        @Comment("Sent to the sender when one other player's gamemode is set (use {mode} and {targets})")
        private String setOther = "{prefix}{plain}Set gamemode to {hl}{mode}{plain} "
                + "for {hl}{targets}{plain}.";

        @Comment("Sent to the sender when several players' gamemodes are set "
                + "(use {mode}, {count} and {targets})")
        private String setMultiple = "{prefix}{plain}Set gamemode to {hl}{mode}{plain} "
                + "for {hl}{count}{plain} players: {hl}{targets}{plain}.";

        @Comment("Sent to each affected player when their gamemode is set (use {mode})")
        private String setTarget = "{prefix}{plain}Your gamemode is now {hl}{mode}{plain}.";

        @Comment("Sent when a toggle cannot swap (use {permission} for the tip)")
        private String toggleNoPermission = "{prefix}{error}Unable to open game mode switcher; "
                + "no permission. Grant {hl}{permission}{error}.";

        @Comment("Sent when the mode token matches no gamemode (use {input})")
        private String invalid = "{prefix}{error}'{input}' is not a valid gamemode.";

        @Comment("Sent when the sender lacks the permission for a gamemode (use {permission} and {mode})")
        private String noModePermission = "{prefix}{error}You need the permission "
                + "{hl}'{permission}'{error} to switch to {hl}{mode}{error}.";
    }

    @Comment("Prefix prepended to every message, trailing space included")
    private String prefix = "<#a6a6a6>[<gradient:#edca3b:#f0d259><bold>J</bold>GameMode</gradient>]</#a6a6a6> ";

    @Comment("Core plugin messages")
    private Core core = new Core();

    @Comment("Gamemode command messages")
    private Mode mode = new Mode();
}
