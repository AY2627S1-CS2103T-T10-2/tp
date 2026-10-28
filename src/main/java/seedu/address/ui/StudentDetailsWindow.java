package seedu.address.ui;

import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Person;

/**
 * A reusable window showing a student's particulars, enrolled classes and attendance history.
 */
public class StudentDetailsWindow extends UiPart<Stage> {

    private static final String FXML = "StudentDetailsWindow.fxml";

    private static final Logger logger = LogsCenter.getLogger(StudentDetailsWindow.class);

    private final StudentDetailsSelection selection;

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
    private Label attendanceSummary;
    @FXML
    private VBox attendanceDates;

    /**
     * Creates a details window owned by the main application window.
     */
    public StudentDetailsWindow(Stage owner, ObservableList<Person> persons) {
        super(FXML, new Stage());
        getRoot().initOwner(owner);
        selection = new StudentDetailsSelection(persons);
        selection.personProperty().addListener((observable, previous, current) -> {
            if (current == null) {
                getRoot().hide();
            } else {
                updateStudent(current);
            }
        });
        getRoot().setOnHidden(event -> selection.clear());
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
        getRoot().show();
        getRoot().setIconified(false);
        getRoot().toFront();
        getRoot().requestFocus();
    }

    /**
     * Refreshes the contents without changing window visibility or keyboard focus.
     */
    private void updateStudent(Person person) {
        studentName.setText(person.getName().toString());
        matriculationNumber.setText(person.getMatriculationNumber().toString());
        email.setText(person.getEmail().toString());
        String tagText = person.getTags().stream().map(tag -> tag.tagName).sorted()
                .collect(Collectors.joining(", "));
        tags.setText(tagText.isEmpty() ? "No tags" : tagText);

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

        attendanceDates.getChildren().clear();
        int count = person.getAttendance().getDates().size();
        attendanceSummary.setText(count == 1 ? "1 recorded date" : count + " recorded dates");
        if (count == 0) {
            attendanceDates.getChildren().add(new Label("None recorded"));
        } else {
            person.getAttendance().getDates().forEach(date -> {
                Label dateLabel = new Label(date + "  •  Present");
                dateLabel.getStyleClass().add("attendance-date");
                attendanceDates.getChildren().add(dateLabel);
            });
        }

        getRoot().setTitle("Student details — " + person.getName());
    }
}
