package seedu.address.model.gradebook;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import seedu.address.model.person.MatriculationNumber;

/**
 * Holds shared assignment definitions and the marks of students assigned to them.
 * A student assignment with no mark is distinct from a recorded zero.
 */
public final class Gradebook {
    public static final String MESSAGE_DUPLICATE_ID = "An assignment with this ID already exists.";
    public static final String MESSAGE_DUPLICATE_NAME = "An assignment with this name already exists in the module.";
    public static final String MESSAGE_UNKNOWN_ASSIGNMENT = "This assignment does not exist.";
    public static final String MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT =
            "This assignment is already assigned to the student.";
    public static final String MESSAGE_UNASSIGNED_STUDENT = "This assignment is not assigned to the student.";
    public static final String MESSAGE_MARK_EXCEEDS_MAX = "The mark exceeds the assignment maximum.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "The new matriculation number already has assignments.";

    private final Map<AssignmentId, Assignment> assignments = new LinkedHashMap<>();
    private final Map<MatriculationNumber, Map<AssignmentId, Optional<Mark>>> studentMarks = new LinkedHashMap<>();

    /**
     * Creates an empty gradebook.
     */
    public Gradebook() {}

    /**
     * Creates an independent copy of a gradebook.
     */
    public Gradebook(Gradebook source) {
        requireNonNull(source);
        assignments.putAll(source.assignments);
        source.studentMarks.forEach((student, marks) -> studentMarks.put(student, new LinkedHashMap<>(marks)));
    }

    /**
     * Adds a shared assignment definition, rejecting a duplicate ID or name in its module.
     */
    public void addAssignment(Assignment assignment) {
        requireNonNull(assignment);
        checkArgument(!assignments.containsKey(assignment.getId()), MESSAGE_DUPLICATE_ID);
        boolean hasSameName = assignments.values().stream()
                .anyMatch(existing -> existing.getId().getModuleCode().equals(assignment.getId().getModuleCode())
                        && existing.getNameKey().equals(assignment.getNameKey()));
        checkArgument(!hasSameName, MESSAGE_DUPLICATE_NAME);
        assignments.put(assignment.getId(), assignment);
    }

    /**
     * Returns the assignment definition for an ID, if one exists.
     */
    public Optional<Assignment> findAssignment(AssignmentId id) {
        return Optional.ofNullable(assignments.get(requireNonNull(id)));
    }

    /**
     * Returns an immutable snapshot of the assignment definitions in insertion order.
     */
    public List<Assignment> getAssignments() {
        return List.copyOf(assignments.values());
    }

    /**
     * Assigns an existing assignment to a student without recording a mark.
     */
    public void assignStudent(MatriculationNumber student, AssignmentId id) {
        requireNonNull(student);
        requireNonNull(id);
        checkArgument(assignments.containsKey(id), MESSAGE_UNKNOWN_ASSIGNMENT);
        Map<AssignmentId, Optional<Mark>> marks = studentMarks.get(student);
        checkArgument(marks == null || !marks.containsKey(id), MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT);
        studentMarks.computeIfAbsent(student, unused -> new LinkedHashMap<>()).put(id, Optional.empty());
    }

    /**
     * Records or replaces a mark for a student assignment and returns its previous mark, if any.
     */
    public Optional<Mark> recordMark(MatriculationNumber student, AssignmentId id, Mark mark) {
        requireNonNull(student);
        requireNonNull(id);
        requireNonNull(mark);
        Map<AssignmentId, Optional<Mark>> marks = studentMarks.get(student);
        checkArgument(marks != null && marks.containsKey(id), MESSAGE_UNASSIGNED_STUDENT);
        Assignment assignment = assignments.get(id);
        checkArgument(assignment != null, MESSAGE_UNKNOWN_ASSIGNMENT);
        checkArgument(mark.getValue().compareTo(assignment.getMaxMark()) <= 0, MESSAGE_MARK_EXCEEDS_MAX);
        return marks.put(id, Optional.of(mark));
    }

    /**
     * Returns an immutable snapshot of students assigned to an assignment and their optional marks.
     */
    public Map<MatriculationNumber, Optional<Mark>> getStudentMarks(AssignmentId id) {
        requireNonNull(id);
        checkArgument(assignments.containsKey(id), MESSAGE_UNKNOWN_ASSIGNMENT);
        Map<MatriculationNumber, Optional<Mark>> marks = new LinkedHashMap<>();
        studentMarks.forEach((student, assignmentsForStudent) -> {
            if (assignmentsForStudent.containsKey(id)) {
                marks.put(student, assignmentsForStudent.get(id));
            }
        });
        return Collections.unmodifiableMap(marks);
    }

    /**
     * Returns an immutable snapshot of assignments and optional marks for a student.
     */
    public Map<AssignmentId, Optional<Mark>> getMarksForStudent(MatriculationNumber student) {
        requireNonNull(student);
        Map<AssignmentId, Optional<Mark>> marks = studentMarks.get(student);
        return marks == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(marks));
    }

    /**
     * Removes one student's assignment and its mark, if recorded.
     */
    public void unassignStudent(MatriculationNumber student, AssignmentId id) {
        requireNonNull(student);
        requireNonNull(id);
        Map<AssignmentId, Optional<Mark>> marks = studentMarks.get(student);
        checkArgument(marks != null && marks.containsKey(id), MESSAGE_UNASSIGNED_STUDENT);
        marks.remove(id);
        if (marks.isEmpty()) {
            studentMarks.remove(student);
        }
    }

    /**
     * Removes an assignment definition and all student marks for it.
     */
    public void removeAssignment(AssignmentId id) {
        requireNonNull(id);
        checkArgument(assignments.containsKey(id), MESSAGE_UNKNOWN_ASSIGNMENT);
        assignments.remove(id);
        for (MatriculationNumber student : new ArrayList<>(studentMarks.keySet())) {
            Map<AssignmentId, Optional<Mark>> marks = studentMarks.get(student);
            marks.remove(id);
            if (marks.isEmpty()) {
                studentMarks.remove(student);
            }
        }
    }

    /**
     * Moves a student's assignments and marks to a new matriculation number.
     */
    public void moveStudent(MatriculationNumber oldStudent, MatriculationNumber newStudent) {
        requireNonNull(oldStudent);
        requireNonNull(newStudent);
        if (oldStudent.equals(newStudent) || !studentMarks.containsKey(oldStudent)) {
            return;
        }
        checkArgument(!studentMarks.containsKey(newStudent), MESSAGE_DUPLICATE_STUDENT);
        studentMarks.put(newStudent, studentMarks.remove(oldStudent));
    }

    /**
     * Removes all assignment entries and marks for a student, leaving shared definitions intact.
     */
    public void removeStudent(MatriculationNumber student) {
        studentMarks.remove(requireNonNull(student));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Gradebook otherGradebook
                && assignments.equals(otherGradebook.assignments)
                && studentMarks.equals(otherGradebook.studentMarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments, studentMarks);
    }
}
