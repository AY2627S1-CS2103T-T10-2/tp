package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final ReadOnlyObjectWrapper<SortDisplay> sortDisplay = new ReadOnlyObjectWrapper<>(SortDisplay.NONE);
    private final ReadOnlyIntegerWrapper addressBookRestoreCount =
            new ReadOnlyIntegerWrapper(0);

    private Predicate<Person> filterPredicate = PREDICATE_SHOW_ALL_PERSONS;
    private Predicate<Person> findPredicate = PREDICATE_SHOW_ALL_PERSONS;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.versionedAddressBook = new VersionedAddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredPersons = new FilteredList<>(this.versionedAddressBook.getPersonList());
        this.sortedPersons = new SortedList<>(this.filteredPersons);
        this.sortedPersons.setComparator(COMPARATOR_NAME_ASC);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.versionedAddressBook.resetData(addressBook);
        resetAssignmentSort();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedAddressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        versionedAddressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        versionedAddressBook.addPerson(person);
        resetFilteredPersonList();
        sortPersons(COMPARATOR_NAME_ASC);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        versionedAddressBook.setPerson(target, editedPerson);
        resetAssignmentSort();
    }

    @Override
    public void addAssignment(Assignment assignment) {
        versionedAddressBook.addAssignment(assignment);
        resetAssignmentSort();
    }

    @Override
    public void addAssignmentForStudent(Assignment assignment, MatriculationNumber student) {
        versionedAddressBook.addAssignmentForStudent(assignment, student);
        resetAssignmentSort();
    }

    @Override
    public void assignStudent(MatriculationNumber student, AssignmentId assignmentId) {
        versionedAddressBook.assignStudent(student, assignmentId);
        resetAssignmentSort();
    }

    @Override
    public Optional<Mark> recordMark(MatriculationNumber student, AssignmentId assignmentId, Mark mark) {
        Optional<Mark> previous = versionedAddressBook.recordMark(student, assignmentId, mark);
        resetAssignmentSort();
        return previous;
    }

    @Override
    public void unassignStudent(MatriculationNumber student, AssignmentId assignmentId) {
        versionedAddressBook.unassignStudent(student, assignmentId);
        resetAssignmentSort();
    }

    @Override
    public void removeAssignment(AssignmentId assignmentId) {
        versionedAddressBook.removeAssignment(assignmentId);
        resetAssignmentSort();
    }

    private void resetAssignmentSort() {
        if (sortDisplay.get().isAssignmentSort()) {
            sortPersons(COMPARATOR_NAME_ASC);
        }
    }

    @Override
    public void sortPersons(Comparator<Person> comparator) {
        sortPersons(comparator, SortDisplay.NONE);
    }

    @Override
    public void sortPersons(Comparator<Person> comparator, SortDisplay sortDisplay) {
        this.sortDisplay.set(requireNonNull(sortDisplay));
        sortedPersons.setComparator(comparator);
    }

    @Override
    public ObservableValue<SortDisplay> sortDisplayProperty() {
        return sortDisplay.getReadOnlyProperty();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filterPredicate = predicate;
        findPredicate = PREDICATE_SHOW_ALL_PERSONS;
        applyPredicates();
    }

    @Override
    public void resetFilteredPersonList() {
        filterPredicate = PREDICATE_SHOW_ALL_PERSONS;
        findPredicate = PREDICATE_SHOW_ALL_PERSONS;
        applyPredicates();
    }

    @Override
    public void updateFilterPredicate(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filterPredicate = predicate;
        applyPredicates();
    }

    @Override
    public void updateFindPredicate(Predicate<Person> predicate) {
        requireNonNull(predicate);
        findPredicate = predicate;
        applyPredicates();
    }

    private void applyPredicates() {
        filteredPersons.setPredicate(filterPredicate.and(findPredicate));
    }

    //=========== AddressBook History ========================================================================

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        resetDisplayAfterRestoration();
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        resetDisplayAfterRestoration();
    }

    private void resetDisplayAfterRestoration() {
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        sortPersons(COMPARATOR_NAME_ASC);

        addressBookRestoreCount.set(addressBookRestoreCount.get() + 1);
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
    }

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public ObservableValue<Number> addressBookRestoreCountProperty() {
        return addressBookRestoreCount.getReadOnlyProperty();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return versionedAddressBook.equals(otherModelManager.versionedAddressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }
}
