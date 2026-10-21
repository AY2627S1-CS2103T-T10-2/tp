package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns a snapshot of the shared assignments and student marks.
     */
    ReadOnlyGradebook getGradebook();
}
