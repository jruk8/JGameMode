package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.commands.mode.GameModeOption;
import com.jruk8.jtemplate.core.commands.mode.GameUsageTracker;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameUsageExpansionTest {

    private final UUID selfId = UUID.randomUUID();
    private final UUID otherId = UUID.randomUUID();

    @Mock
    private GameUsageTracker tracker;

    @Mock
    private JavaPlugin plugin;

    private GameUsageExpansion expansion() {
        return new GameUsageExpansion(tracker, plugin, Map.of("Alex", otherId)::get);
    }

    private OfflinePlayer player(UUID id) {
        OfflinePlayer player = mock(OfflinePlayer.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    private void meta(String name, String version) {
        PluginMeta meta = mock(PluginMeta.class);
        when(meta.getAuthors()).thenReturn(List.of("jruk"));
        when(meta.getVersion()).thenReturn(version);
        when(plugin.getPluginMeta()).thenReturn(meta);
        when(plugin.getName()).thenReturn(name);
    }

    @Test
    @DisplayName("should advertise the jgm identifier and every placeholder")
    void shouldAdvertisePlaceholders() {
        var placeholders = expansion().getPlaceholders();

        assertEquals("jgm", expansion().getIdentifier());
        assertEquals(18, placeholders.size());
        for (String suffix : List.of("uses", "toggle_uses", "survival_uses",
                "creative_uses", "adventure_uses", "spectator_uses")) {
            assertTrue(placeholders.contains("%jgm_" + suffix + "%"));
            assertTrue(placeholders.contains("%jgm_" + suffix + "_<player>%"));
            assertTrue(placeholders.contains("%jgm_" + suffix + "_global%"));
        }
    }

    @Test
    @DisplayName("should read author, version and required plugin from metadata")
    void shouldReadMetadata() {
        meta("JGameMode", "1.0.0");

        assertEquals("jruk", expansion().getAuthor());
        assertEquals("1.0.0", expansion().getVersion());
        assertEquals("JGameMode", expansion().getRequiredPlugin());
    }

    @Test
    @DisplayName("should resolve self counters for the requesting player")
    void shouldResolveSelfCounters() {
        when(tracker.playerUses(selfId)).thenReturn(7);
        when(tracker.playerToggleUses(selfId)).thenReturn(3);
        when(tracker.playerModeUses(selfId, GameModeOption.CREATIVE)).thenReturn(5);
        OfflinePlayer self = player(selfId);

        assertEquals("7", expansion().onRequest(self, "uses"));
        assertEquals("3", expansion().onRequest(self, "toggle_uses"));
        assertEquals("5", expansion().onRequest(self, "creative_uses"));
    }

    @Test
    @DisplayName("should resolve counters for a named player")
    void shouldResolveNamedCounters() {
        when(tracker.playerUses(otherId)).thenReturn(4);
        when(tracker.playerToggleUses(otherId)).thenReturn(2);

        assertEquals("4", expansion().onRequest(mock(OfflinePlayer.class), "uses_Alex"));
        assertEquals("2", expansion().onRequest(mock(OfflinePlayer.class), "toggle_uses_Alex"));
    }

    @Test
    @DisplayName("should resolve global counters")
    void shouldResolveGlobalCounters() {
        when(tracker.globalUses()).thenReturn(11);
        when(tracker.globalToggleUses()).thenReturn(6);
        when(tracker.globalModeUses(GameModeOption.SPECTATOR)).thenReturn(9);

        assertEquals("11", expansion().onRequest(mock(OfflinePlayer.class), "uses_global"));
        assertEquals("6", expansion().onRequest(mock(OfflinePlayer.class), "toggle_uses_global"));
        assertEquals("9", expansion().onRequest(mock(OfflinePlayer.class), "spectator_uses_global"));
    }

    @Test
    @DisplayName("should return null for missing context and unknown params")
    void shouldReturnNullForUnresolvable() {
        assertNull(expansion().onRequest(null, "uses"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "uses_"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "unknown"));
    }
}
