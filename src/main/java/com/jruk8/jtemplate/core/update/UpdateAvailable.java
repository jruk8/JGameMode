package com.jruk8.jtemplate.core.update;

/**
 * A found update: the new and running versions plus the GitHub release
 * page and the Modrinth project page.
 */
public record UpdateAvailable(String latest, String current, String url, String modrinthUrl) {
}
