package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a non-negative whole number of participation points.
 */
public final class ParticipationPoints {

    public static final String MESSAGE_CONSTRAINTS =
            "Participation points must contain digits only and be between 0 and "
                    + Integer.MAX_VALUE + " (inclusive). Example: p/2.";

    public final int value;

    /**
     * Constructs participation points from a numeric string.
     * Leading zeros are accepted, but signs and whitespace are not.
     *
     * @throws NullPointerException if points is null
     * @throws IllegalArgumentException if points is invalid
     */
    public ParticipationPoints(String points) {
        requireNonNull(points);
        if (!isValidPoints(points)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        value = Integer.parseInt(points);
    }

    /**
     * Returns whether the string represents points in the supported range.
     *
     * @throws NullPointerException if test is null
     */
    public static boolean isValidPoints(String test) {
        requireNonNull(test);
        if (!test.matches("[0-9]+")) {
            return false;
        }
        try {
            Integer.parseInt(test);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ParticipationPoints otherPoints)) {
            return false;
        }
        return value == otherPoints.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
