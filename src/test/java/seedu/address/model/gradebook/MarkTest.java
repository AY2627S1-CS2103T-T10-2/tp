package seedu.address.model.gradebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class MarkTest {
    @Test
    public void constructor_normalizesValueWithoutLosingFraction() {
        assertEquals(new Mark(new BigDecimal("17.5")), new Mark(new BigDecimal("17.50")));
        assertEquals("17.5", new Mark(new BigDecimal("17.50")).toString());
        assertEquals("0", new Mark(new BigDecimal("0.00")).toString());
    }

    @Test
    public void constructor_scientificNotation_normalizesValue() {
        assertEquals("17.5", new Mark(new BigDecimal("1.7500e1")).toString());
        assertEquals("1", new Mark(new BigDecimal("100e-2")).toString());
        assertEquals("1", new Mark(new BigDecimal("1." + "0".repeat(100))).toString());
        assertEquals("0", new Mark(new BigDecimal("0e2147483647")).toString());
        assertEquals("0", new Mark(new BigDecimal("0e-2147483647")).toString());
    }

    @Test
    public void constructor_negativeValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Mark.MESSAGE_CONSTRAINTS, () ->
                new Mark(new BigDecimal("-0.1")));
    }

    @Test
    public void constructor_displayLengthLimit_rejectsExpandedScientificNotation() {
        assertEquals(64, new Mark(new BigDecimal("1e63")).toString().length());
        assertEquals(64, new Mark(new BigDecimal("1e-62")).toString().length());
        assertEquals(64, new Mark(new BigDecimal("100e61")).toString().length());
        assertEquals(64, new Mark(new BigDecimal("100e-64")).toString().length());
        assertEquals("0", new Mark(new BigDecimal("0e-1000000")).toString());
        assertThrows(IllegalArgumentException.class, Mark.MESSAGE_LENGTH_CONSTRAINTS, () ->
                new Mark(new BigDecimal("1e64")));
        assertThrows(IllegalArgumentException.class, Mark.MESSAGE_LENGTH_CONSTRAINTS, () ->
                new Mark(new BigDecimal("1e-63")));
        assertThrows(IllegalArgumentException.class, Mark.MESSAGE_LENGTH_CONSTRAINTS, () ->
                new Mark(new BigDecimal("1e1000000")));
    }

    @Test
    public void constructor_extremeScientificNotation_throwsLengthConstraint() {
        String[] inputs = {"100e2147483647", "100e2147483646", "10e2147483647", "1e-2147483647"};
        for (String input : inputs) {
            assertThrows(IllegalArgumentException.class, Mark.MESSAGE_LENGTH_CONSTRAINTS, () ->
                    new Mark(new BigDecimal(input)));
        }
    }
}
