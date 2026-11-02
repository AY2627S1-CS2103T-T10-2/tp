package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;

import seedu.address.model.classcode.ClassCode;

/**
 * Represents a student's attendance at one class session, identified by class and date.
 * Instances are immutable.
 */
public final class AttendanceRecord implements Comparable<AttendanceRecord> {
    private static final Comparator<AttendanceRecord> CHRONOLOGICAL_ORDER =
            Comparator.comparing(AttendanceRecord::getDate)
                    .thenComparing(record -> record.getClassCode().toString());

    private final AttendanceDate date;
    private final ClassCode classCode;

    /**
     * Constructs an attendance record for the given date and class.
     */
    public AttendanceRecord(AttendanceDate date, ClassCode classCode) {
        this.date = requireNonNull(date);
        this.classCode = requireNonNull(classCode);
    }

    public AttendanceDate getDate() {
        return date;
    }

    public ClassCode getClassCode() {
        return classCode;
    }

    /**
     * Compares records by date ascending, then by class code text ascending.
     */
    @Override
    public int compareTo(AttendanceRecord other) {
        return CHRONOLOGICAL_ORDER.compare(this, other);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof AttendanceRecord otherRecord
                && date.equals(otherRecord.date) && classCode.equals(otherRecord.classCode));
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, classCode);
    }

    @Override
    public String toString() {
        return date + " " + classCode;
    }
}
