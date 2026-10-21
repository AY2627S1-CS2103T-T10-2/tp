package seedu.address.model.gradebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AssignmentIdTest {
    @Test
    public void constructor_validId_normalizesCase() {
        AssignmentId id = new AssignmentId("cs2103t:a1");
        assertEquals("CS2103T:A1", id.toString());
        assertEquals(new AssignmentId("CS2103T:A1"), id);
        assertEquals(new AssignmentId("CS2103T:A1").hashCode(), id.hashCode());
    }

    @Test
    public void constructor_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, AssignmentId.MESSAGE_CONSTRAINTS, () ->
                new AssignmentId("CS2103T"));
        assertThrows(IllegalArgumentException.class, AssignmentId.MESSAGE_CONSTRAINTS, () ->
                new AssignmentId("CS2103T:1"));
        assertFalse(AssignmentId.isValidAssignmentId("CS2103T:A 1"));
        assertTrue(AssignmentId.isValidAssignmentId("CS2103T:A1"));
    }
}
