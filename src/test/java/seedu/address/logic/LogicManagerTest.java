package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.MATRICULATION_NUMBER_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AssignmentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.MarksCommand;
import seedu.address.logic.commands.RemoveAssignmentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
        assertTrue(logic.getStartupErrorMessage().isEmpty());
    }

    @Test
    public void execute_assignmentCommand_savesAssignmentAndStudentLink() throws Exception {
        model.addPerson(AMY);
        String command = "assignment m/" + AMY.getMatriculationNumber()
                + " id/CS2103T:A1 n/Assignment One max/20.5";
        AssignmentId id = new AssignmentId("CS2103T:A1");

        assertEquals(String.format(AssignmentCommand.MESSAGE_SUCCESS, id, AMY.getMatriculationNumber()),
                logic.execute(command).getFeedbackToUser());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), restored);
        assertEquals(Optional.empty(), restored.getGradebook()
                .getMarksForStudent(AMY.getMatriculationNumber()).get(id));
    }

    @Test
    public void execute_markCommand_recordsUpdatesAndRestoresMark() throws Exception {
        model.addPerson(AMY);
        String student = AMY.getMatriculationNumber().toString();
        String assignment = " id/CS2103T:A1";
        logic.execute("assignment m/" + student + assignment + " n/Assignment One max/20");

        assertEquals(String.format(MarkCommand.MESSAGE_RECORDED, "0", "20", student, "CS2103T:A1"),
                logic.execute("mark m/" + student + assignment + " mark/0").getFeedbackToUser());
        assertEquals(String.format(MarkCommand.MESSAGE_UPDATED, "17.5", "20", student, "CS2103T:A1"),
                logic.execute("mark m/" + student + assignment + " mark/17.5").getFeedbackToUser());
        assertThrows(CommandException.class, Gradebook.MESSAGE_MARK_EXCEEDS_MAX, () ->
                logic.execute("mark m/" + student + assignment + " mark/21"));

        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), restored);
        assertEquals(Optional.of(new Mark(new BigDecimal("17.5"))), restored.getGradebook()
                .getMarksForStudent(AMY.getMatriculationNumber()).get(new AssignmentId("CS2103T:A1")));
        String expectedMarks = "Assignment: CS2103T:A1 - Assignment One (out of 20)\n"
                + "Student marks:\n" + student + " - " + AMY.getName() + ": 17.5/20";
        assertEquals(expectedMarks, new MarksCommand(new AssignmentId("CS2103T:A1"))
                .execute(new ModelManager(restored, new UserPrefs())).getFeedbackToUser());
    }

    @Test
    public void execute_oversizedGradebookNumbers_rejectsWithoutChangingSavedData() throws Exception {
        model.addPerson(AMY);
        String student = AMY.getMatriculationNumber().toString();
        String assignment = " id/CS2103T:A1";
        assertParseException("assignment m/" + student + assignment + " n/Assignment One max/1e1000000",
                Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS);
        assertTrue(model.getAddressBook().getGradebook().getAssignments().isEmpty());

        logic.execute("assignment m/" + student + assignment + " n/Assignment One max/20");
        assertParseException("mark m/" + student + assignment + " mark/1e1000000",
                Mark.MESSAGE_LENGTH_CONSTRAINTS);
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook()
                .getMarksForStudent(AMY.getMatriculationNumber()).get(new AssignmentId("CS2103T:A1")));

        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_removeAssignmentCommand_savesBothRemovalScopes() throws Exception {
        model.addPerson(AMY);
        Person anotherStudent = new PersonBuilder(AMY).withName("Another Student")
                .withMatriculationNumber("A2345678Y").build();
        model.addPerson(anotherStudent);
        String amyNumber = AMY.getMatriculationNumber().toString();
        String anotherNumber = anotherStudent.getMatriculationNumber().toString();
        AssignmentId id = new AssignmentId("CS2103T:A1");
        logic.execute("assignment m/" + amyNumber + " id/CS2103T:A1 n/Assignment One max/20");
        logic.execute("assignment m/" + anotherNumber + " id/CS2103T:A1");
        logic.execute("mark m/" + anotherNumber + " id/CS2103T:A1 mark/17.5");

        assertEquals(String.format(RemoveAssignmentCommand.MESSAGE_UNASSIGNED_STUDENT, id,
                        AMY.getMatriculationNumber()),
                logic.execute("removeassignment id/CS2103T:A1 m/" + amyNumber).getFeedbackToUser());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), restored);
        assertEquals(2, restored.getPersonList().size());
        assertTrue(restored.getGradebook().getMarksForStudent(AMY.getMatriculationNumber()).isEmpty());
        assertEquals(Optional.of(new Mark(new BigDecimal("17.5"))),
                restored.getGradebook().getMarksForStudent(anotherStudent.getMatriculationNumber()).get(id));
        assertEquals("Assignment: CS2103T:A1 - Assignment One (out of 20)\nStudent marks:\n"
                        + anotherNumber + " - " + anotherStudent.getName() + ": 17.5/20",
                logic.execute("marks id/CS2103T:A1").getFeedbackToUser());

        assertEquals(String.format(RemoveAssignmentCommand.MESSAGE_REMOVED_ASSIGNMENT, id),
                logic.execute("removeassignment id/CS2103T:A1").getFeedbackToUser());
        restored = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), restored);
        assertEquals(2, restored.getPersonList().size());
        assertTrue(restored.getGradebook().getAssignments().isEmpty());
        assertTrue(restored.getGradebook().getMarksForStudent(anotherStudent.getMatriculationNumber()).isEmpty());
        assertCommandException("marks id/CS2103T:A1", Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
    }

    @Test
    public void execute_restoreFailed_blocksAllCommandsWithoutChangingModelOrSaving() {
        String startupError = "Saved records could not be restored. Close TAssist and correct the data file.";
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("data.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                throw new AssertionError("Commands must not save data after a restore failure.");
            }
        };
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        model.addPerson(AMY);
        logic = new LogicManager(model, storage, Optional.of(startupError));

        String[] commands = {"clear", "delete 1", "edit 1 n/Changed", "list", "find Nobody", "help", "exit",
            "attendance m/" + AMY.getMatriculationNumber() + " d/07-10-2026",
            "assignment m/" + AMY.getMatriculationNumber() + " id/CS2103T:A1 n/Assignment One max/20",
            "mark m/" + AMY.getMatriculationNumber() + " id/CS2103T:A1 mark/10",
            "marks id/CS2103T:A1",
            "removeassignment id/CS2103T:A1",
            AddCommand.COMMAND_WORD + NAME_DESC_AMY + MATRICULATION_NUMBER_DESC_AMY + EMAIL_DESC_AMY,
            "invalid command", ""};
        for (String command : commands) {
            assertCommandException(command, startupError);
            assertEquals(model.getAddressBook().getPersonList(), logic.getFilteredPersonList());
        }
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getPersonList_filterAndEdit_retainsFullObservableList() {
        model.addPerson(AMY);
        var persons = logic.getPersonList();
        model.updateFilteredPersonList(person -> false);
        assertEquals(List.of(AMY), persons);
        Person edited = new PersonBuilder(AMY).withName("Updated Amy").build();
        model.setPerson(AMY, edited);
        assertEquals(List.of(edited), persons);
        model.deletePerson(edited);
        assertTrue(persons.isEmpty());
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getPersonList().add(AMY));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + MATRICULATION_NUMBER_DESC_AMY
                + EMAIL_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
