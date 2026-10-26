package com.jruk8.jtemplate.core;

/**
 * Common contract for plugin subsystems that need to reload
 * their components (commands, listeners, etc.) on reload.
 */
public interface Reloadable {

    void reload();
}