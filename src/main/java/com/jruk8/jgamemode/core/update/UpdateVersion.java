package com.jruk8.jgamemode.core.update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Three-part plugin version: major.minor.patch. Parsing scans the raw
 * text for x.y.z (a leading v and any trailing snapshot text are
 * ignored), falls back to x.y, then x, then 0.0.0 when no digits exist.
 */
public record UpdateVersion(int major, int minor, int patch) {

    private static final Pattern TRIPLE = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final Pattern PAIR = Pattern.compile("(\\d+)\\.(\\d+)");
    private static final Pattern SINGLE = Pattern.compile("(\\d+)");

    /**
     * Parses a version from free text like a release tag.
     *
     * @param raw the raw text, e.g. "v1.2.0" or "1.2.0-SNAPSHOT"
     * @return the parsed version, or 0.0.0 when no digits are found
     */
    public static UpdateVersion parse(String raw) {
        String text = raw == null ? "" : raw;
        Matcher triple = TRIPLE.matcher(text);
        if (triple.find()) {
            return new UpdateVersion(number(triple.group(1)), number(triple.group(2)), number(triple.group(3)));
        }
        Matcher pair = PAIR.matcher(text);
        if (pair.find()) {
            return new UpdateVersion(number(pair.group(1)), number(pair.group(2)), 0);
        }
        Matcher single = SINGLE.matcher(text);
        if (single.find()) {
            return new UpdateVersion(number(single.group(1)), 0, 0);
        }
        return new UpdateVersion(0, 0, 0);
    }

    private static int number(String digits) {
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException overflow) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Returns whether this version is strictly newer than the other.
     *
     * @param other the version to compare against
     * @return {@code true} when this version is newer
     */
    public boolean isNewerThan(UpdateVersion other) {
        if (major != other.major) {
            return major > other.major;
        }
        if (minor != other.minor) {
            return minor > other.minor;
        }
        return patch > other.patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
