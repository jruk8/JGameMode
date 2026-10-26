package com.jruk8.jtemplate.core.placeholders;

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
class UsageExpansionTest {

    private final UUID selfId = UUID.randomUUID();
    private final UUID otherId = UUID.randomUUID();

    @Mock
    private CommandUsageTracker tracker;

    @Mock
    private JavaPlugin plugin;

    private UsageExpansion expansion() {
        return new UsageExpansion(tracker, plugin, Map.of("Alex", otherId)::get);
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
    @DisplayName("should advertise the jtemplate identifier and both placeholders")
    void shouldAdvertisePlaceholders() {
        var placeholders = expansion().getPlaceholders();

        assertEquals("jtemplate", expansion().getIdentifier());
        assertEquals(2, placeholders.size());
        assertTrue(placeholders.contains("%jtemplate_uses%"));
        assertTrue(placeholders.contains("%jtemplate_uses_<player>%"));
    }

    @Test
    @DisplayName("should read author, version and required plugin from metadata")
    void shouldReadMetadata() {
        meta("JTemplate", "1.0.0");

        assertEquals("jruk", expansion().getAuthor());
        assertEquals("1.0.0", expansion().getVersion());
        assertEquals("JTemplate", expansion().getRequiredPlugin());
    }

    @Test
    @DisplayName("should resolve the counter for the requesting player")
    void shouldResolveSelfCounter() {
        when(tracker.playerUses(selfId)).thenReturn(7);
        OfflinePlayer self = player(selfId);

        assertEquals("7", expansion().onRequest(self, "uses"));
    }

    @Test
    @DisplayName("should resolve the counter for a named player")
    void shouldResolveNamedCounter() {
        when(tracker.playerUses(otherId)).thenReturn(4);

        assertEquals("4", expansion().onRequest(mock(OfflinePlayer.class), "uses_Alex"));
    }

    @Test
    @DisplayName("should return null for missing context and unknown params")
    void shouldReturnNullForUnresolvable() {
        assertNull(expansion().onRequest(null, "uses"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "uses_"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "unknown"));
    }
}
