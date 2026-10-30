package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Describes an assignment mark sort that can be rebuilt from the current gradebook.
 */
public final class AssignmentSort {
    private final AssignmentId assignmentId;
    private final boolean isDescending;

    /** Creates a sort for one assignment's marks in the requested direction. */
    public AssignmentSort(AssignmentId assignmentId, boolean isDescending) {
        this.assignmentId = requireNonNull(assignmentId);
        this.isDescending = isDescending;
    }

    public AssignmentId getAssignmentId() {
        return assignmentId;
    }

    /** Creates an ordering from the given gradebook, placing students without recorded marks last. */
    public Comparator<Person> createComparator(ReadOnlyGradebook gradebook) {
        requireNonNull(gradebook);
        Map<MatriculationNumber, Optional<Mark>> marks = gradebook.getStudentMarks(assignmentId);
        Comparator<Person> comparator = (first, second) -> {
            Optional<Mark> firstMark = marks.getOrDefault(first.getMatriculationNumber(), Optional.empty());
            Optional<Mark> secondMark = marks.getOrDefault(second.getMatriculationNumber(), Optional.empty());
            if (firstMark.isEmpty() || secondMark.isEmpty()) {
                return Boolean.compare(firstMark.isEmpty(), secondMark.isEmpty());
            }
            int result = firstMark.orElseThrow().getValue().compareTo(secondMark.orElseThrow().getValue());
            return isDescending ? -result : result;
        };
        return comparator.thenComparing(Model.COMPARATOR_NAME_ASC);
    }

    /** Creates card details using the assignment definition and marks in the given gradebook. */
    public SortDisplay createDisplay(ReadOnlyGradebook gradebook) {
        requireNonNull(gradebook);
        Assignment assignment = gradebook.findAssignment(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT));
        Map<MatriculationNumber, Optional<Mark>> marks = gradebook.getStudentMarks(assignmentId);
        String maximum = assignment.getMaxMark().toPlainString();
        return SortDisplay.createForAssignment(this, person -> {
            MatriculationNumber student = person.getMatriculationNumber();
            String value = marks.containsKey(student)
                    ? marks.get(student).map(mark -> mark + "/" + maximum).orElse("Unmarked")
                    : "Not assigned";
            return assignmentId + ": " + value;
        });
    }
}
