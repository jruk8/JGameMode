package com.jruk8.jtemplate.core.update;

import java.util.Optional;

/**
 * Fetches releases from the update host. Blocking: call off the main thread.
 */
public interface UpdateCheckHttp {

    /**
     * Fetches the newest release from the update host.
     *
     * @return the newest release, or empty when none is published
     * @throws Exception when the request or response fails
     */
    Optional<GitHubRelease> fetchLatest() throws Exception;
}
