package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a valid attendance date in dd-MM-yyyy format. Instances are immutable.
 */
public final class AttendanceDate implements Comparable<AttendanceDate> {
    public static final String MESSAGE_CONSTRAINTS =
            "Attendance dates must be valid calendar dates in the format dd-MM-yyyy.";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final LocalDate date;

    /**
     * Constructs an attendance date from a valid date string.
     */
    public AttendanceDate(String value) {
        requireNonNull(value);
        checkArgument(isValidAttendanceDate(value), MESSAGE_CONSTRAINTS);
        date = LocalDate.parse(value, FORMATTER);
    }

    /**
     * Returns whether the string represents a calendar date with a year from 0001 to 9999.
     */
    public static boolean isValidAttendanceDate(String value) {
        requireNonNull(value);
        if (!value.matches("[0-9]{2}-[0-9]{2}-[0-9]{4}")) {
            return false;
        }
        try {
            return LocalDate.parse(value, FORMATTER).getYear() > 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public int compareTo(AttendanceDate other) {
        return date.compareTo(other.date);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AttendanceDate otherDate && date.equals(otherDate.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public String toString() {
        return date.format(FORMATTER);
    }
}
