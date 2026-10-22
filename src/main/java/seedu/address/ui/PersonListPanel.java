package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.SortDisplay;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private final ObservableValue<SortDisplay> sortDisplay;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given student list and sort display.
     */
    public PersonListPanel(ObservableList<Person> personList, ObservableValue<SortDisplay> sortDisplay) {
        super(FXML);
        this.sortDisplay = requireNonNull(sortDisplay);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        sortDisplay.addListener((observable, previous, current) -> personListView.refresh());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1,
                        sortDisplay.getValue().getDetailFor(person)).getRoot());
            }
        }
    }

}
