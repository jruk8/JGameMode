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
        "#         JTemplate          #",
        "################################",
        "",
        "# A starting point for a JSuite plugin.",
        ""
})
public class PluginConfig extends OkaeriConfig {

    @Comment("GitHub update checker settings.")
    private UpdateChecker updateChecker = new UpdateChecker();

    @Comment({
            "Extra labels for the /jtemplate root command (shortcuts like /jt).",
            "Applies on restart."
    })
    private List<String> aliases = List.of("jt");

    @Comment({
            "SQLite storage settings.",
            "The database file is created inside the plugin's data folder."
    })
    private Storage storage = new Storage();

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
        private String dbFile = "jtemplate.db";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Colors extends OkaeriConfig {

        @Comment("MiniMessage color tag for normal message bodies, used via {plain}.")
        private String plainColor = "<#b8d8e0>";

        @Comment("MiniMessage color tag for brand accents, used via {brand}.")
        private String brandColor = "<#84b8db>";

        @Comment("MiniMessage color tag for highlighted words, used via {hl}.")
        private String highlightColor = "<#ebf1f2>";

        @Comment("MiniMessage color tag for errors, used via {error}.")
        private String errorColor = "<#415ad9>";
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
                "Checks GitHub releases for new JTemplate versions on enable and on reload.",
                "The check runs asynchronously; findings go to the console and to",
                "players holding jtemplate.reload (on join and via /jtemplate version)."
        })
        private boolean enabled = true;
    }
}
