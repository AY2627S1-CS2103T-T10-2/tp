package seedu.address.model.gradebook;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents an assignment definition shared by the students to whom it is assigned.
 */
public final class Assignment {
    public static final String MESSAGE_NAME_CONSTRAINTS = "Assignment names must not be blank.";
    public static final String MESSAGE_MAX_MARK_CONSTRAINTS = "Maximum marks must be greater than zero.";
    public static final String MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS =
            "Maximum mark's displayed decimal form must be at most "
                    + GradebookNumber.MAX_PLAIN_LENGTH + " characters.";

    private final AssignmentId id;
    private final String name;
    private final BigDecimal maxMark;

    /**
     * Creates an assignment with a module-scoped ID, name, and maximum mark.
     */
    public Assignment(AssignmentId id, String name, BigDecimal maxMark) {
        this.id = requireNonNull(id);
        requireNonNull(name);
        requireNonNull(maxMark);
        String normalizedName = name.strip().replaceAll("\\s+", " ");
        checkArgument(!normalizedName.isEmpty(), MESSAGE_NAME_CONSTRAINTS);
        checkArgument(maxMark.signum() > 0, MESSAGE_MAX_MARK_CONSTRAINTS);
        checkArgument(GradebookNumber.fitsPlainLength(maxMark), MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS);
        this.name = normalizedName;
        this.maxMark = maxMark.stripTrailingZeros();
    }

    public AssignmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMaxMark() {
        return maxMark;
    }

    /**
     * Returns a normalized name for checking duplicates within a module.
     */
    public String getNameKey() {
        return name.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Assignment otherAssignment
                && id.equals(otherAssignment.id)
                && name.equals(otherAssignment.name)
                && maxMark.equals(otherAssignment.maxMark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxMark);
    }

    @Override
    public String toString() {
        return id + " - " + name + " (out of " + maxMark.toPlainString() + ")";
    }
}
