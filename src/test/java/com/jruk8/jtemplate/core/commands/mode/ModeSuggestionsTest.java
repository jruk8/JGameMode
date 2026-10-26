package com.jruk8.jtemplate.core.commands.mode;

import com.jruk8.jtemplate.core.commands.GameModePermissions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModeSuggestionsTest {

    @Mock
    private CommandSourceStack source;

    @Mock
    private GameModeExecutor exec;

    private Player player(GameMode mode, String... nodes) {
        Player player = mock(Player.class);
        lenient().when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        lenient().when(player.getGameMode()).thenReturn(mode);
        for (String node : nodes) {
            lenient().when(player.hasPermission(node)).thenReturn(true);
        }
        return player;
    }

    private void sender(CommandSender sender) {
        lenient().when(source.getSender()).thenReturn(sender);
    }

    private List<String> allModes() {
        return List.of("survival", "creative", "adventure", "spectator",
                "toggle", "s", "c", "a", "sp");
    }

    @Test
    @DisplayName("should suggest nothing without the use permission")
    void shouldSuggestNothingWithoutUse() {
        sender(player(GameMode.SURVIVAL, GameModePermissions.SELF));

        assertEquals(List.of(), ModeSuggestions.suggest(source, exec, ""));
    }

    @Test
    @DisplayName("should suggest nothing without a scope permission")
    void shouldSuggestNothingWithoutScope() {
        sender(player(GameMode.SURVIVAL, GameModePermissions.USE));

        assertEquals(List.of(), ModeSuggestions.suggest(source, exec, ""));
    }

    @Test
    @DisplayName("should list full names, toggle and shorthands in order")
    void shouldListEverythingInOrder() {
        Player self = player(GameMode.SURVIVAL, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.SURVIVAL),
                GameModePermissions.modeNode(GameModeOption.CREATIVE),
                GameModePermissions.modeNode(GameModeOption.ADVENTURE),
                GameModePermissions.modeNode(GameModeOption.SPECTATOR));
        sender(self);
        when(exec.toggleTarget(self, self)).thenReturn(Optional.of(GameModeOption.SPECTATOR));

        assertEquals(allModes(), ModeSuggestions.suggest(source, exec, ""));
    }

    @Test
    @DisplayName("should filter to permitted modes only")
    void shouldFilterByModePermission() {
        Player self = player(GameMode.SURVIVAL, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.CREATIVE));
        sender(self);
        when(exec.toggleTarget(self, self)).thenReturn(Optional.of(GameModeOption.SPECTATOR));

        assertEquals(List.of("creative", "c"), ModeSuggestions.suggest(source, exec, ""));
    }

    @Test
    @DisplayName("should hide toggle when the self swap is not permitted")
    void shouldHideToggleWithoutTarget() {
        Player self = player(GameMode.SPECTATOR, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.SURVIVAL));
        sender(self);
        when(exec.toggleTarget(self, self)).thenReturn(Optional.empty());

        assertEquals(List.of("survival", "s"), ModeSuggestions.suggest(source, exec, ""));
    }

    @Test
    @DisplayName("should narrow full names as the sender types")
    void shouldNarrowWhileTyping() {
        Player self = player(GameMode.SURVIVAL, GameModePermissions.USE, GameModePermissions.SELF,
                GameModePermissions.modeNode(GameModeOption.SURVIVAL),
                GameModePermissions.modeNode(GameModeOption.CREATIVE),
                GameModePermissions.modeNode(GameModeOption.ADVENTURE),
                GameModePermissions.modeNode(GameModeOption.SPECTATOR));
        sender(self);
        when(exec.toggleTarget(self, self)).thenReturn(Optional.of(GameModeOption.SPECTATOR));

        assertEquals(List.of("survival"), ModeSuggestions.suggest(source, exec, "surv"));
        assertEquals(List.of("toggle"), ModeSuggestions.suggest(source, exec, "t"));
    }

    @Test
    @DisplayName("should include toggle for senders with the other permission")
    void shouldIncludeToggleWithOther() {
        CommandSender sender = mock(CommandSender.class);
        lenient().when(sender.hasPermission(GameModePermissions.USE)).thenReturn(true);
        lenient().when(sender.hasPermission(GameModePermissions.SELF)).thenReturn(false);
        lenient().when(sender.hasPermission(GameModePermissions.OTHER)).thenReturn(true);
        lenient().when(sender.hasPermission(GameModePermissions.modeNode(GameModeOption.SURVIVAL))).thenReturn(true);
        sender(sender);

        assertEquals(List.of("survival", "toggle", "s"), ModeSuggestions.suggest(source, exec, ""));
    }
}
