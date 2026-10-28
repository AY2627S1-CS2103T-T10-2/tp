package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

    /**
     * Starts JavaFX once and keeps it running while test windows are opened and closed.
     */
    @BeforeAll
    public static void setUpJavaFx() throws Exception {
        CompletableFuture<Void> initialized = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            initialized.complete(null);
        });
        initialized.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void showStudent_differentStudents_replacesDisplayedAssignmentMarks() throws Exception {
        Person otherStudent = new PersonBuilder().withName("Other Student")
                .withMatriculationNumber("A0123456B").build();
        Person unassignedStudent = new PersonBuilder().withName("Unassigned Student")
                .withMatriculationNumber("A0123457B").build();
        Assignment shared = addAssignment("CS2103T:A1", "Quiz", "10");
        Assignment exclusive = addAssignment("CS2103T:A2", "Project", "100");
        gradebook.assignStudent(student.getMatriculationNumber(), shared.getId());
        gradebook.assignStudent(student.getMatriculationNumber(), exclusive.getId());
        gradebook.assignStudent(otherStudent.getMatriculationNumber(), shared.getId());
        gradebook.recordMark(student.getMatriculationNumber(), shared.getId(), new Mark(new BigDecimal("8.5")));
        gradebook.recordMark(otherStudent.getMatriculationNumber(), shared.getId(), new Mark(new BigDecimal("3")));

        FutureTask<Void> checkWindow = new FutureTask<>(() -> {
            Stage owner = new Stage();
            StudentDetailsWindow window = new StudentDetailsWindow(owner);
            try {
                window.showStudent(student, gradebook);
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10",
                        "CS2103T:A2 - Project (out of 100)\nMark: Unmarked"), displayedAssignmentRows(window));

                window.showStudent(otherStudent, gradebook);
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 3/10"),
                        displayedAssignmentRows(window));

                window.showStudent(unassignedStudent, gradebook);
                assertEquals(List.of("None assigned"), displayedAssignmentRows(window));

                window.showStudent(student, gradebook);
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10",
                        "CS2103T:A2 - Project (out of 100)\nMark: Unmarked"), displayedAssignmentRows(window));
            } finally {
                window.getRoot().close();
                owner.close();
            }
            return null;
        });
        Platform.runLater(checkWindow);
        checkWindow.get(10, TimeUnit.SECONDS);
    }

    private List<String> displayedAssignmentRows(StudentDetailsWindow window) {
        VBox assignments = (VBox) window.getRoot().getScene().lookup("#assignments");
        return assignments.getChildren().stream().map(node -> ((Label) node).getText()).toList();
    }

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
