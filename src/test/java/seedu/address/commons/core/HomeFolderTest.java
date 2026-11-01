package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HomeFolderTest {

    @TempDir
    public Path testFolder;

    @Test
    public void resolveHomeFolder_jarFile_returnsParentFolder() throws IOException {
        Path jarFile = Files.createFile(testFolder.resolve("addressbook.jar"));
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));

        assertEquals(testFolder, HomeFolder.resolveHomeFolder(jarFile, workingDirectory));
    }

    @Test
    public void resolveHomeFolder_jarInFolderWithSpaceHashAndPercent_returnsParentFolder() throws IOException {
        Path jarFolder = Files.createDirectory(testFolder.resolve("A b#1%g"));
        Path jarFile = Files.createFile(jarFolder.resolve("addressbook.jar"));

        assertEquals(jarFolder, HomeFolder.resolveHomeFolder(jarFile, testFolder));
    }

    @Test
    public void resolveHomeFolder_renamedJar_returnsParentFolder() throws IOException {
        Path jarFile = Files.createFile(testFolder.resolve("download"));
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));

        assertEquals(testFolder, HomeFolder.resolveHomeFolder(jarFile, workingDirectory));
    }

    @Test
    public void resolveHomeFolder_classesDirectory_returnsWorkingDirectory() throws IOException {
        Path classesDirectory = Files.createDirectory(testFolder.resolve("classes"));

        assertEquals(testFolder, HomeFolder.resolveHomeFolder(classesDirectory, testFolder));
    }

    @Test
    public void resolveHomeFolder_missingLocation_returnsWorkingDirectory() {
        Path missingLocation = testFolder.resolve("missing.jar");

        assertEquals(testFolder, HomeFolder.resolveHomeFolder(missingLocation, testFolder));
    }

    @Test
    public void resolveHomeFolder_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HomeFolder.resolveHomeFolder(null, testFolder));
        assertThrows(NullPointerException.class, () -> HomeFolder.resolveHomeFolder(testFolder, null));
        assertThrows(NullPointerException.class, () -> HomeFolder.resolveHomeFolder(null, null));
    }

    @Test
    public void getHomeFolder_testRun_returnsAbsoluteWorkingDirectory() {
        Path homeFolder = HomeFolder.getHomeFolder();

        assertTrue(homeFolder.isAbsolute());
        assertEquals(Paths.get("").toAbsolutePath(), homeFolder);
    }
}
