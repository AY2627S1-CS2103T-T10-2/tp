package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Prevents multiple application instances from accessing the same home folder.
 * The operating system releases the lock when its channel closes or the process terminates.
 */
public class AppInstanceLock implements AutoCloseable {
    private static final String LOCK_FILE_NAME = ".tassist.lock";
    private static final Map<Path, AppInstanceLock> activeLocks = new HashMap<>();

    private final Path homeFolder;
    private final FileChannel channel;

    private AppInstanceLock(Path homeFolder, FileChannel channel) {
        this.homeFolder = homeFolder;
        this.channel = channel;
    }

    /**
     * Tries to acquire exclusive access to {@code homeFolder} without waiting.
     * Returns an empty optional if another instance already holds the lock.
     *
     * @throws IOException If the lock file cannot be opened or locked.
     */
    public static synchronized Optional<AppInstanceLock> tryAcquire(Path homeFolder) throws IOException {
        requireNonNull(homeFolder);
        Path canonicalHomeFolder = homeFolder.toRealPath();
        // Closing a second channel can release this JVM's existing lock on some operating systems.
        if (activeLocks.containsKey(canonicalHomeFolder)) {
            return Optional.empty();
        }
        FileChannel channel = FileChannel.open(canonicalHomeFolder.resolve(LOCK_FILE_NAME),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        boolean isAcquired = false;
        try {
            isAcquired = channel.tryLock() != null;
            if (!isAcquired) {
                return Optional.empty();
            }
            AppInstanceLock instanceLock = new AppInstanceLock(canonicalHomeFolder, channel);
            activeLocks.put(canonicalHomeFolder, instanceLock);
            return Optional.of(instanceLock);
        } catch (OverlappingFileLockException e) {
            // Another instance in this JVM already holds the lock.
            return Optional.empty();
        } finally {
            if (!isAcquired) {
                channel.close();
            }
        }
    }

    /**
     * Releases the lock. Keeps the file so concurrent launches always lock the same file.
     */
    @Override
    public void close() throws IOException {
        synchronized (AppInstanceLock.class) {
            channel.close();
            activeLocks.remove(homeFolder, this);
        }
    }
}
