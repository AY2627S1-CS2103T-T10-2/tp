package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Represents an immutable history of dates on which a student was marked present.
 * Missing dates indicate unrecorded attendance, not absence.
 */
public final class Attendance {
    public static final String MESSAGE_DUPLICATE_DATE = "Attendance for this date has already been recorded.";

    private final NavigableSet<AttendanceDate> dates;

    /**
     * Constructs a history from unique, non-null dates, making a defensive copy.
     */
    public Attendance(Collection<AttendanceDate> dates) {
        requireNonNull(dates);
        NavigableSet<AttendanceDate> copiedDates = new TreeSet<>();
        for (AttendanceDate date : dates) {
            requireNonNull(date);
            checkArgument(copiedDates.add(date), MESSAGE_DUPLICATE_DATE);
        }
        this.dates = Collections.unmodifiableNavigableSet(copiedDates);
    }

    /**
     * Returns an empty attendance history.
     */
    public static Attendance empty() {
        return new Attendance(List.of());
    }

    /**
     * Returns whether attendance has been recorded for the given date.
     */
    public boolean contains(AttendanceDate date) {
        return dates.contains(requireNonNull(date));
    }

    /**
     * Returns a new history with the date added, rejecting an already recorded date.
     */
    public Attendance withDate(AttendanceDate date) {
        requireNonNull(date);
        checkArgument(!contains(date), MESSAGE_DUPLICATE_DATE);
        NavigableSet<AttendanceDate> updatedDates = new TreeSet<>(dates);
        updatedDates.add(date);
        return new Attendance(updatedDates);
    }

    /**
     * Returns an immutable list of dates ordered from newest to oldest.
     */
    public List<AttendanceDate> getDates() {
        return List.copyOf(dates.descendingSet());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Attendance otherAttendance && dates.equals(otherAttendance.dates);
    }

    @Override
    public int hashCode() {
        return dates.hashCode();
    }

    @Override
    public String toString() {
        return getDates().toString();
    }
}
