package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's matriculation number in the address book. Guarantees:
 * immutable; is valid as declared in
 * {@link #isValidMatriculationNumber(String)}
 */
public class MatriculationNumber {

    public static final String MESSAGE_CONSTRAINTS = "Matriculation numbers should only start with an A, "
        + "followed by 7 digits, and end with a letter";
    public static final String VALIDATION_REGEX = "^[aA]\\d{7}[a-zA-Z]$";
    public final String value;

    /**
     * Constructs a {@code MatriculationNumber}.
     *
     * @param matriculationNumber A valid matriculation number.
     */
    public MatriculationNumber(String matriculationNumber) {
        requireNonNull(matriculationNumber);
        checkArgument(isValidMatriculationNumber(matriculationNumber), MESSAGE_CONSTRAINTS);
        value = matriculationNumber.toUpperCase();
    }

    /**
     * Returns true if a given string is a valid matriculation number.
     */
    public static boolean isValidMatriculationNumber(String test) {
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

        // instanceof handles nulls
        if (!(other instanceof MatriculationNumber otherMatriculationNumber)) {
            return false;
        }

        return value.equals(otherMatriculationNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
