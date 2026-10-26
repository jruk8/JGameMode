package com.jruk8.jtemplate.core.update;

/**
 * One GitHub release: its tag name and the release page URL.
 */
public record GitHubRelease(String versionNumber, String pageUrl) {
}
