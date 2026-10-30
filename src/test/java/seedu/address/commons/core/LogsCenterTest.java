package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {

    private static final int LOG_FILE_SIZE_LIMIT = 0;
    private static final int LOG_FILE_COUNT = 2;

    @TempDir
    public Path testFolder;

    @Test
    public void toLogFilePattern_noPercent_returnsSamePath() {
        Path logFilePath = testFolder.resolve("addressbook.log");

        assertEquals(logFilePath.toString(), LogsCenter.toLogFilePattern(logFilePath));
    }

    @Test
    public void toLogFilePattern_percentInFolderName_doublesPercent() {
        Path logFilePath = testFolder.resolve("A b#1%g").resolve("addressbook.log");
        Path escapedLogFilePath = testFolder.resolve("A b#1%%g").resolve("addressbook.log");

        assertEquals(escapedLogFilePath.toString(), LogsCenter.toLogFilePattern(logFilePath));
    }

    @Test
    public void toLogFilePattern_folderNamedLikePattern_createsLogInThatFolder() throws IOException {
        Path logFolder = Files.createDirectory(testFolder.resolve("A b#1%g"));
        Path logFilePath = logFolder.resolve("addressbook.log");
        FileHandler fileHandler = new FileHandler(LogsCenter.toLogFilePattern(logFilePath),
                LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, true);
        fileHandler.close();

        assertTrue(Files.isRegularFile(logFolder.resolve("addressbook.log.0")));
    }
}
