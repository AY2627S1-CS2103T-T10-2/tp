package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
        assertEquals(List.of(), addressBookFromFile.getGradebook().getAssignments());
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_invalidAssignmentDefinitions_throwsIllegalValueException() {
        List<JsonAdaptedAssignment> invalidAssignments = List.of(
                new JsonAdaptedAssignment("bad", "Assignment One", "20"),
                new JsonAdaptedAssignment("CS2103T:A1", " ", "20"),
                new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "0"),
                new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "not a number"));
        for (JsonAdaptedAssignment invalidAssignment : invalidAssignments) {
            JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                    List.of(), List.of(invalidAssignment), List.of());
            assertThrows(IllegalValueException.class, data::toModelType);
        }
    }

    @Test
    public void toModelType_duplicateAssignmentIdOrName_throwsIllegalValueException() {
        JsonAdaptedAssignment first = new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "20");
        JsonAdaptedAssignment sameId = new JsonAdaptedAssignment("CS2103T:A1", "Assignment Two", "20");
        JsonAdaptedAssignment sameName = new JsonAdaptedAssignment("CS2103T:A2", " assignment  one ", "20");
        assertThrows(IllegalValueException.class, Gradebook.MESSAGE_DUPLICATE_ID, () ->
                new JsonSerializableAddressBook(List.of(), List.of(first, sameId), List.of()).toModelType());
        assertThrows(IllegalValueException.class, Gradebook.MESSAGE_DUPLICATE_NAME, () ->
                new JsonSerializableAddressBook(List.of(), List.of(first, sameName), List.of()).toModelType());
    }

    @Test
    public void toModelType_invalidStudentAssignments_throwsIllegalValueException() {
        JsonAdaptedAssignment assignment = new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "20");
        String student = TypicalPersons.ALICE.getMatriculationNumber().toString();
        List<JsonAdaptedStudentAssignment> invalidEntries = List.of(
                new JsonAdaptedStudentAssignment("bad", "CS2103T:A1", "10"),
                new JsonAdaptedStudentAssignment(student, "bad", "10"),
                new JsonAdaptedStudentAssignment(student, "CS2103T:A2", "10"),
                new JsonAdaptedStudentAssignment(student, "CS2103T:A1", "-1"),
                new JsonAdaptedStudentAssignment(student, "CS2103T:A1", "21"),
                new JsonAdaptedStudentAssignment(student, "CS2103T:A1", "invalid"));
        for (JsonAdaptedStudentAssignment invalidEntry : invalidEntries) {
            JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                    List.of(new JsonAdaptedPerson(TypicalPersons.ALICE)), List.of(assignment), List.of(invalidEntry));
            assertThrows(IllegalValueException.class, data::toModelType);
        }
        JsonAdaptedStudentAssignment missingStudent =
                new JsonAdaptedStudentAssignment("A7654321Y", "CS2103T:A1", "10");
        assertThrows(IllegalValueException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                new JsonSerializableAddressBook(List.of(), List.of(assignment), List.of(missingStudent)).toModelType());
    }

    @Test
    public void toModelType_duplicateStudentAssignment_throwsIllegalValueException() {
        String student = TypicalPersons.ALICE.getMatriculationNumber().toString();
        JsonAdaptedStudentAssignment entry = new JsonAdaptedStudentAssignment(student, "CS2103T:A1", "0");
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                List.of(new JsonAdaptedPerson(TypicalPersons.ALICE)),
                List.of(new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "20")),
                List.of(entry, entry));
        assertThrows(IllegalValueException.class, Gradebook.MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT, data::toModelType);
    }

    @Test
    public void toModelType_nullGradebookEntries_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, () ->
                new JsonSerializableAddressBook(List.of(), Collections.singletonList(null), List.of())
                        .toModelType());
        assertThrows(IllegalValueException.class, () ->
                new JsonSerializableAddressBook(List.of(), List.of(),
                        Collections.singletonList(null)).toModelType());
    }

    @Test
    public void toModelType_unmarkedAndZeroMarks_remainDistinct() throws Exception {
        String student = TypicalPersons.ALICE.getMatriculationNumber().toString();
        AssignmentId id = new AssignmentId("CS2103T:A1");
        List<JsonAdaptedAssignment> assignments = List.of(
                new JsonAdaptedAssignment("CS2103T:A1", "Assignment One", "20"),
                new JsonAdaptedAssignment("CS2103T:A2", "Assignment Two", "20"));
        List<JsonAdaptedStudentAssignment> entries = List.of(
                new JsonAdaptedStudentAssignment(student, "CS2103T:A1", null),
                new JsonAdaptedStudentAssignment(student, "CS2103T:A2", "0"));
        AddressBook addressBook = new JsonSerializableAddressBook(
                List.of(new JsonAdaptedPerson(TypicalPersons.ALICE)), assignments, entries).toModelType();
        MatriculationNumber number = TypicalPersons.ALICE.getMatriculationNumber();
        assertEquals(Optional.empty(), addressBook.getGradebook().getMarksForStudent(number).get(id));
        assertEquals(Optional.of(new Mark(BigDecimal.ZERO)),
                addressBook.getGradebook().getMarksForStudent(number).get(new AssignmentId("CS2103T:A2")));
    }

}
