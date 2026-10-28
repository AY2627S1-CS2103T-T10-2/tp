package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortDisplay;

public class RedoCommandTest {
    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RedoCommand().execute(null));
    }

    @Test
    public void execute_unavailableHistory_leavesDataAndDisplayUnchanged() {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.updateFilteredPersonList(person -> false);
        model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed(), SortDisplay.ATTENDANCE);
        assertThrows(CommandException.class, RedoCommand.MESSAGE_FAILURE, () -> new RedoCommand().execute(model));
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(0, model.addressBookRestoreCountProperty().getValue().intValue());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(SortDisplay.ATTENDANCE, model.sortDisplayProperty().getValue());
    }

    @Test
    public void execute_availableHistory_returnsSuccessWithoutExiting() throws Exception {
        ModelManager model = new ModelManager();
        new AddCommand(ALICE).execute(model);
        model.undoAddressBook();

        assertEquals(new CommandResult(RedoCommand.MESSAGE_SUCCESS), new RedoCommand().execute(model));
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
    }
}
