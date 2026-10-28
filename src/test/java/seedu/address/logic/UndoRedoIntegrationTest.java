package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortDisplay;

public class UndoRedoIntegrationTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_readOnlyAndRejectedCommands_preserveRedo() throws Exception {
        ModelManager model = new ModelManager();
        String addAmy = "add n/Amy m/A1234567A e/amy@example.com";
        parser.parseCommand(addAmy).execute(model);
        parser.parseCommand("add n/Bob m/A2345678B e/bob@example.com").execute(model);
        AddressBook bothStudents = new AddressBook(model.getAddressBook());
        parser.parseCommand("undo").execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());
        parser.parseCommand("list").execute(model);
        parser.parseCommand("sort name desc").execute(model);
        parser.parseCommand("find Amy").execute(model);
        assertThrows(CommandException.class, () -> parser.parseCommand(addAmy).execute(model));
        assertEquals(before, model.getAddressBook());
        assertTrue(model.canRedoAddressBook());
        parser.parseCommand("redo").execute(model);
        assertEquals(bothStudents, model.getAddressBook());
    }

    @Test
    public void parseCommand_historyCommands_returnsCorrectTypes() throws Exception {
        assertTrue(parser.parseCommand("undo") instanceof UndoCommand);
        assertTrue(parser.parseCommand("redo") instanceof RedoCommand);
        assertTrue(parser.parseCommand("undo ignored") instanceof UndoCommand);
        assertTrue(parser.parseCommand("redo ignored") instanceof RedoCommand);
    }

    @Test
    public void execute_mutatingCommands_supportUndoRedoAndResetDisplay() throws Exception {
        ModelManager model = new ModelManager();
        List<String> commands = List.of(
                "add n/Amy m/A1234567A e/amy@example.com",
                "add n/Bob m/A2345678B e/bob@example.com",
                "assignment m/A1234567A id/CS2103T:A1 n/Assignment One max/20",
                "assignment m/A2345678B id/CS2103T:A1",
                "mark m/A1234567A id/CS2103T:A1 mark/17",
                "enroll 1 c/CS2103T-T10",
                "attendance m/A1234567A d/07-10-2026",
                "edit 1 m/A3456789C",
                "unenroll 1 c/CS2103T-T10",
                "enroll 1 c/CS2103T-T10",
                "unenroll 1 all/",
                "attendance unmark m/A3456789C d/07-10-2026",
                "removeassignment id/CS2103T:A1 m/A2345678B",
                "removeassignment id/CS2103T:A1",
                "delete m/A2345678B",
                "clear");
        for (String text : commands) {
            AddressBook before = new AddressBook(model.getAddressBook());
            parser.parseCommand(text).execute(model);
            AddressBook after = new AddressBook(model.getAddressBook());
            assertNotEquals(before, after, text);
            for (String historyCommand : List.of("undo", "redo")) {
                model.updateFilteredPersonList(person -> false);
                model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed(), SortDisplay.ATTENDANCE);
                int restoreCount = model.addressBookRestoreCountProperty().getValue().intValue();
                assertFalse(parser.parseCommand(historyCommand).execute(model).isExit());
                AddressBook expected = historyCommand.equals("undo") ? before : after;
                assertEquals(expected, model.getAddressBook(), text + ": " + historyCommand);
                assertEquals(expected.getPersonList().stream().sorted(Model.COMPARATOR_NAME_ASC).toList(),
                        model.getFilteredPersonList(), text + ": display after " + historyCommand);
                assertEquals(SortDisplay.NONE, model.sortDisplayProperty().getValue());
                assertEquals(restoreCount + 1, model.addressBookRestoreCountProperty().getValue().intValue());
            }
        }
        for (int i = 0; i < commands.size(); i++) {
            parser.parseCommand("undo").execute(model);
        }
        assertEquals(new AddressBook(), model.getAddressBook());
        assertFalse(model.canUndoAddressBook());
    }
}
