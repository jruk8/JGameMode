package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.commands.user.version.AuthorFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuthorFormatterTest {

    private AuthorFormatter authorFormatter;

    @BeforeEach
    void setUp() {
        authorFormatter = new AuthorFormatter();
    }

    @Nested
    @DisplayName("formatSingleAuthor tests")
    class SingleAuthorTests {

        @Test
        @DisplayName("should return single author as-is")
        void shouldReturnSingleAuthor() {
            String result = authorFormatter.formatSingleAuthor("MyAuthor");
            assertEquals("MyAuthor", result);
        }
    }

    @Nested
    @DisplayName("formatTwoAuthors tests")
    class TwoAuthorTests {

        @Test
        @DisplayName("should format two authors with 'and' in gray")
        void shouldFormatTwoAuthorsWithAnd() {
            String result = authorFormatter.formatTwoAuthors("MyAuthor", "MyOther");
            assertEquals("MyAuthor <gray>and</gray> MyOther", result);
        }
    }

    @Nested
    @DisplayName("formatThreePlusAuthors tests")
    class ThreePlusAuthorTests {

        @Test
        @DisplayName("should format three authors with commas and 'and' in gray")
        void shouldFormatThreeAuthors() {
            List<String> authors = List.of("MyAuthor", "MyOther", "MyOtherOther");
            String result = authorFormatter.formatThreePlusAuthors(authors);
            assertEquals("MyAuthor<gray>,</gray> MyOther<gray>, and</gray> MyOtherOther", result);
        }

        @Test
        @DisplayName("should format four authors")
        void shouldFormatFourAuthors() {
            List<String> authors = List.of("Author1", "Author2", "Author3", "Author4");
            String result = authorFormatter.formatThreePlusAuthors(authors);
            assertEquals("Author1<gray>,</gray> Author2<gray>,</gray> Author3<gray>, and</gray> Author4", result);
        }
    }

    @Nested
    @DisplayName("formatAuthors tests")
    class FormatAuthorsTests {

        @Test
        @DisplayName("should format single author")
        void shouldFormatSingleAuthor() {
            String result = authorFormatter.formatAuthors(List.of("MyAuthor"));
            assertEquals("MyAuthor", result);
        }

        @Test
        @DisplayName("should format two authors with 'and'")
        void shouldFormatTwoAuthors() {
            String result = authorFormatter.formatAuthors(List.of("MyAuthor", "MyOther"));
            assertEquals("MyAuthor <gray>and</gray> MyOther", result);
        }

        @Test
        @DisplayName("should format three authors with commas and 'and'")
        void shouldFormatThreeAuthors() {
            List<String> authors = List.of("MyAuthor", "MyOther", "MyOtherOther");
            String result = authorFormatter.formatAuthors(authors);
            assertEquals("MyAuthor<gray>,</gray> MyOther<gray>, and</gray> MyOtherOther", result);
        }

        @Test
        @DisplayName("should return empty string for no authors")
        void shouldReturnEmptyForNoAuthors() {
            String result = authorFormatter.formatAuthors(Collections.emptyList());
            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("author links")
    class AuthorLinkTests {

        @Test
        @DisplayName("should link a known author to their page")
        void shouldLinkKnownAuthor() {
            String result = authorFormatter.formatSingleAuthor("jruk");
            assertEquals(
                    "<click:open_url:'https://github.com/jruk8'>"
                            + "<hover:show_text:'https://github.com/jruk8'>jruk</hover></click>",
                    result);
        }

        @Test
        @DisplayName("should link only the known author in a pair")
        void shouldLinkOnlyKnownAuthor() {
            String result = authorFormatter.formatTwoAuthors("jruk", "MyOther");
            assertEquals(
                    "<click:open_url:'https://github.com/jruk8'>"
                            + "<hover:show_text:'https://github.com/jruk8'>jruk</hover></click>"
                            + " <gray>and</gray> MyOther",
                    result);
        }
    }
}