package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {
    public static final String MESSAGE_STUDENT_NOT_FOUND = "This student does not exist.";

    private final UniquePersonList persons = new UniquePersonList();
    private Gradebook gradebook = new Gradebook();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons. Marks for removed students are discarded.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        Set<MatriculationNumber> retainedStudents = new HashSet<>();
        persons.forEach(person -> retainedStudents.add(person.getMatriculationNumber()));
        for (Assignment assignment : gradebook.getAssignments()) {
            gradebook.getStudentMarks(assignment.getId()).keySet().stream()
                    .filter(student -> !retainedStudents.contains(student))
                    .forEach(gradebook::removeStudent);
        }
    }

    /**
     * Resets the students and gradebook with {@code newData}, rejecting dangling student marks.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        List<Person> newPersons = List.copyOf(newData.getPersonList());
        Gradebook newGradebook = new Gradebook(newData.getGradebook());
        Set<MatriculationNumber> studentIds = new HashSet<>();
        newPersons.forEach(person -> studentIds.add(person.getMatriculationNumber()));
        for (Assignment assignment : newGradebook.getAssignments()) {
            for (MatriculationNumber student : newGradebook.getStudentMarks(assignment.getId()).keySet()) {
                if (!studentIds.contains(student)) {
                    throw new IllegalArgumentException(MESSAGE_STUDENT_NOT_FOUND);
                }
            }
        }
        persons.setPersons(newPersons);
        gradebook = newGradebook;
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(target);
        requireNonNull(editedPerson);
        MatriculationNumber oldNumber = target.getMatriculationNumber();
        MatriculationNumber newNumber = editedPerson.getMatriculationNumber();
        if (oldNumber.equals(newNumber)) {
            persons.setPerson(target, editedPerson);
            return;
        }
        Gradebook updatedGradebook = new Gradebook(gradebook);
        updatedGradebook.moveStudent(oldNumber, newNumber);
        persons.setPerson(target, editedPerson);
        gradebook = updatedGradebook;
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        gradebook.removeStudent(key.getMatriculationNumber());
    }

    /**
     * Adds an assignment definition shared by its assigned students.
     */
    public void addAssignment(Assignment assignment) {
        gradebook.addAssignment(assignment);
    }

    /**
     * Adds a new assignment and assigns it to an existing student as one change.
     */
    public void addAssignmentForStudent(Assignment assignment, MatriculationNumber student) {
        requireNonNull(assignment);
        requireNonNull(student);
        if (!hasStudent(student)) {
            throw new IllegalArgumentException(MESSAGE_STUDENT_NOT_FOUND);
        }
        Gradebook updatedGradebook = new Gradebook(gradebook);
        updatedGradebook.addAssignment(assignment);
        updatedGradebook.assignStudent(student, assignment.getId());
        gradebook = updatedGradebook;
    }

    /**
     * Assigns existing work to a student in this address book.
     */
    public void assignStudent(MatriculationNumber student, AssignmentId assignmentId) {
        requireNonNull(student);
        requireNonNull(assignmentId);
        if (!hasStudent(student)) {
            throw new IllegalArgumentException(MESSAGE_STUDENT_NOT_FOUND);
        }
        gradebook.assignStudent(student, assignmentId);
    }

    /**
     * Records or replaces a student's mark and returns the previous mark, if any.
     */
    public Optional<Mark> recordMark(MatriculationNumber student, AssignmentId assignmentId, Mark mark) {
        return gradebook.recordMark(student, assignmentId, mark);
    }

    /**
     * Removes one student's assignment and its mark.
     */
    public void unassignStudent(MatriculationNumber student, AssignmentId assignmentId) {
        gradebook.unassignStudent(student, assignmentId);
    }

    /**
     * Removes an assignment definition and all of its student marks.
     */
    public void removeAssignment(AssignmentId assignmentId) {
        gradebook.removeAssignment(assignmentId);
    }

    private boolean hasStudent(MatriculationNumber student) {
        return getPersonList().stream()
                .anyMatch(person -> person.getMatriculationNumber().equals(student));
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("gradebook", gradebook)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ReadOnlyGradebook getGradebook() {
        return new Gradebook(gradebook);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons) && gradebook.equals(otherAddressBook.gradebook);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, gradebook);
    }
}
