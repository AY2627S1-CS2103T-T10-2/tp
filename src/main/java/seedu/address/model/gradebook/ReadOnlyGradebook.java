package seedu.address.model.gradebook;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import seedu.address.model.person.MatriculationNumber;

/**
 * Read-only operations for assignment definitions and student marks.
 */
public interface ReadOnlyGradebook {
    /**
     * Returns the assignment definition for an ID, if one exists.
     */
    Optional<Assignment> findAssignment(AssignmentId id);

    /**
     * Returns the assignment definitions in insertion order.
     */
    List<Assignment> getAssignments();

    /**
     * Returns students assigned to an assignment and their optional marks.
     */
    Map<MatriculationNumber, Optional<Mark>> getStudentMarks(AssignmentId id);

    /**
     * Returns assignments and optional marks for a student.
     */
    Map<AssignmentId, Optional<Mark>> getMarksForStudent(MatriculationNumber student);
}
