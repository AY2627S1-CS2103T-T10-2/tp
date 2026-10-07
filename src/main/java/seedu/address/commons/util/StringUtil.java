package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Locale;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, both partial and full word matches are allowed.
     *   <br>examples:<pre>
     *       containsSubwordIgnoreCase("ABc def", "abc") == true
     *       containsSubwordIgnoreCase("ABc def", "DEF") == true
     *       containsSubwordIgnoreCase("ABc def", "AB") == true
     *       containsSubwordIgnoreCase("ABc def", "g") == false //g not in sentence
     *       </pre>
     * @param sentence cannot be null
     * @param subword cannot be null, cannot be empty, must be a substring of a single word
     */
    public static boolean containsSubwordIgnoreCase(String sentence, String subword) {
        requireNonNull(sentence);
        requireNonNull(subword);

        String preppedSubWord = subword.trim().toLowerCase(Locale.ROOT);
        checkArgument(!preppedSubWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedSubWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence.toLowerCase(Locale.ROOT);
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(wordInPreppedSentence -> wordInPreppedSentence.contains(preppedSubWord));
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
