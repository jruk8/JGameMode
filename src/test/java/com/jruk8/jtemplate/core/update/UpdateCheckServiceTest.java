package com.jruk8.jtemplate.core.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCheckServiceTest {

    @Mock
    private UpdateCheckHttp http;

    @Mock
    private Logger logger;

    private UpdateCheckService service() {
        return new UpdateCheckService(http, logger);
    }

    private GitHubRelease release(String number) {
        return new GitHubRelease(number, "https://github.com/jruk8/JGameMode/releases/tag/v" + number);
    }

    @Test
    @DisplayName("should store a newer release as pending for the caller to announce")
    void shouldKeepPendingUpdate() throws Exception {
        when(http.fetchLatest()).thenReturn(Optional.of(release("1.4.0")));
        var checked = service();

        checked.checkNow(new UpdateCheckSettings(true), "1.2.0");

        var pending = checked.pendingUpdate();
        assertTrue(pending.isPresent());
        assertEquals("1.4.0", pending.get().latest());
        assertEquals("1.2.0", pending.get().current());
    }

    @Test
    @DisplayName("should stay silent when disabled")
    void shouldStaySilentWhenDisabled() throws Exception {
        service().checkNow(new UpdateCheckSettings(false), "1.2.0");

        verify(http, never()).fetchLatest();
        assertTrue(service().pendingUpdate().isEmpty());
    }

    @Test
    @DisplayName("should stay silent when current")
    void shouldStaySilentWhenCurrent() throws Exception {
        when(http.fetchLatest()).thenReturn(Optional.of(release("1.2.0")));
        var checked = service();

        checked.checkNow(new UpdateCheckSettings(true), "1.2.0");

        assertTrue(checked.pendingUpdate().isEmpty());
    }

    @Test
    @DisplayName("should log and clear the pending update on failure")
    void shouldClearPendingOnFailure() throws Exception {
        when(http.fetchLatest())
                .thenReturn(Optional.of(release("1.4.0")))
                .thenThrow(new RuntimeException("boom"));
        var checked = service();
        checked.checkNow(new UpdateCheckSettings(true), "1.2.0");

        checked.checkNow(new UpdateCheckSettings(true), "1.2.0");

        assertTrue(checked.pendingUpdate().isEmpty());
        verify(logger).warning(any(String.class));
    }
}
