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

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

public class RemoveAssignmentCommandTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final AssignmentId otherId = new AssignmentId("CS2103T:A2");
    private final Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
    private final Assignment otherAssignment = new Assignment(otherId, "Assignment Two", new BigDecimal("10"));
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_oneStudent_preservesDefinitionOtherMarksAndPeople() {
        model.addAssignmentForStudent(assignment, ALICE.getMatriculationNumber());
        model.assignStudent(BENSON.getMatriculationNumber(), id);
        model.recordMark(ALICE.getMatriculationNumber(), id, new Mark(new BigDecimal("17")));
        model.recordMark(BENSON.getMatriculationNumber(), id, new Mark(new BigDecimal("18")));
        model.updateFilteredPersonList(person -> false);

        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.unassignStudent(ALICE.getMatriculationNumber(), id);
        expected.updateFilteredPersonList(person -> false);
        String message = String.format(RemoveAssignmentCommand.MESSAGE_UNASSIGNED_STUDENT,
                id, ALICE.getMatriculationNumber());
        assertCommandSuccess(new RemoveAssignmentCommand(id, ALICE.getMatriculationNumber()),
                model, message, expected);
        assertEquals(7, model.getAddressBook().getPersonList().size());
        assertTrue(model.getAddressBook().getGradebook().findAssignment(id).isPresent());
        assertFalse(model.getAddressBook().getGradebook().getMarksForStudent(ALICE.getMatriculationNumber())
                .containsKey(id));
        assertEquals(new Mark(new BigDecimal("18")), model.getAddressBook().getGradebook()
                .getMarksForStudent(BENSON.getMatriculationNumber()).get(id).orElseThrow());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_sharedAssignment_cascadesOnlyThatAssignmentAndCanRecreateIt() {
        model.addAssignmentForStudent(assignment, ALICE.getMatriculationNumber());
        model.assignStudent(BENSON.getMatriculationNumber(), id);
        model.recordMark(ALICE.getMatriculationNumber(), id, new Mark(new BigDecimal("17")));
        model.addAssignmentForStudent(otherAssignment, ALICE.getMatriculationNumber());
        model.recordMark(ALICE.getMatriculationNumber(), otherId, new Mark(new BigDecimal("9")));

        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.removeAssignment(id);
        assertCommandSuccess(new RemoveAssignmentCommand(id), model,
                String.format(RemoveAssignmentCommand.MESSAGE_REMOVED_ASSIGNMENT, id), expected);
        assertEquals(7, model.getAddressBook().getPersonList().size());
        assertTrue(model.getAddressBook().getGradebook().findAssignment(id).isEmpty());
        assertFalse(model.getAddressBook().getGradebook().getMarksForStudent(ALICE.getMatriculationNumber())
                .containsKey(id));
        assertFalse(model.getAddressBook().getGradebook().getMarksForStudent(BENSON.getMatriculationNumber())
                .containsKey(id));
        assertEquals(new Mark(new BigDecimal("9")), model.getAddressBook().getGradebook()
                .getMarksForStudent(ALICE.getMatriculationNumber()).get(otherId).orElseThrow());
        model.addAssignmentForStudent(assignment, BENSON.getMatriculationNumber());
        assertTrue(model.getAddressBook().getGradebook().findAssignment(id).isPresent());
    }

    @Test
    public void execute_invalidTargets_doNotChangeModel() {
        model.addAssignmentForStudent(assignment, ALICE.getMatriculationNumber());
        assertCommandFailure(new RemoveAssignmentCommand(otherId), model, Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
        assertCommandFailure(new RemoveAssignmentCommand(id, new MatriculationNumber("A9999999X")),
                model, AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        assertCommandFailure(new RemoveAssignmentCommand(id, BENSON.getMatriculationNumber()),
                model, Gradebook.MESSAGE_UNASSIGNED_STUDENT);
    }

    @Test
    public void equals() {
        assertEquals(new RemoveAssignmentCommand(id), new RemoveAssignmentCommand(new AssignmentId("cs2103t:a1")));
        assertEquals(new RemoveAssignmentCommand(id, ALICE.getMatriculationNumber()),
                new RemoveAssignmentCommand(new AssignmentId("cs2103t:a1"), ALICE.getMatriculationNumber()));
        assertFalse(new RemoveAssignmentCommand(id).equals(new RemoveAssignmentCommand(id,
                ALICE.getMatriculationNumber())));
    }
}
