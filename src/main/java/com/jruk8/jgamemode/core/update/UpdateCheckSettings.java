package com.jruk8.jgamemode.core.update;

import com.jruk8.jgamemode.core.configs.PluginConfig;

/**
 * Update checker toggles read from the config.yml update-checker section.
 */
public record UpdateCheckSettings(boolean enabled) {

    /**
     * Reads the toggles from the update-checker section.
     *
     * @param checker the config section
     * @return the resolved settings
     */
    public static UpdateCheckSettings fromConfig(PluginConfig.UpdateChecker checker) {
        return new UpdateCheckSettings(checker.isEnabled());
    }
}
