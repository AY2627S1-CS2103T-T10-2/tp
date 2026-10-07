package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    //---------------- Tests for isNonZeroUnsignedInteger --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger() {

        // EP: empty strings
        assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value
        assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: number larger than Integer.MAX_VALUE
        assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value
        assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }


    //---------------- Tests for containsSubwordIgnoreCase --------------------------------------

    /*
     * Invalid equivalence partitions for subword: null, empty, multiple words
     * Invalid equivalence partitions for sentence: null
     * The four test cases below test one invalid input at a time.
     */

    @Test
    public void containsSubwordIgnoreCase_nullWord_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                StringUtil.containsSubwordIgnoreCase("typical sentence", null));
    }

    @Test
    public void containsSubwordIgnoreCase_emptyWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter cannot be empty", ()
            -> StringUtil.containsSubwordIgnoreCase("typical sentence", "  "));
    }

    @Test
    public void containsSubwordIgnoreCase_multipleWords_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, "Word parameter should be a single word", ()
            -> StringUtil.containsSubwordIgnoreCase("typical sentence", "aaa BBB"));
    }

    @Test
    public void containsSubwordIgnoreCase_nullSentence_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                StringUtil.containsSubwordIgnoreCase(null, "abc"));
    }

    /*
     * Valid equivalence partitions for subword:
     *   - any partial word
     *   - full word
     *   - subword containing symbols/numbers
     *   - word with leading/trailing spaces
     *
     * Valid equivalence partitions for sentence:
     *   - empty string
     *   - one word
     *   - multiple words
     *   - sentence with extra spaces
     *
     * Possible scenarios returning true:
     *   - subword matches the start of a word
     *   - subword matches the middle of a word
     *   - subword matches the end of a word
     *   - subword matches the whole word
     *   - subword matches parts of multiple words
     *   - subword matches in the first, middle, or last word of the sentence
     *   - subword matches with different upper/lower case letters
     *
     * Possible scenarios returning false:
     *   - subword is longer than any word in the sentence (sentence word is a part of the subword)
     *   - subword does not appear anywhere in the sentence
     *
     * The test method below tries to verify all above with a reasonably low number of test cases.
     */

    @Test
    public void containsSubwordIgnoreCase_validInputs_correctResult() {

        // Empty sentence
        assertFalse(StringUtil.containsSubwordIgnoreCase("", "abc")); // Boundary case
        assertFalse(StringUtil.containsSubwordIgnoreCase("    ", "123"));

        // Matches a partial word (subword is smaller than sentence word)
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "aa")); // Start of first word (boundary case)
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "bb")); // Middle word
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc@1", "@1")); // End of last word (boundary case)
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "b")); // Middle of a word
        assertTrue(StringUtil.containsSubwordIgnoreCase("Aaa", "aa")); // Only one word in sentence (boundary case)
        assertTrue(StringUtil.containsSubwordIgnoreCase("  AAA   bBb   ccc  ", "a")); // Sentence has extra spaces

        // Matches a full word
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "bbb"));

        // Matches with different upper/lower case letters
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bBb ccc", "AaA")); // First word
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bBb ccc@1", "CCc@1")); // Last word
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bBb ccc", "bB")); // Partial word, mixed case

        // Subword has leading/trailing spaces (trimmed before matching)
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "  c  ")); // Partial word
        assertTrue(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "  ccc  ")); // Full word

        // Matches parts of multiple words in sentence
        assertTrue(StringUtil.containsSubwordIgnoreCase("AAA bBb ccc  bbb", "bbB"));

        // Does not match: subword is bigger than the sentence word
        assertFalse(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "bbbb"));

        // Does not match: subword not in sentence
        assertFalse(StringUtil.containsSubwordIgnoreCase("aaa bbb ccc", "xyz"));
    }

    //---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void getDetails_exceptionGiven() {
        assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
            .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

}
