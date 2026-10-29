package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.Person;

/**
 * A reusable window showing a student's particulars, enrolled classes, assignment marks and attendance history.
 */
public class StudentDetailsWindow extends UiPart<Stage> {

    private static final String FXML = "StudentDetailsWindow.fxml";
    private static final String ATTENDANCE_SUMMARY_SINGLE = "1 record";
    private static final String ATTENDANCE_SUMMARY_MULTIPLE = "%d records";
    private static final String ATTENDANCE_EMPTY = "None recorded";
    private static final String ATTENDANCE_SEPARATOR = "  •  ";
    private static final String ATTENDANCE_STATUS = "Present";
    private static final String ATTENDANCE_STYLE_CLASS = "attendance-date";

    private static final Logger logger = LogsCenter.getLogger(StudentDetailsWindow.class);

    private final StudentDetailsSelection selection;
    private final Supplier<ReadOnlyGradebook> gradebookSupplier;
    private long refreshVersion;

    @FXML
    private Label studentName;
    @FXML
    private Label matriculationNumber;
    @FXML
    private Label email;
    @FXML
    private Label tags;
    @FXML
    private GridPane classes;
    @FXML
    private VBox assignments;
    @FXML
    private Label attendanceSummary;
    @FXML
    private VBox attendanceDates;

    /**
     * Creates a details window that reads the latest gradebook when its contents are refreshed.
     * The supplier must return the model's current snapshot, rather than a previously captured snapshot.
     */
    public StudentDetailsWindow(Stage owner, ObservableList<Person> persons,
            Supplier<ReadOnlyGradebook> gradebookSupplier) {
        super(FXML, new Stage());
        this.gradebookSupplier = requireNonNull(gradebookSupplier);
        getRoot().initOwner(owner);
        selection = new StudentDetailsSelection(persons);
        selection.personProperty().addListener((observable, previous, current) -> handleSelectionChange(current));
        configureWindowLifecycle();
    }

    /**
     * Clears the tracked student and invalidates pending work whenever the window is hidden.
     */
    private void configureWindowLifecycle() {
        getRoot().setOnHidden(event -> clearSelection());
        getRoot().getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                getRoot().hide();
                event.consume();
            }
        });
    }

    /**
     * Updates all sections and opens or focuses the window for the selected student.
     */
    public void showStudent(Person person) {
        logger.fine("Showing details for student: " + person.getName());
        selection.select(person);
        cancelPendingRefresh();
        // Selecting the same Person does not fire a property change, so every view must refresh explicitly.
        refreshSelectedStudent();
        getRoot().show();
        getRoot().setIconified(false);
        getRoot().toFront();
        getRoot().requestFocus();
    }

    /**
     * Refreshes an open window after a command has finished updating and saving the model.
     * Does not reopen a closed window or move keyboard focus away from the command box.
     */
    public void refreshIfShowing() {
        if (!getRoot().isShowing()) {
            return;
        }
        cancelPendingRefresh();
        refreshSelectedStudent();
    }

    /**
     * Hides a deleted student's window or schedules a refresh for a replacement student.
     */
    private void handleSelectionChange(Person current) {
        if (current == null) {
            cancelPendingRefresh();
            getRoot().hide();
            return;
        }
        scheduleRefresh();
    }

    /**
     * Waits for the current model operation to finish before reading assignment marks.
     * A matriculation edit publishes its Person replacement before it installs the migrated gradebook.
     */
    private void scheduleRefresh() {
        long requestedVersion = ++refreshVersion;
        Platform.runLater(() -> {
            if (requestedVersion != refreshVersion || !getRoot().isShowing()) {
                return;
            }
            refreshSelectedStudent();
        });
    }

    /**
     * Invalidates queued refreshes after a newer view, edit, deletion or window close.
     */
    private void cancelPendingRefresh() {
        refreshVersion++;
    }

    /**
     * Releases the selection without allowing a queued refresh to reopen the window.
     */
    private void clearSelection() {
        cancelPendingRefresh();
        selection.clear();
    }

    /**
     * Renders the latest selected student and gradebook together without showing or focusing the window.
     */
    private void refreshSelectedStudent() {
        Person person = selection.personProperty().get();
        if (person == null) {
            return;
        }
        ReadOnlyGradebook gradebook = requireNonNull(gradebookSupplier.get());
        updateStudent(person, gradebook);
    }

    /**
     * Refreshes the contents without changing window visibility or keyboard focus.
     */
    private void updateStudent(Person person, ReadOnlyGradebook gradebook) {
        updateParticulars(person);
        updateClasses(person);
        updateAssignments(person, gradebook);
        updateAttendance(person);
        getRoot().setTitle("Student details — " + person.getName());
    }

    /**
     * Updates the student's identifying details and tags.
     */
    private void updateParticulars(Person person) {
        studentName.setText(person.getName().toString());
        matriculationNumber.setText(person.getMatriculationNumber().toString());
        email.setText(person.getEmail().toString());
        String tagText = person.getTags().stream().map(tag -> tag.tagName).sorted()
                .collect(Collectors.joining(", "));
        tags.setText(tagText.isEmpty() ? "No tags" : tagText);
    }

    /**
     * Replaces the enrolled classes in module and class-group order.
     */
    private void updateClasses(Person person) {
        classes.getChildren().clear();
        if (person.getClassCodes().isEmpty()) {
            classes.add(new Label("No classes enrolled"), 0, 0, 2, 1);
        } else {
            Label moduleHeading = new Label("MODULE");
            Label groupHeading = new Label("CLASS GROUP");
            moduleHeading.getStyleClass().add("column-heading");
            groupHeading.getStyleClass().add("column-heading");
            classes.addRow(0, moduleHeading, groupHeading);
            int row = 1;
            for (ClassCode classCode : person.getClassCodes().stream()
                    .sorted(Comparator.comparing(ClassCode::toString)).toList()) {
                classes.addRow(row++, new Label(classCode.getModuleCode().toString()),
                        new Label(classCode.getClassGroup().toString()));
            }
        }
    }

    /**
     * Rebuilds the assignment section from the snapshot used for this refresh.
     */
    private void updateAssignments(Person person, ReadOnlyGradebook gradebook) {
        assignments.getChildren().clear();
        List<String> assignmentRows = formatAssignmentRows(person, gradebook);
        if (assignmentRows.isEmpty()) {
            assignments.getChildren().add(new Label("None assigned"));
        } else {
            for (String row : assignmentRows) {
                Label assignmentLabel = new Label(row);
                assignmentLabel.setWrapText(true);
                assignmentLabel.setMaxWidth(Double.MAX_VALUE);
                assignments.getChildren().add(assignmentLabel);
            }
        }
    }

    /**
     * Returns the selected student's assignments in ID order, preserving the distinction between zero and unmarked.
     */
    static List<String> formatAssignmentRows(Person person, ReadOnlyGradebook gradebook) {
        return gradebook.getMarksForStudent(person.getMatriculationNumber()).entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .map(entry -> {
                    Assignment assignment = gradebook.findAssignment(entry.getKey()).orElseThrow();
                    String result = entry.getValue()
                            .map(mark -> mark + "/" + assignment.getMaxMark().toPlainString())
                            .orElse("Unmarked");
                    return assignment + "\nMark: " + result;
                })
                .toList();
    }

    /**
     * Updates the attendance count and lists each record's date and class.
     */
    private void updateAttendance(Person person) {
        attendanceDates.getChildren().clear();
        int count = person.getAttendance().getRecords().size();
        attendanceSummary.setText(count == 1 ? ATTENDANCE_SUMMARY_SINGLE
                : String.format(ATTENDANCE_SUMMARY_MULTIPLE, count));
        if (count == 0) {
            attendanceDates.getChildren().add(new Label(ATTENDANCE_EMPTY));
            return;
        }

        person.getAttendance().getRecords().forEach(record -> {
            Label recordLabel = new Label(record.getDate() + ATTENDANCE_SEPARATOR + record.getClassCode()
                    + ATTENDANCE_SEPARATOR + ATTENDANCE_STATUS);
            recordLabel.getStyleClass().add(ATTENDANCE_STYLE_CLASS);
            attendanceDates.getChildren().add(recordLabel);
        });
    }
}
