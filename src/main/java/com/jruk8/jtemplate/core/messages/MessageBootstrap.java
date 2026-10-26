package com.jruk8.jtemplate.core.messages;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import lombok.Getter;

public class MessageBootstrap implements Bootstrap {

    @Getter
    private Messenger messenger;
    private final ConfigRegistrar config;

    public MessageBootstrap(ConfigRegistrar config) {
        this.config = config;
    }

    @Override
    public void register() {
        var parser = new MessageParser(config.getPluginConfig(), config.getMessagesConfig());
        this.messenger = new Messenger(parser);
    }
}
