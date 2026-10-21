package seedu.address.model.gradebook;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;

/**
 * Represents a non-negative numeric mark for one student assignment.
 */
public final class Mark {
    public static final String MESSAGE_CONSTRAINTS = "Marks must be zero or greater.";

    private final BigDecimal value;

    /**
     * Creates a mark. The gradebook checks it against the assignment maximum.
     */
    public Mark(BigDecimal value) {
        requireNonNull(value);
        checkArgument(value.signum() >= 0, MESSAGE_CONSTRAINTS);
        this.value = value.stripTrailingZeros();
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Mark otherMark && value.equals(otherMark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
