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
    public void constructor_negativeValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Mark.MESSAGE_CONSTRAINTS, () ->
                new Mark(new BigDecimal("-0.1")));
    }
}
