package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;

/**
 * Represents the common parent of the attendance command family, holding the optional session date.
 */
public abstract class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    private final Optional<AttendanceDate> date;

    /**
     * Constructs an attendance command with the optional session date.
     */
    protected AttendanceCommand(Optional<AttendanceDate> date) {
        this.date = requireNonNull(date);
    }

    protected Optional<AttendanceDate> getDate() {
        return date;
    }

    /**
     * Returns the given session date, or today if none is given.
     */
    protected AttendanceDate resolveDate() {
        return date.orElseGet(AttendanceDate::today);
    }

    /**
     * Creates a copy of the student with the given attendance history,
     * preserving other details, including participation history.
     */
    static Person createStudentWithAttendance(Person student, Attendance attendance) {
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), attendance, student.getClassCodes(), student.getParticipationHistory());
    }

    /**
     * Returns whether both commands have the same runtime class and optional session date.
     * Subclasses must call super.equals first, then compare their own fields.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        AttendanceCommand otherAttendanceCommand = (AttendanceCommand) other;
        return date.equals(otherAttendanceCommand.date);
    }
}
