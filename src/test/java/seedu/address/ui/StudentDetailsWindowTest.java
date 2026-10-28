package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the assignment rows displayed in the student details window.
 */
public class StudentDetailsWindowTest {
    private final Person student = new PersonBuilder().build();
    private final Gradebook gradebook = new Gradebook();

    @Test
    public void formatAssignmentRows_noAssignments_returnsEmptyList() {
        assertEquals(List.of(), StudentDetailsWindow.formatAssignmentRows(student, gradebook));
    }

    @Test
    public void formatAssignmentRows_mixedMarks_sortsAndFormatsOnlySelectedStudentAssignments() {
        Assignment unmarked = addAssignment("CS2103T:A2", "Project", "100");
        Assignment marked = addAssignment("CS2100:A1", "Quiz", "20.5");
        Assignment zero = addAssignment("CS2103T:A1", "Homework", "10");
        Assignment other = addAssignment("CS2100:A2", "Other student", "10");
        gradebook.assignStudent(student.getMatriculationNumber(), unmarked.getId());
        gradebook.assignStudent(student.getMatriculationNumber(), marked.getId());
        gradebook.assignStudent(student.getMatriculationNumber(), zero.getId());
        gradebook.recordMark(student.getMatriculationNumber(), marked.getId(), new Mark(new BigDecimal("8.50")));
        gradebook.recordMark(student.getMatriculationNumber(), zero.getId(), new Mark(BigDecimal.ZERO));
        Person otherStudent = new PersonBuilder().withMatriculationNumber("A0123456B").build();
        gradebook.assignStudent(otherStudent.getMatriculationNumber(), other.getId());

        assertEquals(List.of(
                "CS2100:A1 - Quiz (out of 20.5)\nMark: 8.5/20.5",
                "CS2103T:A1 - Homework (out of 10)\nMark: 0/10",
                "CS2103T:A2 - Project (out of 100)\nMark: Unmarked"),
                StudentDetailsWindow.formatAssignmentRows(student, gradebook));
        assertEquals(List.of("CS2100:A2 - Other student (out of 10)\nMark: Unmarked"),
                StudentDetailsWindow.formatAssignmentRows(otherStudent, gradebook));
    }

    @Test
    public void formatAssignmentRows_markUpdatedOrAssignmentRemoved_usesCurrentGradebook() {
        Assignment assignment = addAssignment("CS2103T:A1", "Quiz", "10");
        gradebook.assignStudent(student.getMatriculationNumber(), assignment.getId());
        assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: Unmarked"),
                StudentDetailsWindow.formatAssignmentRows(student, gradebook));
        gradebook.recordMark(student.getMatriculationNumber(), assignment.getId(), new Mark(new BigDecimal("10")));
        assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10"),
                StudentDetailsWindow.formatAssignmentRows(student, gradebook));
        gradebook.unassignStudent(student.getMatriculationNumber(), assignment.getId());
        assertEquals(List.of(), StudentDetailsWindow.formatAssignmentRows(student, gradebook));
    }

    private Assignment addAssignment(String id, String name, String maximum) {
        Assignment assignment = new Assignment(new AssignmentId(id), name, new BigDecimal(maximum));
        gradebook.addAssignment(assignment);
        return assignment;
    }
}
