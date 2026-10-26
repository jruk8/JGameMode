package com.jruk8.jtemplate.core.update;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Update check engine: fetches the latest GitHub release and stores a
 * newer version as pending. Only JDK types are referenced, so the engine
 * stays easy to unit test; the caller broadcasts the pending update on
 * the main thread.
 */
public final class UpdateCheckService {

    private static final String MODRINTH_SLUG = "jtemplate";

    private final UpdateCheckHttp http;
    private final Logger logger;
    private volatile UpdateAvailable pending;

    public UpdateCheckService(UpdateCheckHttp http, Logger logger) {
        this.http = http;
        this.logger = logger;
    }

    /**
     * Returns the last found update, if any.
     *
     * @return the pending update
     */
    public Optional<UpdateAvailable> pendingUpdate() {
        return Optional.ofNullable(pending);
    }

    /**
     * Runs one check against the current version. Blocking: call off the
     * main thread. A found update is stored as pending and logged; the
     * caller announces it to players on the main thread. Failures log
     * once and leave no pending update.
     *
     * @param settings the checker toggles
     * @param currentVersion the running plugin version
     */
    public void checkNow(UpdateCheckSettings settings, String currentVersion) {
        pending = null;
        if (!settings.enabled()) {
            return;
        }
        GitHubRelease release;
        try {
            Optional<GitHubRelease> fetched = http.fetchLatest();
            if (fetched.isEmpty()) {
                return;
            }
            release = fetched.get();
        } catch (Exception exception) {
            logger.warning("Update check failed: " + exception.getMessage());
            return;
        }
        UpdateVersion current = UpdateVersion.parse(currentVersion);
        UpdateVersion latest = UpdateVersion.parse(release.versionNumber());
        if (!latest.isNewerThan(current)) {
            return;
        }
        pending = new UpdateAvailable(
                latest.toString(), current.toString(), release.pageUrl(), modrinthPageUrl());
        logger.info("JTemplate " + latest + " is available (running " + current + "): " + release.pageUrl());
    }

    private static String modrinthPageUrl() {
        return "https://modrinth.com/project/" + MODRINTH_SLUG;
    }
}
