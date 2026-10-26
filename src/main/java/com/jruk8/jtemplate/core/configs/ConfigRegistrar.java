package com.jruk8.jtemplate.core.configs;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloadable;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.jruk8.jtemplate.core.sounds.SoundsConfig;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class ConfigRegistrar implements Bootstrap, Reloadable {

    @Getter
    private PluginConfig pluginConfig;
    @Getter
    private MessagesConfig messagesConfig;
    @Getter
    private SoundsConfig soundsConfig;
    private final JavaPlugin plugin;

    private List<OkaeriConfig> configs;

    public ConfigRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        this.pluginConfig = createConfig(PluginConfig.class, "config.yml");
        this.messagesConfig = createConfig(MessagesConfig.class, "messages.yml");
        this.soundsConfig = createConfig(SoundsConfig.class, "sounds.yml");

        this.configs = List.of(this.pluginConfig, this.messagesConfig, this.soundsConfig);
    }

    @Override
    public void reload() {
        if (this.configs == null) {
            return;
        }
        this.configs.forEach(ConfigRegistrar::saveAndLoad);
    }

    private <T extends OkaeriConfig> T createConfig(Class<T> configClass, String fileName) {
        return ConfigManager.create(configClass, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(plugin.getDataFolder(), fileName));
            it.withRemoveOrphans(true);
            saveAndLoad(it);
        });
    }

    private static void saveAndLoad(OkaeriConfig config) {
        config.saveDefaults();
        config.load(true);
    }
}
