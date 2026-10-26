package com.jruk8.jtemplate.core.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("FieldMayBeFinal")
@Header({
        "################################",
        "#          JGameMode         #",
        "################################",
        "",
        "# Modern 26.x plugin for /gm s and /gmc.",
        ""
})
public class PluginConfig extends OkaeriConfig {

    @Comment("GitHub update checker settings.")
    private UpdateChecker updateChecker = new UpdateChecker();

    @Comment({
            "Extra labels for the /jgamemode root command (shortcuts like /jgm).",
            "Applies on restart."
    })
    private List<String> aliases = List.of("jgm");

    @Comment({
            "Routes vanilla /gamemode through this plugin: same targets plus",
            "dynamic mode resolution, /gamemode toggle and toggle history.",
            "When false, vanilla /gamemode is left untouched."
    })
    private boolean overrideVanillaCommand = true;

    @Comment({
            "SQLite storage settings.",
            "The database file is created inside the plugin's data folder."
    })
    private Storage storage = new Storage();

    @Comment({
            "Standalone root commands: each channel registers (or hides) its",
            "root, with extra labels under 'aliases'. Toggle roots (/gmt, /t)",
            "flip between spectator and the last non-spectator mode.",
            "Applies on restart; /jgamemode reload reports changed toggles instead."
    })
    private Commands commands = new Commands();

    @Comment({
            "The /s, /c, /a and /sp single-letter roots. Each label is only",
            "registered when free; taken labels are skipped with a console warning."
    })
    private Singles singles = new Singles();

    @Comment({
            "Color palette used by every message via {plain}, {brand}, {hl} and",
            "{error}. Values are MiniMessage color tags."
    })
    private Colors colors = new Colors();

    @Comment({
            "Anonymous usage statistics via bStats (open-source, no personal data or IP addresses).",
            "Requires a server restart to take effect."
    })
    private Metrics metrics = new Metrics();

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Storage extends OkaeriConfig {

        @Comment("Name of the SQLite database file created in the plugin's data folder.")
        private String dbFile = "jgamemode.db";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Commands extends OkaeriConfig {

        @Comment("Standalone /gm root command and its extra labels.")
        private Channel gm = new Channel(List.of());

        @Comment("Standalone /gms root command and its extra labels.")
        private Channel survival = new Channel(List.of());

        @Comment("Standalone /gmc root command and its extra labels.")
        private Channel creative = new Channel(List.of());

        @Comment("Standalone /gma root command and its extra labels.")
        private Channel adventure = new Channel(List.of());

        @Comment("Standalone /gmsp root command and its extra labels.")
        private Channel spectator = new Channel(List.of());

        @Comment("Standalone /gmt toggle root command and its extra labels.")
        private Channel toggle = new Channel(List.of("t"));
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Channel extends OkaeriConfig {

        @Comment("Registers the standalone root command for this channel.")
        private boolean enabled = true;

        @Comment("Extra labels for the root command.")
        private List<String> aliases = List.of();

        public Channel() {
        }

        public Channel(List<String> aliases) {
            this.aliases = aliases;
        }
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Singles extends OkaeriConfig {

        @Comment("Registers the /s, /c, /a and /sp single-letter roots when their labels are free.")
        private boolean enabled = true;
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Colors extends OkaeriConfig {

        @Comment("MiniMessage color tag for normal message bodies, used via {plain}.")
        private String plainColor = "<#e0d8b8>";

        @Comment("MiniMessage color tag for brand accents, used via {brand}.")
        private String brandColor = "<#edca3b>";

        @Comment("MiniMessage color tag for highlighted words, used via {hl}.")
        private String highlightColor = "<#f2f0e9>";

        @Comment("MiniMessage color tag for errors, used via {error}.")
        private String errorColor = "<#d9ba41>";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Metrics extends OkaeriConfig {

        @Comment("Send anonymous statistics to bStats.")
        private boolean enabled = true;
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class UpdateChecker extends OkaeriConfig {

        @Comment({
                "Checks GitHub releases for new JGameMode versions on enable and on reload.",
                "The check runs asynchronously; findings go to the console and to",
                "players holding jgamemode.reload (on join and via /jgamemode version)."
        })
        private boolean enabled = true;
    }
}
