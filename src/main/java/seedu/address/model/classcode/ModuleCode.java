package seedu.address.model.classcode;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a module code, e.g. CS2103T. Guarantees: immutable; is valid as declared in
 * {@link #isValidModuleCode(String)}
 */
public class ModuleCode {

    public static final String MESSAGE_CONSTRAINTS = "Module codes should be 2 to 3 letters, followed by 4 digits "
            + "and an optional suffix of up to 2 letters (e.g. CS2103T), with no spaces";

    public static final String VALIDATION_REGEX = "[A-Za-z]{2,3}\\d{4}[A-Za-z]{0,2}";

    /** Stored in upper case so that "cs2103t" and "CS2103T" are the same module. */
    public final String value;

    /**
     * Constructs a {@code ModuleCode}.
     *
     * @param moduleCode A valid module code.
     */
    public ModuleCode(String moduleCode) {
        requireNonNull(moduleCode);
        checkArgument(isValidModuleCode(moduleCode), MESSAGE_CONSTRAINTS);
        value = moduleCode.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns true if a given string is a valid module code.
     */
    public static boolean isValidModuleCode(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ModuleCode)) {
            return false;
        }
        return value.equals(((ModuleCode) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
