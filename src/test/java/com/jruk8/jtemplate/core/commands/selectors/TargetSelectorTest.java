package com.jruk8.jtemplate.core.commands.selectors;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.TemplatePermissions;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetSelectorTest {

    @Mock
    private CommandContext ctx;

    @Mock
    private CommandSourceStack source;

    @Mock
    private PlayerSelectorArgumentResolver selector;

    private Player player(UUID id) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    private String selectedMessage(Function<MessagesConfig, String> messageSelector) {
        MessagesConfig messages = mock(MessagesConfig.class);
        MessagesConfig.Core core = mock(MessagesConfig.Core.class);
        when(messages.getCore()).thenReturn(core);
        when(core.getNoTargets()).thenReturn("none");
        return messageSelector.apply(messages);
    }

    @Test
    @DisplayName("should send no-targets when the selector resolves to nobody")
    void shouldSendNoTargetsForEmptyResolution() throws Exception {
        lenient().when(source.getSender()).thenReturn(mock(Player.class));
        when(selector.resolve(source)).thenReturn(List.of());

        var targets = TargetSelector.fromSelector(source, selector, ctx);

        assertTrue(targets.isEmpty());
        var captor = ArgumentCaptor.forClass(Function.class);
        verify(ctx).error(eq(source), captor.capture(), any(Map.class));
        assertEquals("none", selectedMessage(captor.getValue()));
    }

    @Test
    @DisplayName("should send no-targets when selector parsing throws")
    void shouldSendNoTargetsWhenSelectorThrows() throws Exception {
        when(selector.resolve(source)).thenThrow(mock(CommandSyntaxException.class));

        var targets = TargetSelector.fromSelector(source, selector, ctx);

        assertTrue(targets.isEmpty());
        verify(ctx).error(eq(source), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should resolve the sender alone without the other permission")
    void shouldResolveSelfWithoutOtherPermission() throws Exception {
        Player self = player(UUID.randomUUID());
        when(source.getSender()).thenReturn(self);
        when(selector.resolve(source)).thenReturn(List.of(self));

        var targets = TargetSelector.fromSelector(source, selector, ctx);

        assertEquals(List.of(self), targets.orElseThrow());
    }

    @Test
    @DisplayName("should deny other targets without the other permission")
    void shouldDenyOtherTargetsWithoutPermission() throws Exception {
        Player self = player(UUID.randomUUID());
        Player other = player(UUID.randomUUID());
        when(source.getSender()).thenReturn(self);
        when(selector.resolve(source)).thenReturn(List.of(other));

        var targets = TargetSelector.fromSelector(source, selector, ctx);

        assertTrue(targets.isEmpty());
        var selectorCaptor = ArgumentCaptor.forClass(Function.class);
        var tagsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), selectorCaptor.capture(), tagsCaptor.capture());
        assertEquals(TemplatePermissions.OTHER, tagsCaptor.getValue().get("permission"));
        @SuppressWarnings("unchecked")
        Function<MessagesConfig, String> fn = (Function<MessagesConfig, String>) selectorCaptor.getValue();
        var messages = new MessagesConfig();
        assertEquals(messages.getCore().getNoPermission(), fn.apply(messages));
    }

    @Test
    @DisplayName("should resolve other targets with the other permission")
    void shouldResolveOtherTargetsWithPermission() throws Exception {
        Player self = player(UUID.randomUUID());
        Player other = player(UUID.randomUUID());
        when(source.getSender()).thenReturn(self);
        when(self.hasPermission(TemplatePermissions.OTHER)).thenReturn(true);
        when(selector.resolve(source)).thenReturn(List.of(other));

        var targets = TargetSelector.fromSelector(source, selector, ctx);

        assertEquals(List.of(other), targets.orElseThrow());
    }
}
