package com.jruk8.jtemplate.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Registry and orchestrator for components that implement {@link Reloadable}.
 * <p>
 * Sequentially executes reloads and logs failures without aborting the batch.
 */
public final class Reloader {

    @Getter
    private final List<Reloadable> reloadables = new ArrayList<>();
    private final Logger logger;

    public Reloader(Logger logger) {
        this.logger = logger;
    }

    /**
     * Registers a reloadable component.
     *
     * @param reloadable the component to register
     * @return this instance for chaining
     */
    public Reloader register(Reloadable reloadable) {
        if (reloadable != null) {
            reloadables.add(reloadable);
        }
        return this;
    }

    /**
     * Reloads all registered components.
     * <p>
     * A single failure is logged and tracked, but will not prevent
     * remaining components from reloading.
     *
     * @return {@code true} if every component reloaded successfully, {@code false} if any threw an exception
     */
    public boolean reloadAll() {
        boolean allSucceeded = true;

        for (Reloadable reloadable : reloadables) {
            try {
                reloadable.reload();
            } catch (Exception e) {
                allSucceeded = false;
                logger.warning("Failed to reload " + reloadable.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        return allSucceeded;
    }
}