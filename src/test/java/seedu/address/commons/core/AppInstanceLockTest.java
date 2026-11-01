package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.testutil.AppInstanceLockProcess;

public class AppInstanceLockTest {

    @TempDir
    public Path testFolder;

    @Test
    public void tryAcquire_sameFolderAlreadyLocked_returnsEmpty() throws IOException {
        try (AppInstanceLock firstLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
            assertNotNull(firstLock);
            assertTrue(AppInstanceLock.tryAcquire(testFolder).isEmpty());
        }
    }

    @Test
    public void tryAcquire_sameJvmEquivalentFolder_preservesNativeLock() throws Exception {
        Path nestedFolder = Files.createDirectory(testFolder.resolve("nested"));
        try (AppInstanceLock firstLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
            assertNotNull(firstLock);
            assertTrue(AppInstanceLock.tryAcquire(nestedFolder.resolve("..")).isEmpty());
            assertOtherProcessBlocked();
        }
    }

    @Test
    public void close_lockReleased_allowsReopeningWithExistingLockFile() throws IOException {
        AppInstanceLock firstLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow();
        firstLock.close();
        firstLock.close();

        assertTrue(Files.exists(testFolder.resolve(".tassist.lock")));
        try (AppInstanceLock reopenedLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
            assertNotNull(reopenedLock);
        }
    }

    @Test
    public void close_previousOwnerClosedAgain_preservesReplacementLock() throws Exception {
        AppInstanceLock previousLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow();
        previousLock.close();

        try (AppInstanceLock replacementLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
            assertNotNull(replacementLock);
            previousLock.close();
            assertTrue(AppInstanceLock.tryAcquire(testFolder).isEmpty());
            assertOtherProcessBlocked();
        }
    }

    @Test
    public void tryAcquire_existingUnlockedFile_succeedsWithoutChangingContents() throws IOException {
        Path lockFile = testFolder.resolve(".tassist.lock");
        Files.writeString(lockFile, "left over from a previous run");

        try (AppInstanceLock instanceLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
            assertNotNull(instanceLock);
        }

        assertEquals("left over from a previous run", Files.readString(lockFile));
    }

    @Test
    public void tryAcquire_differentFolders_allowsBothInstances() throws IOException {
        Path otherFolder = Files.createDirectory(testFolder.resolve("other"));
        try (AppInstanceLock firstLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow();
                AppInstanceLock otherLock = AppInstanceLock.tryAcquire(otherFolder).orElseThrow()) {
            assertNotNull(firstLock);
            assertNotNull(otherLock);
        }
    }

    @Test
    public void tryAcquire_lockPathIsDirectory_throwsIoException() throws IOException {
        Files.createDirectory(testFolder.resolve(".tassist.lock"));

        assertThrows(IOException.class, () -> AppInstanceLock.tryAcquire(testFolder));
    }

    @Test
    public void tryAcquire_otherProcessOwnsLock_rejectsUntilProcessTerminates() throws Exception {
        Path readyFile = testFolder.resolve("owner-ready");
        Path outputFile = testFolder.resolve("owner-output.txt");
        Process owner = startLockProcess(readyFile, outputFile, "hold");

        try {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
            while (!Files.exists(readyFile) && owner.isAlive() && System.nanoTime() < deadline) {
                Thread.sleep(25);
            }
            assertTrue(Files.exists(readyFile), "Lock owner did not become ready: " + Files.readString(outputFile));
            assertTrue(AppInstanceLock.tryAcquire(testFolder).isEmpty());

            owner.destroyForcibly();
            assertTrue(owner.waitFor(10, TimeUnit.SECONDS), "Lock owner did not terminate");

            try (AppInstanceLock recoveredLock = AppInstanceLock.tryAcquire(testFolder).orElseThrow()) {
                assertNotNull(recoveredLock);
            }
        } finally {
            owner.destroyForcibly();
            owner.waitFor(10, TimeUnit.SECONDS);
        }
    }

    private void assertOtherProcessBlocked() throws Exception {
        Path resultFile = Files.createTempFile(testFolder, "probe-result-", ".txt");
        Path outputFile = Files.createTempFile(testFolder, "probe-output-", ".txt");
        Process probe = startLockProcess(resultFile, outputFile, "probe");
        try {
            assertTrue(probe.waitFor(10, TimeUnit.SECONDS), "Lock probe did not terminate");
            assertEquals(0, probe.exitValue(), Files.readString(outputFile));
            assertEquals("blocked", Files.readString(resultFile));
        } finally {
            probe.destroyForcibly();
            probe.waitFor(10, TimeUnit.SECONDS);
        }
    }

    private Process startLockProcess(Path resultFile, Path outputFile, String mode) throws Exception {
        String javaName = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", javaName);
        String classPath = Path.of(AppInstanceLockProcess.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI()) + File.pathSeparator
                + Path.of(AppInstanceLock.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return new ProcessBuilder(javaExecutable.toString(), "-cp", classPath,
                AppInstanceLockProcess.class.getName(), testFolder.toString(), resultFile.toString(), mode)
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile())
                .start();
    }
}
