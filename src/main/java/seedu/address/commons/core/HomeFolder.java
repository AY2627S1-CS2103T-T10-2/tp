package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * Represents the home folder that confines app file reads and writes to the JAR's folder and its subfolders.
 * Uses the working directory when running outside a JAR.
 * Must not use {@code LogsCenter}, which needs this class during its own static initialization.
 */
public class HomeFolder {

    /**
     * Returns the JAR's parent folder, or the absolute working directory if no valid JAR location is available.
     */
    public static Path getHomeFolder() {
        Path workingDirectory = Paths.get("").toAbsolutePath();
        CodeSource codeSource = HomeFolder.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            // Logging here would create a circular dependency during LogsCenter's static initialization.
            return workingDirectory;
        }

        try {
            Path codeLocation = Paths.get(codeSource.getLocation().toURI());
            return resolveHomeFolder(codeLocation, workingDirectory);
        } catch (URISyntaxException e) {
            // Logging here would create a circular dependency during LogsCenter's static initialization.
            return workingDirectory;
        }
    }

    /**
     * Returns the code location's parent if it is a regular file, or the working directory otherwise.
     * Checks the file type rather than its suffix so renamed JARs are supported.
     *
     * @throws NullPointerException If either argument is null.
     */
    static Path resolveHomeFolder(Path codeLocation, Path workingDirectory) {
        requireNonNull(codeLocation);
        requireNonNull(workingDirectory);
        if (Files.isRegularFile(codeLocation)) {
            return codeLocation.getParent();
        }
        return workingDirectory;
    }
}
