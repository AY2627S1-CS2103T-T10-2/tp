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
    public void constructor_invalidDetails_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_NAME_CONSTRAINTS, () ->
                new Assignment(id, "   ", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, Assignment.MESSAGE_MAX_MARK_CONSTRAINTS, () ->
                new Assignment(id, "Assignment One", BigDecimal.ZERO));
    }
}
