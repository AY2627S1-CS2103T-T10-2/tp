package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;

/**
 * Represents a student's participation points for a class on a particular date.
 * The owning student is determined by the participation history containing this record.
 */
public final class ParticipationRecord {

    private final ClassCode classCode;
    private final LocalDate date;
    private final ParticipationPoints points;

    /**
     * Creates a participation record with the given class, date and points.
     * All arguments must be non-null.
     */
    public ParticipationRecord(ClassCode classCode, LocalDate date, ParticipationPoints points) {
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
        this.points = requireNonNull(points);
    }

    public ClassCode getClassCode() {
        return classCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public ParticipationPoints getPoints() {
        return points;
    }

    /**
     * Returns whether both records refer to the same class and date.
     * Points are not compared because they can change when a score is corrected.
     */
    public boolean isSameSession(ParticipationRecord otherRecord) {
        return otherRecord != null
                && classCode.equals(otherRecord.classCode)
                && date.equals(otherRecord.date);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ParticipationRecord otherRecord)) {
            return false;
        }

        return isSameSession(otherRecord)
                && points.equals(otherRecord.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classCode, date, points);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classCode", classCode)
                .add("date", date)
                .add("points", points)
                .toString();
    }
}
