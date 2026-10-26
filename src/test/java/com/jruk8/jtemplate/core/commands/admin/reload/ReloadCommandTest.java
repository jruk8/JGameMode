package com.jruk8.jtemplate.core.commands.admin.reload;

import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReloadCommandTest {

    @Mock
    private Reloader reloader;

    @Mock
    private CommandContext ctx;

    @Mock
    private CommandSourceStack source;

    private ReloadCommand command(PluginConfig config) {
        return new ReloadCommand(reloader, ctx, config);
    }

    @Test
    @DisplayName("should report success without a restart notice when toggles are unchanged")
    void shouldReportSuccess() {
        when(reloader.reloadAll()).thenReturn(true);

        command(new PluginConfig()).onReload(source);

        verify(ctx).sendReload(eq(source), any(Function.class), any(Map.class));
        verify(ctx, never()).error(eq(source), any(Function.class), any(Map.class));
        verify(ctx, never()).sendMessage(eq(source), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should report failure when any reloadable throws")
    void shouldReportFailure() {
        when(reloader.reloadAll()).thenReturn(false);

        command(new PluginConfig()).onReload(source);

        verify(ctx).error(eq(source), any(Function.class), any(Map.class));
        verify(ctx, never()).sendReload(eq(source), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should ask for a restart when labels changed during reload")
    void shouldAskForRestartOnLabelChange() {
        var config = new PluginConfig();
        when(reloader.reloadAll()).thenAnswer(ignored -> {
            config.setAliases(List.of("jt", "template"));
            return true;
        });

        command(config).onReload(source);

        var selector = forClass(Function.class);
        verify(ctx).sendMessage(eq(source), selector.capture(), any(Map.class));
        @SuppressWarnings("unchecked")
        Function<MessagesConfig, String> fn = (Function<MessagesConfig, String>) selector.getValue();
        var messages = new MessagesConfig();
        assertEquals(messages.getCore().getReloadRestartRequired(), fn.apply(messages));
    }
}
