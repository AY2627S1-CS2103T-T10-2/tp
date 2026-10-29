package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Email;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class MainAppTest {
    private static final String VALID_RECORD_JSON =
            "{\"date\":\"07-10-2026\",\"classCode\":\"CS2103T-T10\"}";
    private static final String VALID_STUDENT_JSON = "{\"name\":\"Amy Bee\",\"matriculationNumber\":\"A1234567Z\","
            + "\"email\":\"amy@gmail.com\",\"tags\":[],\"attendance\":[" + VALID_RECORD_JSON + "]}";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void initModelAndLogic_validRecords_restoresStudentsAndAttendance() throws Exception {
        Path dataFilePath = temporaryFolder.resolve("addressbook.json");
        AddressBook savedRecords = new AddressBook();
        savedRecords.addPerson(new PersonBuilder().withAttendance("CS2103T-T10", "07-10-2026").build());
        new JsonAddressBookStorage(dataFilePath).saveAddressBook(savedRecords);

        MainApp app = initializeApp(new JsonAddressBookStorage(dataFilePath));

        assertEquals(savedRecords, app.model.getAddressBook());
        assertTrue(app.logic.getStartupErrorMessage().isEmpty());
        app.logic.execute("list");
        app.stop();
        assertEquals(savedRecords, new JsonAddressBookStorage(dataFilePath).readAddressBook().orElseThrow());
    }

    @Test
    public void initModelAndLogic_missingFile_usesSampleRecordsAndAllowsCommands() throws Exception {
        Path dataFilePath = temporaryFolder.resolve("missing.json");
        MainApp app = initializeApp(new JsonAddressBookStorage(dataFilePath));

        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertTrue(app.logic.getStartupErrorMessage().isEmpty());
        assertFalse(Files.exists(dataFilePath));
        app.logic.execute("list");
        assertTrue(Files.exists(dataFilePath));
    }

    @Test
    public void initModelAndLogic_malformedJson_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{invalid json", "Reason:");
    }

    @Test
    public void initModelAndLogic_consecutiveObjects_blocksCommandsAndPreservesFile() throws Exception {
        String savedRecords = "{\"persons\":[" + VALID_STUDENT_JSON + "]}";
        assertRestoreFailure("{\"persons\":[]}" + savedRecords, "Reason:");
        assertRestoreFailure("{\"persons\":[]} \t\r\n" + savedRecords, "Reason:");
    }

    @Test
    public void initModelAndLogic_trailingContent_blocksCommandsAndPreservesFile() throws Exception {
        String savedRecords = "{\"persons\":[" + VALID_STUDENT_JSON + "]}";
        for (String trailingContent : new String[]{" []", " \"extra\"", " 42", " true", " null", " garbage", " }"}) {
            assertRestoreFailure(savedRecords + trailingContent, "Reason:");
        }
    }

    @Test
    public void initModelAndLogic_trailingWhitespace_restoresRecordsAndAllowsCommands() throws Exception {
        Path dataFilePath = temporaryFolder.resolve("addressbook.json");
        Files.writeString(dataFilePath, "{\"persons\":[" + VALID_STUDENT_JSON + "]} \t\r\n");
        MainApp app = initializeApp(new JsonAddressBookStorage(dataFilePath));
        AddressBook restoredRecords = new AddressBook(app.model.getAddressBook());

        assertTrue(app.logic.getStartupErrorMessage().isEmpty());
        assertEquals(1, restoredRecords.getPersonList().size());
        assertEquals("Amy Bee", restoredRecords.getPersonList().getFirst().getName().fullName);
        app.logic.execute("list");
        app.stop();
        assertEquals(restoredRecords, new JsonAddressBookStorage(dataFilePath).readAddressBook().orElseThrow());
    }

    @Test
    public void initModelAndLogic_invalidStudent_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{\"persons\":[" + VALID_STUDENT_JSON.replace("amy@gmail.com", "invalid") + "]}",
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void initModelAndLogic_invalidAttendance_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{\"persons\":[" + VALID_STUDENT_JSON.replace("07-10-2026", "31-02-2026") + "]}",
                AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void initModelAndLogic_duplicateAttendance_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{\"persons\":["
                + VALID_STUDENT_JSON.replace(VALID_RECORD_JSON, VALID_RECORD_JSON + "," + VALID_RECORD_JSON) + "]}",
                Attendance.MESSAGE_DUPLICATE_RECORD);
    }

    @Test
    public void initModelAndLogic_nullRecords_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("null", "must not contain null");
    }

    @Test
    public void initModelAndLogic_nullStudent_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{\"persons\":[null]}", "Student records must not be null");
    }

    @Test
    public void initModelAndLogic_nullTag_blocksCommandsAndPreservesFile() throws Exception {
        assertRestoreFailure("{\"persons\":[" + VALID_STUDENT_JSON.replace("\"tags\":[]", "\"tags\":[null]") + "]}",
                "Student tags must not be null");
    }

    @Test
    public void initModelAndLogic_unreadableFile_blocksCommandsAndPreservesFile() throws Exception {
        Path dataFilePath = temporaryFolder.resolve("unreadable.json");
        Files.writeString(dataFilePath, "{\"persons\":[" + VALID_STUDENT_JSON + "]}");
        byte[] originalContents = Files.readAllBytes(dataFilePath);
        JsonAddressBookStorage unreadableStorage = new JsonAddressBookStorage(dataFilePath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(new AccessDeniedException(dataFilePath.toString(), null,
                        "Insufficient permissions to read the saved records"));
            }
        };

        MainApp app = initializeApp(unreadableStorage);

        assertStartupError(app, dataFilePath, "Insufficient permissions to read the saved records");
        assertThrows(CommandException.class, () -> app.logic.execute("list"));
        app.stop();
        assertArrayEquals(originalContents, Files.readAllBytes(dataFilePath));
    }

    @Test
    public void initModelAndLogic_fileCorrected_requiresReopeningBeforeCommandsAreAllowed() throws Exception {
        Path dataFilePath = temporaryFolder.resolve("addressbook.json");
        Files.writeString(dataFilePath, "{invalid json");
        MainApp failedApp = initializeApp(new JsonAddressBookStorage(dataFilePath));
        Files.writeString(dataFilePath, "{\"persons\":[" + VALID_STUDENT_JSON + "]}");

        assertThrows(CommandException.class, () -> failedApp.logic.execute("clear"));
        failedApp.stop();
        MainApp reopenedApp = initializeApp(new JsonAddressBookStorage(dataFilePath));

        assertTrue(reopenedApp.logic.getStartupErrorMessage().isEmpty());
        assertEquals(1, reopenedApp.model.getAddressBook().getPersonList().size());
        reopenedApp.logic.execute("list");
    }

    private MainApp initializeApp(JsonAddressBookStorage addressBookStorage) {
        MainApp app = new MainApp();
        app.storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        app.initModelAndLogic(app.storage, new UserPrefs());
        return app;
    }

    private void assertRestoreFailure(String savedData, String expectedReason) throws Exception {
        Path dataFilePath = temporaryFolder.resolve("addressbook.json");
        Files.writeString(dataFilePath, savedData);
        byte[] originalContents = Files.readAllBytes(dataFilePath);

        MainApp app = initializeApp(new JsonAddressBookStorage(dataFilePath));

        assertArrayEquals(originalContents, Files.readAllBytes(dataFilePath));
        assertStartupError(app, dataFilePath, expectedReason);
        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertThrows(CommandException.class, () -> app.logic.execute("list"));
        assertThrows(CommandException.class, () -> app.logic.execute("clear"));
        assertArrayEquals(originalContents, Files.readAllBytes(dataFilePath));
        app.stop();
        assertArrayEquals(originalContents, Files.readAllBytes(dataFilePath));
    }

    private void assertStartupError(MainApp app, Path dataFilePath, String expectedReason) {
        String message = app.logic.getStartupErrorMessage().orElseThrow();
        assertTrue(message.contains(dataFilePath.toAbsolutePath().toString()));
        assertTrue(message.contains(expectedReason));
        assertTrue(message.contains("Commands are disabled"));
        assertTrue(message.contains("Close TAssist"));
        assertTrue(message.contains("Correct the file"));
        assertTrue(message.contains("replace it with a valid backup"));
        assertTrue(message.contains("delete it to start with sample student records"));
        assertTrue(message.contains("Then reopen TAssist"));
    }
}
