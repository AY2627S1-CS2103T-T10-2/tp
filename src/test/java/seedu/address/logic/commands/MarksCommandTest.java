package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MarksCommandTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_assignmentWithoutStudents_showsEmptyMessage() {
        model.addAssignment(assignment);
        String expected = "Assignment: CS2103T:A1 - Assignment One (out of 20)\n"
                + "Student marks:\nNone assigned.";
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new MarksCommand(id), model, expected, expectedModel);
    }

    @Test
    public void execute_mixedMarks_showsSortedRowsAndUnmarkedStatus() {
        model.addAssignment(assignment);
        model.assignStudent(CARL.getMatriculationNumber(), id);
        model.assignStudent(BENSON.getMatriculationNumber(), id);
        model.assignStudent(ALICE.getMatriculationNumber(), id);
        model.recordMark(CARL.getMatriculationNumber(), id, new Mark(new BigDecimal("17.5")));
        model.recordMark(ALICE.getMatriculationNumber(), id, new Mark(BigDecimal.ZERO));
        model.updateFilteredPersonList(person -> false);

        String expected = "Assignment: CS2103T:A1 - Assignment One (out of 20)\n"
                + "Student marks:\n"
                + ALICE.getMatriculationNumber() + " - " + ALICE.getName() + ": 0/20\n"
                + BENSON.getMatriculationNumber() + " - " + BENSON.getName() + ": Unmarked\n"
                + CARL.getMatriculationNumber() + " - " + CARL.getName() + ": 17.5/20";
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new MarksCommand(id), model, expected, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_editedAndDeletedStudent_reflectsCurrentStudents() {
        model.addAssignmentForStudent(assignment, ALICE.getMatriculationNumber());
        model.assignStudent(BENSON.getMatriculationNumber(), id);
        Person edited = new PersonBuilder(ALICE).withName("New Name")
                .withMatriculationNumber("A7654321Y").build();
        model.setPerson(ALICE, edited);
        model.deletePerson(BENSON);

        String expected = "Assignment: CS2103T:A1 - Assignment One (out of 20)\n"
                + "Student marks:\nA7654321Y - New Name: Unmarked";
        assertCommandSuccess(new MarksCommand(id), model, expected,
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_unknownAssignment_rejectsWithoutChangingModel() {
        assertCommandFailure(new MarksCommand(id), model, Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
    }

    @Test
    public void equals() {
        MarksCommand command = new MarksCommand(id);
        assertTrue(command.equals(new MarksCommand(new AssignmentId("cs2103t:a1"))));
        assertFalse(command.equals(new MarksCommand(new AssignmentId("CS2103T:A2"))));
        assertFalse(command.equals(null));
    }
}
