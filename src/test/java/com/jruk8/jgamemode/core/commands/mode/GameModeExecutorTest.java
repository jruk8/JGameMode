package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.commands.GameModePermissions;
import com.jruk8.jgamemode.core.messages.MessagesConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameModeExecutorTest {

    @Mock
    private CommandContext ctx;

    @Mock
    private ToggleHistory history;

    @Mock
    private GameUsageTracker tracker;

    @Mock
    private CommandSourceStack source;

    private GameModeExecutor executor() {
        return new GameModeExecutor(ctx, history, tracker);
    }

    private Player player(UUID id, GameMode mode) {
        Player player = mock(Player.class);
        lenient().when(player.getUniqueId()).thenReturn(id);
        lenient().when(player.getGameMode()).thenReturn(mode);
        lenient().when(player.getName()).thenReturn("Steve");
        return player;
    }

    private void permissions(Player sender, String... nodes) {
        lenient().when(source.getSender()).thenReturn(sender);
        for (String node : nodes) {
            lenient().when(sender.hasPermission(node)).thenReturn(true);
        }
    }

    private String selectedMessage(Function<MessagesConfig, String> selector) {
        return selector.apply(new MessagesConfig());
    }

    @Test
    @DisplayName("should set the sender mode and cache the previous one")
    void shouldSetSelf() {
        UUID id = UUID.randomUUID();
        Player self = player(id, GameMode.SURVIVAL);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.CREATIVE));

        assertEquals(1, executor().set(source, GameModeOption.CREATIVE, List.of(self)));

        verify(self).setGameMode(GameMode.CREATIVE);
        verify(history).recordSwitch(id, GameMode.SURVIVAL, GameMode.CREATIVE);
        verify(tracker).recordSwitch(id, GameModeOption.CREATIVE);
        var captor = ArgumentCaptor.forClass(Function.class);
        verify(ctx).send(eq(source), captor.capture(), any(Map.class));
        assertEquals(selectedMessage(m -> m.getMode().getSetSelf()), selectedMessage(captor.getValue()));
    }

    @Test
    @DisplayName("should deny sets without the use permission")
    void shouldDenyWithoutUse() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        lenient().when(source.getSender()).thenReturn(self);

        assertEquals(0, executor().set(source, GameModeOption.CREATIVE, List.of(self)));

        verify(self, never()).setGameMode(any(GameMode.class));
        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), any(Function.class), tags.capture());
        assertEquals(GameModePermissions.USE, tags.getValue().get("permission"));
    }

    @Test
    @DisplayName("should deny other targets without the other permission")
    void shouldDenyOthersWithoutOther() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        Player other = player(UUID.randomUUID(), GameMode.SURVIVAL);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF);

        assertEquals(0, executor().set(source, GameModeOption.CREATIVE, List.of(self, other)));

        verify(other, never()).setGameMode(any(GameMode.class));
    }

    @Test
    @DisplayName("should deny modes without the mode permission")
    void shouldDenyModeWithoutPermission() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF);

        assertEquals(0, executor().set(source, GameModeOption.SPECTATOR, List.of(self)));

        verify(self, never()).setGameMode(any(GameMode.class));
        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), any(Function.class), tags.capture());
        assertEquals(GameModePermissions.modeNode(GameModeOption.SPECTATOR), tags.getValue().get("permission"));
    }

    @Test
    @DisplayName("should notify affected players other than the sender")
    void shouldNotifyTargets() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        Player other = player(UUID.randomUUID(), GameMode.ADVENTURE);
        permissions(self, GameModePermissions.USE, GameModePermissions.OTHER,
                GameModePermissions.modeNode(GameModeOption.CREATIVE));

        assertEquals(1, executor().set(source, GameModeOption.CREATIVE, List.of(other)));

        verify(other).setGameMode(GameMode.CREATIVE);
        verify(ctx).notify(eq(other), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should toggle non-spectator targets to spectator and cache the mode")
    void shouldToggleToSpectator() {
        UUID id = UUID.randomUUID();
        Player self = player(id, GameMode.CREATIVE);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.SPECTATOR));

        assertEquals(1, executor().toggle(source, List.of(self)));

        verify(self).setGameMode(GameMode.SPECTATOR);
        verify(history).recordSwap(id, GameMode.CREATIVE);
        verify(tracker).recordToggle(eq(id), eq(List.of(GameModeOption.SPECTATOR)));
    }

    @Test
    @DisplayName("should toggle spectators back to their cached mode")
    void shouldToggleToHistory() {
        UUID id = UUID.randomUUID();
        Player self = player(id, GameMode.SPECTATOR);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.CREATIVE));
        when(history.lastMode(id)).thenReturn(Optional.of(GameMode.CREATIVE));

        assertEquals(1, executor().toggle(source, List.of(self)));

        verify(self).setGameMode(GameMode.CREATIVE);
        verify(history).recordSwap(id, GameMode.SPECTATOR);
    }

    @Test
    @DisplayName("should fall back to creative for spectators without history")
    void shouldFallbackToCreative() {
        UUID id = UUID.randomUUID();
        Player self = player(id, GameMode.SPECTATOR);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.CREATIVE));
        when(history.lastMode(id)).thenReturn(Optional.empty());

        assertEquals(1, executor().toggle(source, List.of(self)));

        verify(self).setGameMode(GameMode.CREATIVE);
    }

    @Test
    @DisplayName("should report the creative tip when no fallback is permitted")
    void shouldReportCreativeTipWithoutFallback() {
        Player self = player(UUID.randomUUID(), GameMode.SPECTATOR);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF);
        when(history.lastMode(any(UUID.class))).thenReturn(Optional.empty());

        assertEquals(0, executor().toggle(source, List.of(self)));

        verify(self, never()).setGameMode(any(GameMode.class));
        var selector = ArgumentCaptor.forClass(Function.class);
        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), selector.capture(), tags.capture());
        var messages = new MessagesConfig();
        assertEquals(messages.getMode().getToggleNoPermission(), selector.getValue().apply(messages));
        assertEquals(GameModePermissions.modeNode(GameModeOption.CREATIVE), tags.getValue().get("permission"));
    }

    @Test
    @DisplayName("should report the history tip when history is not permitted")
    void shouldReportHistoryTip() {
        Player self = player(UUID.randomUUID(), GameMode.SPECTATOR);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF);
        when(history.lastMode(any(UUID.class))).thenReturn(Optional.of(GameMode.SURVIVAL));

        assertEquals(0, executor().toggle(source, List.of(self)));

        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), any(Function.class), tags.capture());
        assertEquals(GameModePermissions.modeNode(GameModeOption.SURVIVAL), tags.getValue().get("permission"));
    }

    @Test
    @DisplayName("should report the spectator tip when switching to spectator is denied")
    void shouldReportSpectatorTip() {
        Player self = player(UUID.randomUUID(), GameMode.CREATIVE);
        permissions(self, GameModePermissions.USE, GameModePermissions.SELF);

        assertEquals(0, executor().toggle(source, List.of(self)));

        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).error(eq(source), any(Function.class), tags.capture());
        assertEquals(GameModePermissions.modeNode(GameModeOption.SPECTATOR), tags.getValue().get("permission"));
    }

    @Test
    @DisplayName("should let console switch targets with the other permission")
    void shouldLetConsoleSwitchTargets() {
        CommandSender console = mock(CommandSender.class);
        when(source.getSender()).thenReturn(console);
        when(console.hasPermission(GameModePermissions.USE)).thenReturn(true);
        when(console.hasPermission(GameModePermissions.OTHER)).thenReturn(true);
        when(console.hasPermission(GameModePermissions.modeNode(GameModeOption.ADVENTURE))).thenReturn(true);
        Player target = player(UUID.randomUUID(), GameMode.SURVIVAL);

        assertEquals(1, executor().set(source, GameModeOption.ADVENTURE, List.of(target)));

        verify(target).setGameMode(GameMode.ADVENTURE);
        verify(tracker, never()).recordSwitch(any(UUID.class), any(GameModeOption.class));
    }

    @Test
    @DisplayName("should summarize several targets with one message")
    void shouldSummarizeSeveralTargets() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        Player first = player(UUID.randomUUID(), GameMode.SURVIVAL);
        Player second = player(UUID.randomUUID(), GameMode.SURVIVAL);
        permissions(self, GameModePermissions.USE, GameModePermissions.OTHER,
                GameModePermissions.modeNode(GameModeOption.ADVENTURE));

        assertEquals(1, executor().set(source, GameModeOption.ADVENTURE, List.of(first, second)));

        var selector = ArgumentCaptor.forClass(Function.class);
        var tags = ArgumentCaptor.forClass(Map.class);
        verify(ctx).send(eq(source), selector.capture(), tags.capture());
        assertEquals("2", tags.getValue().get("count"));
    }

    @Test
    @DisplayName("should abort the whole toggle when one target cannot swap")
    void shouldAbortToggleForUnswappableTarget() {
        Player self = player(UUID.randomUUID(), GameMode.SURVIVAL);
        Player stuck = player(UUID.randomUUID(), GameMode.SPECTATOR);
        permissions(self, GameModePermissions.USE, GameModePermissions.OTHER,
                GameModePermissions.modeNode(GameModeOption.SPECTATOR));
        when(history.lastMode(any(UUID.class))).thenReturn(Optional.empty());

        assertEquals(0, executor().toggle(source, List.of(self, stuck)));

        verify(self, never()).setGameMode(any(GameMode.class));
        verify(stuck, never()).setGameMode(any(GameMode.class));
        verify(ctx).error(eq(source), any(Function.class), any(Map.class));
    }
}
