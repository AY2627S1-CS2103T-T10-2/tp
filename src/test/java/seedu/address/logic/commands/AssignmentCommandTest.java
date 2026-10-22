package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.person.MatriculationNumber;

public class AssignmentCommandTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
    private final MatriculationNumber student = ALICE.getMatriculationNumber();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_newAssignment_addsDefinitionAndUnmarkedStudentEntry() {
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.addAssignmentForStudent(assignment, student);
        assertCommandSuccess(new AssignmentCommand(student, assignment), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, student), expected);
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
    }

    @Test
    public void execute_existingAssignment_reusesDefinitionForAnotherStudent() {
        model.addAssignmentForStudent(assignment, student);
        MatriculationNumber otherStudent = BENSON.getMatriculationNumber();
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.assignStudent(otherStudent, id);
        assertCommandSuccess(new AssignmentCommand(otherStudent, id), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, otherStudent), expected);
        assertEquals(1, model.getAddressBook().getGradebook().getAssignments().size());
    }

    @Test
    public void execute_duplicateNameOrId_rejectsWithoutChangingModel() {
        model.addAssignmentForStudent(assignment, student);
        MatriculationNumber otherStudent = BENSON.getMatriculationNumber();
        Assignment sameName = new Assignment(new AssignmentId("CS2103T:A2"),
                " assignment   one ", new BigDecimal("25"));
        assertCommandFailure(new AssignmentCommand(otherStudent, sameName), model,
                Gradebook.MESSAGE_DUPLICATE_NAME);
        Assignment sameId = new Assignment(id, "Another Name", new BigDecimal("25"));
        assertCommandFailure(new AssignmentCommand(otherStudent, sameId), model,
                Gradebook.MESSAGE_DUPLICATE_ID);
    }

    @Test
    public void execute_alreadyAssigned_rejectsWithoutChangingModel() {
        model.addAssignmentForStudent(assignment, student);
        assertCommandFailure(new AssignmentCommand(student, id), model,
                Gradebook.MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT);
    }

    @Test
    public void execute_unknownStudentOrAssignment_rejectsWithoutChangingModel() {
        MatriculationNumber unknownStudent = new MatriculationNumber("A9999999Z");
        assertCommandFailure(new AssignmentCommand(unknownStudent, assignment), model,
                AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        assertCommandFailure(new AssignmentCommand(student, id), model,
                Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
    }

    @Test
    public void execute_studentOutsideFilter_canBeAssigned() {
        model.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        assertFalse(model.getFilteredPersonList().contains(ALICE));
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        expected.addAssignmentForStudent(assignment, student);
        assertCommandSuccess(new AssignmentCommand(student, assignment), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, student), expected);
    }

    @Test
    public void equals() {
        AssignmentCommand newAssignment = new AssignmentCommand(student, assignment);
        assertTrue(newAssignment.equals(new AssignmentCommand(student, assignment)));
        assertFalse(newAssignment.equals(new AssignmentCommand(student, id)));
        assertFalse(newAssignment.equals(new AssignmentCommand(BENSON.getMatriculationNumber(), assignment)));
        assertFalse(newAssignment.equals(null));
    }
}
