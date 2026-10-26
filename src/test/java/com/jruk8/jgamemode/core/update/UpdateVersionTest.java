package com.jruk8.jgamemode.core.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateVersionTest {

    @Test
    @DisplayName("should parse a plain triple")
    void shouldParseTriple() {
        assertEquals(new UpdateVersion(1, 2, 3), UpdateVersion.parse("1.2.3"));
    }

    @Test
    @DisplayName("should ignore a leading v and trailing qualifiers")
    void shouldIgnoreTagNoise() {
        assertEquals(new UpdateVersion(1, 2, 0), UpdateVersion.parse("v1.2.0-SNAPSHOT"));
    }

    @Test
    @DisplayName("should fall back to pairs, singles and zero")
    void shouldFallBack() {
        assertEquals(new UpdateVersion(1, 2, 0), UpdateVersion.parse("1.2"));
        assertEquals(new UpdateVersion(4, 0, 0), UpdateVersion.parse("release-4"));
        assertEquals(new UpdateVersion(0, 0, 0), UpdateVersion.parse("no-digits"));
        assertEquals(new UpdateVersion(0, 0, 0), UpdateVersion.parse(null));
    }

    @Test
    @DisplayName("should compare component by component")
    void shouldCompare() {
        assertTrue(new UpdateVersion(2, 0, 0).isNewerThan(new UpdateVersion(1, 9, 9)));
        assertTrue(new UpdateVersion(1, 3, 0).isNewerThan(new UpdateVersion(1, 2, 9)));
        assertTrue(new UpdateVersion(1, 2, 1).isNewerThan(new UpdateVersion(1, 2, 0)));
        assertFalse(new UpdateVersion(1, 2, 0).isNewerThan(new UpdateVersion(1, 2, 0)));
        assertFalse(new UpdateVersion(1, 2, 0).isNewerThan(new UpdateVersion(1, 2, 1)));
    }

    @Test
    @DisplayName("should render as major.minor.patch")
    void shouldRender() {
        assertEquals("1.2.3", new UpdateVersion(1, 2, 3).toString());
    }
}
