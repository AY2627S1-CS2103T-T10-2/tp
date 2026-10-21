package seedu.address.model.gradebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.MatriculationNumber;

public class GradebookTest {
    private final AssignmentId firstId = new AssignmentId("CS2103T:A1");
    private final AssignmentId secondId = new AssignmentId("CS2103T:A2");
    private final MatriculationNumber firstStudent = new MatriculationNumber("A1234567X");
    private final MatriculationNumber secondStudent = new MatriculationNumber("A7654321Y");

    @Test
    public void addAssignment_duplicateIdOrName_rejectedWithinModule() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));

        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_DUPLICATE_ID, () ->
                gradebook.addAssignment(new Assignment(firstId, "Different", BigDecimal.TEN)));
        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_DUPLICATE_NAME, () ->
                gradebook.addAssignment(new Assignment(secondId, " assignment   ONE ", BigDecimal.TEN)));
        gradebook.addAssignment(new Assignment(new AssignmentId("CS2101:A1"), "Assignment One", BigDecimal.TEN));
        assertEquals(2, gradebook.getAssignments().size());
    }

    @Test
    public void assignAndRecordMark_unmarkedIsDifferentFromZero() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));
        gradebook.assignStudent(firstStudent, firstId);
        gradebook.assignStudent(secondStudent, firstId);

        assertEquals(Optional.empty(), gradebook.getStudentMarks(firstId).get(firstStudent));
        assertEquals(Optional.empty(), gradebook.recordMark(firstStudent, firstId, new Mark(BigDecimal.ZERO)));
        assertEquals(Optional.of(new Mark(BigDecimal.ZERO)), gradebook.getStudentMarks(firstId).get(firstStudent));
        assertEquals(Optional.empty(), gradebook.getStudentMarks(firstId).get(secondStudent));

        Mark updatedMark = new Mark(new BigDecimal("17.5"));
        assertEquals(Optional.of(new Mark(BigDecimal.ZERO)), gradebook.recordMark(firstStudent, firstId, updatedMark));
        assertEquals(Optional.of(updatedMark), gradebook.getMarksForStudent(firstStudent).get(firstId));
    }

    @Test
    public void assignAndRecordMark_invalidOperation_doesNotChangeGradebook() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));
        gradebook.assignStudent(firstStudent, firstId);
        Gradebook original = new Gradebook(gradebook);

        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT, () ->
                gradebook.assignStudent(firstStudent, firstId));
        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_UNASSIGNED_STUDENT, () ->
                gradebook.recordMark(secondStudent, firstId, new Mark(BigDecimal.ONE)));
        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_MARK_EXCEEDS_MAX, () ->
                gradebook.recordMark(firstStudent, firstId, new Mark(new BigDecimal("20.1"))));
        assertEquals(original, gradebook);
    }

    @Test
    public void removeOperations_preserveUnrelatedAssignments() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));
        gradebook.addAssignment(new Assignment(secondId, "Assignment Two", new BigDecimal("20")));
        gradebook.assignStudent(firstStudent, firstId);
        gradebook.assignStudent(firstStudent, secondId);
        gradebook.assignStudent(secondStudent, firstId);
        gradebook.recordMark(firstStudent, firstId, new Mark(BigDecimal.TEN));
        gradebook.recordMark(secondStudent, firstId, new Mark(BigDecimal.ONE));

        gradebook.unassignStudent(firstStudent, firstId);
        assertFalse(gradebook.getMarksForStudent(firstStudent).containsKey(firstId));
        assertTrue(gradebook.getMarksForStudent(firstStudent).containsKey(secondId));
        gradebook.removeAssignment(firstId);
        assertFalse(gradebook.findAssignment(firstId).isPresent());
        assertTrue(gradebook.getMarksForStudent(secondStudent).isEmpty());
        assertTrue(gradebook.findAssignment(secondId).isPresent());
        gradebook.removeStudent(firstStudent);
        assertTrue(gradebook.getMarksForStudent(firstStudent).isEmpty());
        assertTrue(gradebook.findAssignment(secondId).isPresent());
    }

    @Test
    public void moveStudent_preservesMarksAndRejectsCollision() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));
        gradebook.assignStudent(firstStudent, firstId);
        gradebook.recordMark(firstStudent, firstId, new Mark(BigDecimal.TEN));
        gradebook.moveStudent(firstStudent, secondStudent);
        assertTrue(gradebook.getMarksForStudent(firstStudent).isEmpty());
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)), gradebook.getMarksForStudent(secondStudent).get(firstId));

        gradebook.assignStudent(firstStudent, firstId);
        Gradebook original = new Gradebook(gradebook);
        assertThrows(IllegalArgumentException.class, Gradebook.MESSAGE_DUPLICATE_STUDENT, () ->
                gradebook.moveStudent(firstStudent, secondStudent));
        assertEquals(original, gradebook);
    }

    @Test
    public void returnedCollections_cannotModifyGradebook() {
        Gradebook gradebook = new Gradebook();
        gradebook.addAssignment(new Assignment(firstId, "Assignment One", new BigDecimal("20")));
        gradebook.assignStudent(firstStudent, firstId);
        Map<MatriculationNumber, Optional<Mark>> marks = gradebook.getStudentMarks(firstId);

        assertThrows(UnsupportedOperationException.class, () -> marks.clear());
        assertThrows(UnsupportedOperationException.class, () -> gradebook.getAssignments().clear());
        Gradebook copy = new Gradebook(gradebook);
        copy.recordMark(firstStudent, firstId, new Mark(BigDecimal.ONE));
        assertEquals(Optional.empty(), gradebook.getStudentMarks(firstId).get(firstStudent));
    }
}
