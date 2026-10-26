package com.jruk8.jtemplate.core.commands.user.version;

import java.util.List;
import java.util.Map;

/**
 * Business logic class for formatting author lists.
 * Follows clean architecture principles - pure formatting logic with no dependencies on framework types.
 * Known authors link to their pages; unknown names render as-is.
 */
public class AuthorFormatter {

    private static final Map<String, String> AUTHOR_LINKS = Map.of(
            "jruk", "https://github.com/jruk8");

    /**
     * Formats a single author name.
     *
     * @param author the author name
     * @return the author name, linked when a URL is known
     */
    public String formatSingleAuthor(String author) {
        return linked(author);
    }

    /**
     * Formats two authors with "and" in gray between them.
     *
     * @param firstAuthor  the first author
     * @param secondAuthor the second author
     * @return formatted string like "MyAuthor <gray>and</gray> MyOther"
     */
    public String formatTwoAuthors(String firstAuthor, String secondAuthor) {
        return linked(firstAuthor) + " <gray>and</gray> " + linked(secondAuthor);
    }

    /**
     * Formats three or more authors with commas and "and" in gray.
     * Format: "MyAuthor<gray>,</gray> MyOther<gray>, and</gray> MyOtherOther"
     *
     * @param authors list of author names (must have at least 3 elements)
     * @return formatted string with gray commas and "and"
     */
    public String formatThreePlusAuthors(List<String> authors) {
        if (authors.size() < 3) {
            return formatTwoAuthors(authors.get(0), authors.get(1));
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < authors.size(); i++) {
            String author = linked(authors.get(i));

            if (i == 0) {
                result.append(author);
            } else if (i == authors.size() - 1) {
                // Last author with "and" in gray
                result.append("<gray>, and</gray> ").append(author);
            } else {
                // Middle authors with comma in gray
                result.append("<gray>,</gray> ").append(author);
            }
        }

        return result.toString();
    }

    private static String linked(String author) {
        String url = AUTHOR_LINKS.get(author);
        if (url == null) {
            return author;
        }
        return "<click:open_url:'" + url + "'><hover:show_text:'" + url + "'>" + author + "</hover></click>";
    }

    /**
     * Formats authors list based on the number of authors.
     *
     * @param authors list of author names
     * @return properly formatted author string
     */
    public String formatAuthors(List<String> authors) {
        if (authors.isEmpty()) {
            return "";
        }

        int count = authors.size();
        if (count == 1) {
            return linked(authors.getFirst());
        } else if (count == 2) {
            return formatTwoAuthors(authors.get(0), authors.get(1));
        }
        return formatThreePlusAuthors(authors);
    }
}
