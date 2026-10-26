package com.jruk8.jtemplate.core.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubReleaseHttpTest {

    private static final String RELEASE = """
            {"tag_name":"v1.4.0","prerelease":false,\
            "html_url":"https://github.com/jruk8/JGameMode/releases/tag/v1.4.0"}""";

    @Test
    @DisplayName("should read tag and page URL from a release")
    void shouldParseRelease() {
        var release = GitHubReleaseHttp.parseLatest(RELEASE);

        assertTrue(release.isPresent());
        assertEquals("v1.4.0", release.get().versionNumber());
        assertEquals("https://github.com/jruk8/JGameMode/releases/tag/v1.4.0", release.get().pageUrl());
    }

    @Test
    @DisplayName("should skip prereleases")
    void shouldSkipPrereleases() {
        assertTrue(GitHubReleaseHttp.parseLatest(RELEASE.replace("false", "true")).isEmpty());
    }

    @Test
    @DisplayName("should return empty for unparseable bodies")
    void shouldReturnEmptyForUnparseable() {
        assertTrue(GitHubReleaseHttp.parseLatest("{}").isEmpty());
        assertTrue(GitHubReleaseHttp.parseLatest("not json").isEmpty());
    }
}
