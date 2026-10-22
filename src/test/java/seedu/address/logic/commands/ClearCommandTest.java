package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;

public class ClearCommandTest {

    @Test
    public void execute_withRecordedMarks_removesGradebookData() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        AssignmentId assignmentId = new AssignmentId("CS2103T:A1");
        model.addAssignment(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")));
        var student = model.getFilteredPersonList().get(0);
        model.assignStudent(student.getMatriculationNumber(), assignmentId);
        model.recordMark(student.getMatriculationNumber(), assignmentId, new Mark(BigDecimal.TEN));

        new ClearCommand().execute(model);

        assertTrue(model.getAddressBook().getGradebook().getAssignments().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
