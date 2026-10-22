package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedAssignment> assignments = new ArrayList<>();
    private final List<JsonAdaptedStudentAssignment> studentAssignments = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    public JsonSerializableAddressBook(List<JsonAdaptedPerson> persons) {
        this(persons, null, null);
    }

    /**
     * Constructs an adapted address book, treating missing gradebook fields as empty for older files.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments,
            @JsonProperty("studentAssignments") List<JsonAdaptedStudentAssignment> studentAssignments) {
        if (persons == null) {
            throw new IllegalArgumentException("Student records must not be null.");
        }
        this.persons.addAll(persons);
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
        if (studentAssignments != null) {
            this.studentAssignments.addAll(studentAssignments);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        ReadOnlyGradebook gradebook = source.getGradebook();
        for (Assignment assignment : gradebook.getAssignments()) {
            assignments.add(new JsonAdaptedAssignment(assignment));
            gradebook.getStudentMarks(assignment.getId()).forEach((student, mark) ->
                    studentAssignments.add(new JsonAdaptedStudentAssignment(student, assignment.getId(), mark)));
        }
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException("Student records must not be null.");
            }
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        for (JsonAdaptedAssignment jsonAdaptedAssignment : assignments) {
            if (jsonAdaptedAssignment == null) {
                throw new IllegalValueException("Assignment definitions must not be null.");
            }
            try {
                addressBook.addAssignment(jsonAdaptedAssignment.toModelType());
            } catch (IllegalArgumentException ex) {
                throw new IllegalValueException(ex.getMessage());
            }
        }
        for (JsonAdaptedStudentAssignment jsonAdaptedStudentAssignment : studentAssignments) {
            if (jsonAdaptedStudentAssignment == null) {
                throw new IllegalValueException("Student assignments must not be null.");
            }
            jsonAdaptedStudentAssignment.addTo(addressBook);
        }
        return addressBook;
    }

}
