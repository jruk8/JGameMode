package com.jruk8.jtemplate.core.sounds;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Header({
        "################################",
        "#            Sounds            #",
        "################################",
        "",
        "Sounds configuration for plugin sounds.",
        "Each entry supports enabled, a namespaced sound key, pitch and volume.",
        "Sounds are resolved against the server's sound registry, see",
        "Mudkipdev's explorer: https://mudkipdev.github.io/minecraft-sound-explorer/",
        ""
})
public class SoundsConfig extends OkaeriConfig {

    @Comment("Neutral feedback for ordinary commands, heard by the executor and every affected player.")
    private Sound neutral = new Sound(true, "entity.breeze.land", 0.8f, 0.7f);

    @Comment("Feedback for state resets, heard by the executor and every affected player.")
    private Sound reset = new Sound(true, "block.azalea_leaves.place", 0.8f, 0.7f);

    @Comment("Sound played when the configuration is reloaded.")
    private Sound reload = new Sound(true, "block.azalea_leaves.place", 0.8f, 0.7f);

    @Comment("Sound played when the help menu is shown.")
    private Sound help = new Sound(true, "block.azalea_leaves.place", 0.8f, 0.7f);

    @Comment("Sound played when a command fails (invalid input, missing permission, failed reload).")
    private Sound error = new Sound(true, "block.note_block.bass", 1.0f, 1.0f);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sound extends OkaeriConfig {

        @Comment("Whether this sound plays at all.")
        private boolean enabled = true;

        @Comment("Namespaced sound key, e.g. entity.breeze.land or minecraft:entity.breeze.land.")
        private String sound = "block.note_block.pling";

        @Comment("Sound pitch, usually between 0.5 and 2.0.")
        private float pitch = 1.0f;

        @Comment("Sound volume, usually between 0.0 and 1.0.")
        private float volume = 1.0f;

        public Sound(boolean enabled, String sound) {
            this(enabled, sound, 1.0f, 1.0f);
        }
    }
}
