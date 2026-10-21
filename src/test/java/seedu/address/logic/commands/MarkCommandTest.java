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
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

public class MarkCommandTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
    private final MatriculationNumber student = ALICE.getMatriculationNumber();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unmarkedAssignment_recordsFractionalMark() {
        model.addAssignmentForStudent(assignment, student);
        Mark mark = new Mark(new BigDecimal("17.5"));
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.recordMark(student, id, mark);
        assertCommandSuccess(new MarkCommand(student, id, mark), model,
                String.format(MarkCommand.MESSAGE_RECORDED, mark, "20", student, id), expected);
        assertEquals(Optional.of(mark), model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
    }

    @Test
    public void execute_existingZeroMark_updatesMark() {
        model.addAssignmentForStudent(assignment, student);
        model.recordMark(student, id, new Mark(BigDecimal.ZERO));
        Mark updated = new Mark(new BigDecimal("20"));
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.recordMark(student, id, updated);
        assertCommandSuccess(new MarkCommand(student, id, updated), model,
                String.format(MarkCommand.MESSAGE_UPDATED, updated, "20", student, id), expected);
    }

    @Test
    public void execute_missingStudentOrAssignment_rejectsWithoutChangingModel() {
        Mark mark = new Mark(BigDecimal.TEN);
        MatriculationNumber unknown = new MatriculationNumber("A9999999Z");
        assertCommandFailure(new MarkCommand(unknown, id, mark), model, AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        assertCommandFailure(new MarkCommand(student, id, mark), model, Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
    }

    @Test
    public void execute_unassignedStudent_rejectsWithoutChangingModel() {
        model.addAssignment(assignment);
        assertCommandFailure(new MarkCommand(student, id, new Mark(BigDecimal.TEN)), model,
                Gradebook.MESSAGE_UNASSIGNED_STUDENT);
    }

    @Test
    public void execute_aboveMaximum_rejectsAndPreservesPreviousMark() {
        model.addAssignmentForStudent(assignment, student);
        model.recordMark(student, id, new Mark(BigDecimal.TEN));
        assertCommandFailure(new MarkCommand(student, id, new Mark(new BigDecimal("20.01"))), model,
                Gradebook.MESSAGE_MARK_EXCEEDS_MAX);
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)),
                model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
    }

    @Test
    public void execute_studentOutsideFilter_canBeMarked() {
        model.addAssignmentForStudent(assignment, student);
        model.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        assertFalse(model.getFilteredPersonList().contains(ALICE));
        Mark mark = new Mark(BigDecimal.TEN);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        expected.recordMark(student, id, mark);
        assertCommandSuccess(new MarkCommand(student, id, mark), model,
                String.format(MarkCommand.MESSAGE_RECORDED, mark, "20", student, id), expected);
    }

    @Test
    public void equals() {
        MarkCommand command = new MarkCommand(student, id, new Mark(BigDecimal.TEN));
        assertTrue(command.equals(new MarkCommand(student, id, new Mark(new BigDecimal("10.0")))));
        assertFalse(command.equals(new MarkCommand(BENSON.getMatriculationNumber(), id, new Mark(BigDecimal.TEN))));
        assertFalse(command.equals(new MarkCommand(student, new AssignmentId("CS2103T:A2"),
                new Mark(BigDecimal.TEN))));
        assertFalse(command.equals(new MarkCommand(student, id, new Mark(BigDecimal.ONE))));
        assertFalse(command.equals(null));
    }
}
