package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortDisplay;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UndoRedoIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final AddressBookParser parser = new AddressBookParser();

    private Logic createLogic(Model model) {
        return new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_noOpPreservesRedoAndChangedCommandDiscardsRedo() throws Exception {
        ModelManager model = new ModelManager();
        Logic logic = createLogic(model);
        logic.execute("add n/Amy m/A1234567A e/amy@example.com");
        logic.execute("undo");
        logic.execute("clear");
        assertTrue(model.canRedoAddressBook());
        assertFalse(model.canUndoAddressBook());
        logic.execute("redo");
        logic.execute("undo");
        logic.execute("add n/Bob m/A2345678B e/bob@example.com");
        assertFalse(model.canRedoAddressBook());
        assertThrows(CommandException.class, () -> logic.execute("redo"));
        logic.execute("undo");
        assertEquals(new AddressBook(), model.getAddressBook());
        assertFalse(model.canUndoAddressBook());
    }

    @Test
    public void execute_readOnlyAndRejectedCommands_preserveRedo() throws Exception {
        ModelManager model = new ModelManager();
        Logic logic = createLogic(model);
        String addAmy = "add n/Amy m/A1234567A e/amy@example.com";
        logic.execute(addAmy);
        logic.execute("add n/Bob m/A2345678B e/bob@example.com");
        AddressBook bothStudents = new AddressBook(model.getAddressBook());
        logic.execute("undo");
        AddressBook before = new AddressBook(model.getAddressBook());
        logic.execute("list");
        logic.execute("sort name desc");
        logic.execute("find Amy");
        assertThrows(CommandException.class, () -> logic.execute(addAmy));
        assertEquals(before, model.getAddressBook());
        assertTrue(model.canRedoAddressBook());
        logic.execute("redo");
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
        Logic logic = createLogic(model);
        List<String> commands = List.of(
                "add n/Amy m/A1234567A e/amy@example.com",
                "add n/Bob m/A2345678B e/bob@example.com",
                "assignment m/A1234567A id/CS2103T:A1 n/Assignment One max/20",
                "assignment m/A2345678B id/CS2103T:A1",
                "mark m/A1234567A id/CS2103T:A1 mark/17",
                "enroll 1 c/CS2103T-T10",
                "attendance c/CS2103T-T10 d/06-10-2026",
                "attendance m/A1234567A c/CS2103T-T10 d/07-10-2026",
                "edit 1 m/A3456789C",
                "unenroll 1 c/CS2103T-T10",
                "enroll 1 c/CS2103T-T10",
                "unenroll 1 all/",
                "attendance unmark m/A3456789C d/07-10-2026",
                "removeassignment id/CS2103T:A1 m/A2345678B",
                "removeassignment id/CS2103T:A1",
                "delete m/A2345678B",
                "clear",
                "add n/Amy m/A1234567A e/amy@example.com",
                "add n/Bob m/A2345678B e/bob@example.com",
                "delete 1 2");
        for (String text : commands) {
            AddressBook before = new AddressBook(model.getAddressBook());
            logic.execute(text);
            AddressBook after = new AddressBook(model.getAddressBook());
            assertNotEquals(before, after, text);
            for (String historyCommand : List.of("undo", "redo")) {
                model.updateFilteredPersonList(person -> false);
                model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed(), SortDisplay.ATTENDANCE);
                int restoreCount = model.addressBookRestoreCountProperty().getValue().intValue();
                assertFalse(logic.execute(historyCommand).isExit());
                AddressBook expected = historyCommand.equals("undo") ? before : after;
                assertEquals(expected, model.getAddressBook(), text + ": " + historyCommand);
                assertEquals(expected.getPersonList().stream().sorted(Model.COMPARATOR_NAME_ASC).toList(),
                        model.getFilteredPersonList(), text + ": display after " + historyCommand);
                assertEquals(SortDisplay.NONE, model.sortDisplayProperty().getValue());
                assertEquals(restoreCount + 1, model.addressBookRestoreCountProperty().getValue().intValue());
            }
        }
        for (int i = 0; i < commands.size(); i++) {
            logic.execute("undo");
        }
        assertEquals(new AddressBook(), model.getAddressBook());
        assertFalse(model.canUndoAddressBook());
    }
}
