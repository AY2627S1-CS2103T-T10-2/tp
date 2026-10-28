package seedu.address.model.gradebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class AssignmentTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");

    @Test
    public void constructor_normalizesNameAndMaximum() {
        Assignment assignment = new Assignment(id, "  Assignment   One  ", new BigDecimal("20.00"));
        assertEquals("Assignment One", assignment.getName());
        assertEquals("assignment one", assignment.getNameKey());
        assertEquals(0, new BigDecimal("20").compareTo(assignment.getMaxMark()));
    }

    @Test
    public void constructor_scientificNotation_normalizesMaximum() {
        assertEquals(new BigDecimal("17.5"),
                new Assignment(id, "Assignment One", new BigDecimal("1.7500e1")).getMaxMark());
        assertEquals(BigDecimal.ONE,
                new Assignment(id, "Assignment One", new BigDecimal("100e-2")).getMaxMark());
        assertEquals(BigDecimal.ONE,
                new Assignment(id, "Assignment One", new BigDecimal("1." + "0".repeat(100))).getMaxMark());
    }

    @Test
    public void constructor_invalidDetails_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_NAME_CONSTRAINTS, () ->
                new Assignment(id, "   ", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("0e2147483647")));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("0e-2147483647")));
    }

    @Test
    public void constructor_displayLengthLimit_rejectsExpandedScientificNotation() {
        assertEquals(64, new Assignment(id, "Assignment One", new BigDecimal("1e63"))
                .getMaxMark().toPlainString().length());
        assertEquals(64, new Assignment(id, "Assignment One", new BigDecimal("1e-62"))
                .getMaxMark().toPlainString().length());
        assertEquals(64, new Assignment(id, "Assignment One", new BigDecimal("100e61"))
                .getMaxMark().toPlainString().length());
        assertEquals(64, new Assignment(id, "Assignment One", new BigDecimal("100e-64"))
                .getMaxMark().toPlainString().length());
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("1e64")));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("1e-63")));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", new BigDecimal("1e1000000")));
    }

    @Test
    public void constructor_extremeScientificNotation_throwsLengthConstraint() {
        String[] inputs = {"100e2147483647", "100e2147483646", "10e2147483647", "1e-2147483647"};
        for (String input : inputs) {
            assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS, () ->
                    new Assignment(id, "Assignment One", new BigDecimal(input)));
        }
    }
}
