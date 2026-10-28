package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Represents an immutable history of class sessions a student was marked present at.
 * A missing record indicates unrecorded attendance, not absence.
 */
public final class Attendance {
    public static final String MESSAGE_DUPLICATE_RECORD =
            "Attendance for this class on this date has already been recorded.";
    public static final String MESSAGE_RECORD_NOT_FOUND =
            "Attendance for this class on this date has not been recorded.";

    private static final Comparator<AttendanceRecord> NEWEST_FIRST =
            Comparator.comparing(AttendanceRecord::getDate).reversed()
                    .thenComparing(record -> record.getClassCode().toString());

    private final NavigableSet<AttendanceRecord> records;

    /**
     * Constructs a history from unique, non-null records, making a defensive copy.
     */
    public Attendance(Collection<AttendanceRecord> records) {
        requireNonNull(records);
        NavigableSet<AttendanceRecord> copiedRecords = new TreeSet<>();
        for (AttendanceRecord record : records) {
            requireNonNull(record);
            checkArgument(copiedRecords.add(record), MESSAGE_DUPLICATE_RECORD);
        }
        this.records = Collections.unmodifiableNavigableSet(copiedRecords);
    }

    /**
     * Returns an empty attendance history.
     */
    public static Attendance empty() {
        return new Attendance(List.of());
    }

    /**
     * Returns whether attendance has been recorded for the given record.
     */
    public boolean contains(AttendanceRecord record) {
        return records.contains(requireNonNull(record));
    }

    /**
     * Returns a new history with the record added, rejecting an already recorded record.
     */
    public Attendance withRecord(AttendanceRecord record) {
        requireNonNull(record);
        checkArgument(!contains(record), MESSAGE_DUPLICATE_RECORD);
        NavigableSet<AttendanceRecord> updatedRecords = new TreeSet<>(records);
        updatedRecords.add(record);
        return new Attendance(updatedRecords);
    }

    /**
     * Returns a new history with the record removed, rejecting a record that is not recorded.
     */
    public Attendance withoutRecord(AttendanceRecord record) {
        requireNonNull(record);
        checkArgument(contains(record), MESSAGE_RECORD_NOT_FOUND);
        NavigableSet<AttendanceRecord> updatedRecords = new TreeSet<>(records);
        updatedRecords.remove(record);
        return new Attendance(updatedRecords);
    }

    /**
     * Returns an immutable list of records ordered by newest date first, then class code ascending.
     */
    public List<AttendanceRecord> getRecords() {
        return records.stream().sorted(NEWEST_FIRST).toList();
    }

    /**
     * Returns an immutable list of records on the given date, ordered by class code ascending.
     */
    public List<AttendanceRecord> getRecordsOn(AttendanceDate date) {
        requireNonNull(date);
        return records.stream().filter(record -> record.getDate().equals(date)).toList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Attendance otherAttendance && records.equals(otherAttendance.records);
    }

    @Override
    public int hashCode() {
        return records.hashCode();
    }

    @Override
    public String toString() {
        return getRecords().toString();
    }
}
