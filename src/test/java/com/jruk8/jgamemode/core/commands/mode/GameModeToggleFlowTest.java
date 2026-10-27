package com.jruk8.jgamemode.core.commands.mode;

import com.jruk8.jgamemode.core.commands.CommandContext;
import com.jruk8.jgamemode.core.storage.SqliteStorage;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * End-to-end toggle flow: set to survival, toggle twice, and the second
 * toggle must return to survival rather than the creative fallback. Uses
 * the real history and storage so a lost write fails here first.
 */
@ExtendWith(MockitoExtension.class)
class GameModeToggleFlowTest {

    private final UUID playerId = UUID.randomUUID();

    @Mock
    private CommandContext ctx;

    @Mock
    private CommandSourceStack source;

    private SqliteStorage storage;
    private final AtomicReference<GameMode> current = new AtomicReference<>(GameMode.CREATIVE);

    @BeforeEach
    void setUp() {
        storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("toggle-flow-test"));
        assertTrue(storage.init());
    }

    @AfterEach
    void tearDown() {
        storage.close();
    }

    private Player player() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(player.getGameMode()).thenAnswer(ignored -> current.get());
        doAnswer(invocation -> {
            current.set(invocation.getArgument(0));
            return null;
        }).when(player).setGameMode(org.mockito.ArgumentMatchers.any(GameMode.class));
        when(source.getSender()).thenReturn(player);
        return player;
    }

    private GameModeExecutor executor() {
        return new GameModeExecutor(ctx, new ToggleHistory(storage), new GameUsageTracker(storage));
    }

    @Test
    @DisplayName("should return to survival after set plus two toggles")
    void shouldReturnToSurvival() {
        Player player = player();

        assertEquals(1, executor().set(source, GameModeOption.SURVIVAL, List.of(player)));
        assertEquals(GameMode.SURVIVAL, current.get());

        assertEquals(1, executor().toggle(source, List.of(player)));
        assertEquals(GameMode.SPECTATOR, current.get());

        assertEquals(1, executor().toggle(source, List.of(player)));
        assertEquals(GameMode.SURVIVAL, current.get());
    }

    @Test
    @DisplayName("should return to survival after a direct set to spectator plus toggle")
    void shouldReturnToSurvivalAfterDirectSpectator() {
        Player player = player();
        current.set(GameMode.SURVIVAL);

        assertEquals(1, executor().set(source, GameModeOption.SPECTATOR, List.of(player)));
        assertEquals(GameMode.SPECTATOR, current.get());

        assertEquals(1, executor().toggle(source, List.of(player)));
        assertEquals(GameMode.SURVIVAL, current.get());
    }
}
