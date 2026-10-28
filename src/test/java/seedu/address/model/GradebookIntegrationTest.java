package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class GradebookIntegrationTest {
    private final AssignmentId assignmentId = new AssignmentId("CS2103T:A1");
    private final Assignment assignment = new Assignment(assignmentId, "Assignment One", new BigDecimal("20"));
    private final Person student = new PersonBuilder().build();

    @Test
    public void setPerson_changesParticulars_preservesAndMovesMarks() {
        AddressBook addressBook = createAddressBookWithMark();
        Person renamedStudent = new PersonBuilder(student).withName("Different Name").build();
        addressBook.setPerson(student, renamedStudent);
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)), addressBook.getGradebook()
                .getMarksForStudent(student.getMatriculationNumber()).get(assignmentId));

        Person renumberedStudent = new PersonBuilder(renamedStudent)
                .withMatriculationNumber("A7654321Y").build();
        addressBook.setPerson(renamedStudent, renumberedStudent);
        assertTrue(addressBook.getGradebook().getMarksForStudent(student.getMatriculationNumber()).isEmpty());
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)), addressBook.getGradebook()
                .getMarksForStudent(renumberedStudent.getMatriculationNumber()).get(assignmentId));
    }

    @Test
    public void removePerson_deletesMarksButKeepsSharedAssignment() {
        AddressBook addressBook = createAddressBookWithMark();
        addressBook.removePerson(student);
        assertTrue(addressBook.getGradebook().getMarksForStudent(student.getMatriculationNumber()).isEmpty());
        assertTrue(addressBook.getGradebook().findAssignment(assignmentId).isPresent());
    }

    @Test
    public void setPerson_duplicateMatriculationNumber_preservesStudentAndMarks() {
        AddressBook addressBook = createAddressBookWithMark();
        Person anotherStudent = new PersonBuilder().withMatriculationNumber("A7654321Y").build();
        addressBook.addPerson(anotherStudent);
        AddressBook original = new AddressBook(addressBook);
        Person invalidEdit = new PersonBuilder(student)
                .withMatriculationNumber("A7654321Y").build();

        assertThrows(DuplicatePersonException.class, () -> addressBook.setPerson(student, invalidEdit));
        assertEquals(original, addressBook);
    }

    @Test
    public void resetData_copiesMarksAndRejectsMissingStudents() {
        AddressBook source = createAddressBookWithMark();
        AddressBook copy = new AddressBook(source);
        assertEquals(source, copy);
        copy.unassignStudent(student.getMatriculationNumber(), assignmentId);
        assertFalse(source.getGradebook().getMarksForStudent(student.getMatriculationNumber()).isEmpty());

        AddressBook target = new AddressBook();
        target.resetData(source);
        assertEquals(source, target);
        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                target.resetData(new ReadOnlyAddressBook() {
                    @Override
                    public ObservableList<Person> getPersonList() {
                        return FXCollections.observableArrayList();
                    }

                    @Override
                    public ReadOnlyGradebook getGradebook() {
                        return source.getGradebook();
                    }
                }));
        assertEquals(source, target);
    }

    @Test
    public void setPersons_dropsOrphanedMarksButKeepsAssignment() {
        AddressBook addressBook = createAddressBookWithMark();
        addressBook.setPersons(List.of());
        assertTrue(addressBook.getGradebook().getMarksForStudent(student.getMatriculationNumber()).isEmpty());
        assertTrue(addressBook.getGradebook().findAssignment(assignmentId).isPresent());
    }

    @Test
    public void getGradebook_mutatingSnapshot_doesNotChangeAddressBook() {
        AddressBook addressBook = createAddressBookWithMark();
        Gradebook snapshot = (Gradebook) addressBook.getGradebook();
        snapshot.unassignStudent(student.getMatriculationNumber(), assignmentId);

        assertEquals(Optional.of(new Mark(BigDecimal.TEN)), addressBook.getGradebook()
                .getMarksForStudent(student.getMatriculationNumber()).get(assignmentId));
    }

    @Test
    public void assignStudent_unknownStudent_doesNotChangeGradebook() {
        AddressBook addressBook = new AddressBook();
        addressBook.addAssignment(assignment);
        MatriculationNumber unknown = student.getMatriculationNumber();
        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                addressBook.assignStudent(unknown, assignmentId));
        assertTrue(addressBook.getGradebook().getStudentMarks(assignmentId).isEmpty());
    }

    @Test
    public void addAssignmentForStudent_createsDefinitionAndEntryTogether() {
        AddressBook addressBook = new AddressBook();
        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                addressBook.addAssignmentForStudent(assignment, student.getMatriculationNumber()));
        assertTrue(addressBook.getGradebook().getAssignments().isEmpty());

        addressBook.addPerson(student);
        addressBook.addAssignmentForStudent(assignment, student.getMatriculationNumber());
        assertEquals(Optional.empty(), addressBook.getGradebook()
                .getMarksForStudent(student.getMatriculationNumber()).get(assignmentId));
    }

    @Test
    public void setAddressBook_emptyData_clearsGradebook() {
        Model model = new ModelManager(createAddressBookWithMark(), new UserPrefs());
        model.setAddressBook(new AddressBook());
        assertTrue(model.getAddressBook().getGradebook().getAssignments().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    private AddressBook createAddressBookWithMark() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(student);
        addressBook.addAssignment(assignment);
        addressBook.assignStudent(student.getMatriculationNumber(), assignmentId);
        addressBook.recordMark(student.getMatriculationNumber(), assignmentId, new Mark(BigDecimal.TEN));
        return addressBook;
    }
}
