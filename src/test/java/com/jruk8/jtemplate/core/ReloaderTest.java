package com.jruk8.jtemplate.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReloaderTest {

    @Mock
    private Logger logger;

    @Mock
    private Reloadable reloadable1;

    @Mock
    private Reloadable reloadable2;

    @Mock
    private Reloadable reloadable3;

    private Reloader reloader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reloader = new Reloader(logger);
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("should add valid reloadable")
        void shouldAddValidReloadable() {
            reloader.register(reloadable1);
            assertEquals(1, reloader.getReloadables().size());
        }

        @Test
        @DisplayName("should ignore null reloadable")
        void shouldIgnoreNullReloadable() {
            reloader.register(null);
            assertEquals(0, reloader.getReloadables().size());
        }

        @Test
        @DisplayName("should allow multiple reloadables")
        void shouldAllowMultipleReloadables() {
            reloader.register(reloadable1);
            reloader.register(reloadable2);
            reloader.register(reloadable3);
            assertEquals(3, reloader.getReloadables().size());
        }

        @Test
        @DisplayName("should return this for chaining")
        void shouldReturnThisForChaining() {
            Reloader result = reloader.register(reloadable1);
            assertSame(reloader, result);
        }
    }

    @Nested
    @DisplayName("reloadAll()")
    class ReloadAllTests {

        @Test
        @DisplayName("should return true when all reloadables succeed")
        void shouldReturnTrueWhenAllSucceed() {
            reloader.register(reloadable1);
            reloader.register(reloadable2);

            boolean result = reloader.reloadAll();

            assertTrue(result);
            verify(reloadable1, times(1)).reload();
            verify(reloadable2, times(1)).reload();
            verifyNoInteractions(logger);
        }

        @Test
        @DisplayName("should return false when one reloadable throws")
        void shouldReturnFalseWhenOneThrows() {
            reloader.register(reloadable1);
            reloader.register(reloadable2);

            doThrow(new RuntimeException("Test exception"))
                .when(reloadable2).reload();

            boolean result = reloader.reloadAll();

            assertFalse(result);
            verify(reloadable1, times(1)).reload();
            verify(reloadable2, times(1)).reload();
            verify(logger, times(1)).warning(contains("Failed to reload"));
        }

        @Test
        @DisplayName("should continue reloading after one failure")
        void shouldContinueAfterFailure() {
            reloader.register(reloadable1);
            reloader.register(reloadable2);
            reloader.register(reloadable3);

            doThrow(new RuntimeException("Test exception"))
                .when(reloadable2).reload();

            boolean result = reloader.reloadAll();

            assertFalse(result);
            verify(reloadable1, times(1)).reload();
            verify(reloadable2, times(1)).reload();
            verify(reloadable3, times(1)).reload();
            verify(logger, times(1)).warning(contains("Failed to reload"));
        }

        @Test
        @DisplayName("should return true when no reloadables registered")
        void shouldReturnTrueWhenEmpty() {
            boolean result = reloader.reloadAll();
            assertTrue(result);
            verifyNoInteractions(logger);
        }
    }
}
