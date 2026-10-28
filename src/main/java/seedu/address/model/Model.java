package seedu.address.model;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Comparator} that compares persons by name, then matriculation number. */
    Comparator<Person> COMPARATOR_NAME_ASC = Comparator
            .comparing((Person person) -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(person -> person.getMatriculationNumber().value);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Adds a shared assignment definition.
     */
    void addAssignment(Assignment assignment);

    /**
     * Adds a new assignment and assigns it to an existing student as one change.
     */
    void addAssignmentForStudent(Assignment assignment, MatriculationNumber student);

    /**
     * Assigns existing work to a student.
     */
    void assignStudent(MatriculationNumber student, AssignmentId assignmentId);

    /**
     * Records or replaces a student's mark and returns the previous mark, if any.
     */
    Optional<Mark> recordMark(MatriculationNumber student, AssignmentId assignmentId, Mark mark);

    /**
     * Removes one student's assignment and its mark.
     */
    void unassignStudent(MatriculationNumber student, AssignmentId assignmentId);

    /**
     * Removes an assignment definition and all of its student marks.
     */
    void removeAssignment(AssignmentId assignmentId);

    /**
     * Sorts the displayed list of persons.
     */
    void sortPersons(Comparator<Person> comparator);

    /** Sorts the displayed persons and selects an extra detail to show on each card. */
    void sortPersons(Comparator<Person> comparator, SortDisplay sortDisplay);

    /** Returns the display detail selected by the active sort. */
    ObservableValue<SortDisplay> sortDisplayProperty();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Resets the filtered person list to show all persons.
     */
    void resetFilteredPersonList();

    /**
     * Updates the find predicate of the filtered person list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFindPredicate(Predicate<Person> predicate);

    /**
     * Updates the filter predicate of the filtered person list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilterPredicate(Predicate<Person> predicate);

    /**
     * Saves changed address book data for undo and clears any redo history.
     * Does nothing if the data matches the current saved state.
     */
    void commitAddressBook();

    /**
     * Reverses the most recent change to the address book.
     * On success, clears the person list filter and resets sorting to {@link #COMPARATOR_NAME_ASC}
     * with no extra sort detail displayed.
     *
     * @throws IllegalStateException if undo is unavailable.
     */
    void undoAddressBook();

    /**
     * Reapplies the most recently undone change.
     * On success, clears the person list filter and resets sorting to {@link #COMPARATOR_NAME_ASC}
     * with no extra sort detail displayed.
     *
     * @throws IllegalStateException if redo is unavailable.
     */
    void redoAddressBook();

    /**
     * Returns true if there is a change to undo.
     */
    boolean canUndoAddressBook();

    /**
     * Returns true if there is a change to redo.
     */
    boolean canRedoAddressBook();

    /**
     * Returns an observable counter that increases after each successful undo or redo.
     */
    ObservableValue<Number> addressBookRestoreCountProperty();
}
