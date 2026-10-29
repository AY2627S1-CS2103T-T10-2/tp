package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;

/**
 * Tracks the student shown in the details window across replacements and removals in the full student list.
 */
class StudentDetailsSelection {
    private final ReadOnlyObjectWrapper<Person> person = new ReadOnlyObjectWrapper<>();

    StudentDetailsSelection(ObservableList<Person> persons) {
        requireNonNull(persons).addListener((ListChangeListener<Person>) change -> {
            while (change.next()) {
                if (person.get() == null || !change.getRemoved().contains(person.get())) {
                    continue;
                }
                // A model edit replaces one immutable Person, even when its matriculation number changes.
                if (change.wasReplaced() && change.getRemovedSize() == 1 && change.getAddedSize() == 1) {
                    person.set(change.getAddedSubList().get(0));
                } else {
                    clear();
                }
            }
        });
    }

    ReadOnlyObjectProperty<Person> personProperty() {
        return person.getReadOnlyProperty();
    }

    void select(Person selected) {
        person.set(requireNonNull(selected));
    }

    void clear() {
        person.set(null);
    }
}
