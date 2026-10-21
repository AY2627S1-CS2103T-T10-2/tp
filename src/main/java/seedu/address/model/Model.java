package seedu.address.model;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

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

    /** {@code Comparator} that compares persons by name */
    Comparator<Person> COMPARATOR_NAME_ASC = (p1, p2) -> p1.getName().fullName
            .compareToIgnoreCase(p2.getName().fullName);

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

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
