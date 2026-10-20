package seedu.address.model.classcode;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a class group (e.g. tutorial group T10) within a module. Guarantees: immutable; is valid as declared in
 * {@link #isValidClassGroup(String)}
 */
public class ClassGroup {

    public static final String MESSAGE_CONSTRAINTS =
            "Class groups should be 1 to 5 letters or digits (e.g. T10), with no spaces or symbols";

    public static final String VALIDATION_REGEX = "[A-Za-z0-9]{1,5}";

    /** Stored in upper case so that "t10" and "T10" are the same group. */
    public final String value;

    /**
     * Constructs a {@code ClassGroup}.
     *
     * @param classGroup A valid class group.
     */
    public ClassGroup(String classGroup) {
        requireNonNull(classGroup);
        checkArgument(isValidClassGroup(classGroup), MESSAGE_CONSTRAINTS);
        value = classGroup.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns true if a given string is a valid class group.
     */
    public static boolean isValidClassGroup(String test) {
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
        if (!(other instanceof ClassGroup)) {
            return false;
        }
        return value.equals(((ClassGroup) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
