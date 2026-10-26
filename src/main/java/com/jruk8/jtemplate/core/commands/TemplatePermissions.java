package com.jruk8.jtemplate.core.commands;

/**
 * Permission nodes used by the JTemplate commands, declared in
 * {@code paper-plugin.yml}.
 */
public final class TemplatePermissions {

    /**
     * Target other players with commands that support selectors. Never
     * sufficient on its own: changing someone else still needs the
     * matching command permission.
     */
    public static final String OTHER = "jtemplate.other";

    /**
     * Reload the plugin configuration.
     */
    public static final String RELOAD = "jtemplate.reload";

    /**
     * View the plugin help listing. Entries are filtered by permission.
     */
    public static final String HELP = "jtemplate.help";

    /**
     * View the plugin version.
     */
    public static final String VERSION = "jtemplate.version";

    private TemplatePermissions() {
    }
}
