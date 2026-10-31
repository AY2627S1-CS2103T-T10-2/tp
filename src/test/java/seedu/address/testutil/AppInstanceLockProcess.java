package seedu.address.testutil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.core.AppInstanceLock;

/**
 * Holds or probes an application lock in a separate JVM.
 */
public class AppInstanceLockProcess {

    /**
     * Acquires the lock for the first argument's folder and writes the result to the second argument's file.
     * In {@code probe} mode, exits immediately; otherwise holds the lock until terminated by its parent test.
     */
    public static void main(String[] args) throws Exception {
        Optional<AppInstanceLock> acquiredLock = AppInstanceLock.tryAcquire(Path.of(args[0]));
        if (args[2].equals("probe")) {
            if (acquiredLock.isEmpty()) {
                Files.writeString(Path.of(args[1]), "blocked");
                return;
            }
            try (AppInstanceLock instanceLock = acquiredLock.orElseThrow()) {
                Files.writeString(Path.of(args[1]), "acquired");
            }
            return;
        }
        try (AppInstanceLock instanceLock = acquiredLock.orElseThrow()) {
            Files.writeString(Path.of(args[1]), "ready");
            System.in.read();
        }
    }
}
