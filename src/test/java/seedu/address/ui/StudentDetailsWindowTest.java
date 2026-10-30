package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the assignment rows displayed in the student details window.
 */
public class StudentDetailsWindowTest {
    @TempDir
    public Path temporaryFolder;

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
            ObservableList<Person> persons =
                    FXCollections.observableArrayList(student, otherStudent, unassignedStudent);
            StudentDetailsWindow window = new StudentDetailsWindow(owner, persons, () -> new Gradebook(gradebook));
            try {
                window.showStudent(student);
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10",
                        "CS2103T:A2 - Project (out of 100)\nMark: Unmarked"), displayedAssignmentRows(window));

                window.showStudent(otherStudent);
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 3/10"),
                        displayedAssignmentRows(window));

                window.showStudent(unassignedStudent);
                assertEquals(List.of("None assigned"), displayedAssignmentRows(window));

                window.showStudent(student);
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
        return displayedAssignmentRows(window.getRoot());
    }

    private List<String> displayedAssignmentRows(Stage stage) {
        VBox assignments = (VBox) stage.getScene().lookup("#assignments");
        return assignments.getChildren().stream().map(node -> ((Label) node).getText()).toList();
    }

    private Person withParticipationRecords(ParticipationRecord... records) {
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), student.getAttendance(), student.getClassCodes(),
                new ParticipationHistory(List.of(records)));
    }

    private List<String> displayedParticipationRows(Stage stage) {
        VBox participation = (VBox) stage.getScene().lookup("#participationRecords");
        return participation.getChildren().stream()
                .map(node -> ((Label) node).getText())
                .toList();
    }

    @Test
    public void showStudent_withParticipation_displaysRecordsGroupedAndSorted() throws Exception {
        Person studentWithRecords = withParticipationRecords(
                new ParticipationRecord(new ClassCode("CS2103T-T10"),
                        LocalDate.of(2026, 10, 29), new ParticipationPoints("2")),
                new ParticipationRecord(new ClassCode("CS2100-T01"),
                        LocalDate.of(2026, 10, 28), new ParticipationPoints("1")),
                new ParticipationRecord(new ClassCode("CS2103T-T10"),
                        LocalDate.of(2026, 10, 28), new ParticipationPoints("3")));

        FutureTask<Void> checkWindow = new FutureTask<>(() -> {
            Stage owner = new Stage();
            StudentDetailsWindow window = new StudentDetailsWindow(owner,
                    FXCollections.observableArrayList(studentWithRecords), () -> new Gradebook());
            try {
                window.showStudent(studentWithRecords);

                assertEquals(List.of(
                                "CS2100-T01",
                                "28-10-2026  •  1 point",
                                "CS2103T-T10",
                                "28-10-2026  •  3 points",
                                "29-10-2026  •  2 points"),
                        displayedParticipationRows(window.getRoot()));
            } finally {
                window.getRoot().close();
                owner.close();
            }
            return null;
        });

        Platform.runLater(checkWindow);
        checkWindow.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void showStudent_withoutParticipation_displaysEmptyMessage() throws Exception {
        FutureTask<Void> checkWindow = new FutureTask<>(() -> {
            Stage owner = new Stage();
            StudentDetailsWindow window = new StudentDetailsWindow(owner,
                    FXCollections.observableArrayList(student), () -> new Gradebook());
            try {
                window.showStudent(student);

                assertEquals(List.of("None recorded"),
                        displayedParticipationRows(window.getRoot()));
            } finally {
                window.getRoot().close();
                owner.close();
            }
            return null;
        });

        Platform.runLater(checkWindow);
        checkWindow.get(10, TimeUnit.SECONDS);
    }


    @Test
    public void mainWindow_participationCommands_refreshOpenDetailsWithoutAnotherView() throws Exception {
        onFxThread(() -> {
            ModelManager model = new ModelManager();
            Person enrolled = new PersonBuilder(student).withClassCodes("CS2103T-T10").build();
            model.addPerson(enrolled);

            Path dataFile = temporaryFolder.resolve("participation-addressbook.json");
            JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataFile);
            StorageManager storage = new StorageManager(addressBookStorage,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("participation-preferences.json")));
            Stage primaryStage = new Stage();
            MainWindow mainWindow = new MainWindow(primaryStage, new LogicManager(model, storage), dataFile);

            try {
                mainWindow.fillInnerParts();
                mainWindow.show();

                TextField commandField = (TextField) primaryStage.getScene().lookup("#commandTextField");
                String studentPrefix = " m/" + enrolled.getMatriculationNumber();

                enterCommand(commandField, "view 1");

                Stage details = Stage.getWindows().stream()
                        .filter(window -> window instanceof Stage)
                        .map(window -> (Stage) window)
                        .filter(stage -> stage.getOwner() == primaryStage)
                        .filter(stage -> stage.getScene().lookup("#participationRecords") != null)
                        .findFirst().orElseThrow();

                assertEquals(List.of("None recorded"), displayedParticipationRows(details));

                enterCommand(commandField, "participation" + studentPrefix
                        + " c/CS2103T-T10 d/28-10-2026 p/2");

                assertEquals(List.of("CS2103T-T10", "28-10-2026  •  2 points"),
                        displayedParticipationRows(details));

                enterCommand(commandField, "participation" + studentPrefix
                        + " c/CS2103T-T10 d/28-10-2026 p/3");

                assertEquals(List.of("CS2103T-T10", "28-10-2026  •  3 points"),
                        displayedParticipationRows(details));

                enterCommand(commandField, "participation remove" + studentPrefix
                        + " c/CS2103T-T10 d/28-10-2026");

                assertEquals(List.of("None recorded"), displayedParticipationRows(details));

                assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
            } finally {
                Stage.getWindows().stream()
                        .filter(window -> window instanceof Stage)
                        .map(window -> (Stage) window)
                        .filter(stage -> stage.getOwner() == primaryStage)
                        .toList().forEach(Stage::close);
                primaryStage.close();
            }
            return null;
        });
    }


    @Test
    public void mainWindow_assignmentCommands_refreshOpenDetailsWithoutAnotherView() throws Exception {
        onFxThread(() -> {
            ModelManager model = new ModelManager();
            Person enrolled = new PersonBuilder(student).withClassCodes("CS2103T-T10").build();
            model.addPerson(enrolled);
            Path dataFile = temporaryFolder.resolve("addressbook.json");
            JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataFile);
            StorageManager storage = new StorageManager(addressBookStorage,
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Stage primaryStage = new Stage();
            MainWindow mainWindow = new MainWindow(primaryStage, new LogicManager(model, storage), dataFile);
            try {
                mainWindow.fillInnerParts();
                mainWindow.show();
                TextField commandField = (TextField) primaryStage.getScene().lookup("#commandTextField");
                String studentPrefix = " m/" + enrolled.getMatriculationNumber();
                enterCommand(commandField, "assignment" + studentPrefix + " id/CS2103T:A1 n/Quiz max/10");
                enterCommand(commandField, "mark" + studentPrefix + " id/CS2103T:A1 mark/8.5");
                enterCommand(commandField, "view 1");
                Stage details = Stage.getWindows().stream().filter(window -> window instanceof Stage)
                        .map(window -> (Stage) window).filter(stage -> stage.getOwner() == primaryStage)
                        .filter(stage -> stage.getScene().lookup("#assignments") != null).findFirst().orElseThrow();
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), displayedAssignmentRows(details));

                enterCommand(commandField, "mark" + studentPrefix + " id/CS2103T:A1 mark/10");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10"), displayedAssignmentRows(details));
                assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());

                enterCommand(commandField, "assignment" + studentPrefix + " id/CS2103T:A2 n/Homework max/20");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10",
                        "CS2103T:A2 - Homework (out of 20)\nMark: Unmarked"), displayedAssignmentRows(details));
                enterCommand(commandField, "removeassignment" + studentPrefix + " id/CS2103T:A2");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10"), displayedAssignmentRows(details));

                details.close();
                enterCommand(commandField, "mark" + studentPrefix + " id/CS2103T:A1 mark/0");
                assertFalse(details.isShowing());
                enterCommand(commandField, "view 1");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 0/10"), displayedAssignmentRows(details));
                assertTrue(details.isShowing());
            } finally {
                Stage.getWindows().stream().filter(window -> window instanceof Stage)
                        .map(window -> (Stage) window).filter(stage -> stage.getOwner() == primaryStage)
                        .toList().forEach(Stage::close);
                primaryStage.close();
            }
            return null;
        });
    }

    private void enterCommand(TextField commandField, String text) {
        commandField.setText(text);
        commandField.fireEvent(new ActionEvent());
        assertFalse(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS), text);
        assertEquals("", commandField.getText());
    }

    @Test
    public void showStudent_sameStudent_refreshesMarkAndAssignmentChanges() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("find m/" + student.getMatriculationNumber());
                fixture.execute("view 1");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());

                fixture.execute("mark m/" + student.getMatriculationNumber() + " id/CS2103T:A1 mark/10");
                fixture.execute("view 1");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10"), fixture.rows());

                Assignment project = new Assignment(new AssignmentId("CS2103T:A2"), "Project", BigDecimal.TEN);
                fixture.model.addAssignmentForStudent(project, student.getMatriculationNumber());
                fixture.execute("view 1");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 10/10",
                        "CS2103T:A2 - Project (out of 10)\nMark: Unmarked"), fixture.rows());

                fixture.model.removeAssignment(fixture.quiz.getId());
                fixture.execute("view 1");
                assertEquals(List.of("CS2103T:A2 - Project (out of 10)\nMark: Unmarked"), fixture.rows());
                fixture.model.unassignStudent(student.getMatriculationNumber(), project.getId());
                AddressBook beforeRefresh = new AddressBook(fixture.model.getAddressBook());
                fixture.execute("view 1");
                assertEquals(List.of("None assigned"), fixture.rows());
                assertEquals(beforeRefresh, fixture.model.getAddressBook());
                return null;
            });
        }
    }

    @Test
    public void editMatriculationNumber_refreshesAfterMigrationWithoutChangingData() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            AddressBook afterEdit = onFxThread(() -> {
                fixture.execute("find m/" + student.getMatriculationNumber());
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                return new AddressBook(fixture.model.getAddressBook());
            });
            // A second task runs after the queued selection refresh, without another view command.
            onFxThread(() -> {
                assertEquals("A9999999Z", fixture.labelText("#matriculationNumber"));
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                assertTrue(fixture.window.getRoot().isShowing());
                assertEquals(afterEdit, fixture.model.getAddressBook());
                assertTrue(afterEdit.getGradebook().getMarksForStudent(student.getMatriculationNumber()).isEmpty());
                fixture.execute("view 1");
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                return null;
            });
        }
    }

    @Test
    public void editStudentTwiceBeforeRefresh_displaysLatestDetailsAndMarks() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                fixture.execute("edit 1 m/A9999998Z n/Updated Student e/updated@example.com t/updated");
                return null;
            });
            onFxThread(() -> {
                assertEquals("A9999998Z", fixture.labelText("#matriculationNumber"));
                assertEquals("Updated Student", fixture.labelText("#studentName"));
                assertEquals("updated@example.com", fixture.labelText("#email"));
                assertEquals("updated", fixture.labelText("#tags"));
                assertEquals("Student details — Updated Student", fixture.window.getRoot().getTitle());
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                return null;
            });
        }
    }

    @Test
    public void switchStudentWhileRefreshPending_keepsNewStudentVisible() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                Person otherStudent = new PersonBuilder().withName("Other Student")
                        .withMatriculationNumber("A0123456B").build();
                fixture.model.addPerson(otherStudent);
                fixture.model.assignStudent(otherStudent.getMatriculationNumber(), fixture.quiz.getId());
                fixture.model.recordMark(otherStudent.getMatriculationNumber(), fixture.quiz.getId(),
                        new Mark(BigDecimal.ZERO));
                fixture.window.showStudent(otherStudent);
                return null;
            });
            onFxThread(() -> {
                assertEquals("Other Student", fixture.labelText("#studentName"));
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 0/10"), fixture.rows());
                assertTrue(fixture.window.getRoot().isShowing());
                return null;
            });
        }
    }

    @Test
    public void closeWhileRefreshPending_staysClosedAndCanViewAgain() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                fixture.window.getRoot().close();
                fixture.execute("edit 1 n/Edited While Closed");
                return null;
            });
            onFxThread(() -> {
                assertFalse(fixture.window.getRoot().isShowing());
                fixture.execute("view 1");
                assertEquals("Edited While Closed", fixture.labelText("#studentName"));
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                assertTrue(fixture.window.getRoot().isShowing());
                return null;
            });
        }
    }

    @Test
    public void deleteWhileRefreshPending_staysClosedAfterStudentIsAddedAgain() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                fixture.execute("delete 1");
                fixture.model.addPerson(student);
                return null;
            });
            onFxThread(() -> {
                assertFalse(fixture.window.getRoot().isShowing());
                assertEquals(List.of(fixture.quiz), fixture.model.getAddressBook().getGradebook().getAssignments());
                fixture.execute("view 1");
                assertEquals(List.of("None assigned"), fixture.rows());
                return null;
            });
        }
    }

    @Test
    public void undoAndRedoWhileRefreshPending_closeWindowAndViewRestoredMarks() throws Exception {
        try (WindowFixture fixture = onFxThread(WindowFixture::new)) {
            onFxThread(() -> {
                fixture.execute("view 1");
                fixture.execute("edit 1 m/A9999999Z");
                fixture.execute("undo");
                return null;
            });
            onFxThread(() -> {
                assertFalse(fixture.window.getRoot().isShowing());
                fixture.execute("view 1");
                assertEquals(student.getMatriculationNumber().toString(), fixture.labelText("#matriculationNumber"));
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                fixture.execute("redo");
                return null;
            });
            onFxThread(() -> {
                assertFalse(fixture.window.getRoot().isShowing());
                fixture.execute("view 1");
                assertEquals("A9999999Z", fixture.labelText("#matriculationNumber"));
                assertEquals(List.of("CS2103T:A1 - Quiz (out of 10)\nMark: 8.5/10"), fixture.rows());
                return null;
            });
        }
    }

    private <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
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

    /**
     * Exercises real commands and model snapshots with the same view and restoration wiring as MainWindow.
     */
    private class WindowFixture implements AutoCloseable {
        private final ModelManager model = new ModelManager();
        private final Stage owner = new Stage();
        private final Assignment quiz = new Assignment(new AssignmentId("CS2103T:A1"), "Quiz", BigDecimal.TEN);
        private final StudentDetailsWindow window;

        WindowFixture() {
            model.addPerson(student);
            model.addAssignmentForStudent(quiz, student.getMatriculationNumber());
            model.recordMark(student.getMatriculationNumber(), quiz.getId(), new Mark(new BigDecimal("8.5")));
            model.commitAddressBook();
            window = new StudentDetailsWindow(owner, model.getAddressBook().getPersonList(),
                    model.getAddressBook()::getGradebook);
            model.addressBookRestoreCountProperty().addListener((observable, previous, current) -> {
                window.getRoot().hide();
            });
        }

        private void execute(String text) throws CommandException, ParseException {
            CommandResult result = new AddressBookParser().parseCommand(text).execute(model);
            model.commitAddressBook();
            result.getPersonToView().ifPresent(window::showStudent);
        }

        private List<String> rows() {
            return displayedAssignmentRows(window);
        }

        private String labelText(String selector) {
            return ((Label) window.getRoot().getScene().lookup(selector)).getText();
        }

        @Override
        public void close() throws Exception {
            onFxThread(() -> {
                window.getRoot().close();
                owner.close();
                return null;
            });
        }
    }
}
