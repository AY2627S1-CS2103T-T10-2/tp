package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;

/**
 * Marks the student identified by displayed index or matriculation number present on the given date,
 * or today if no date is given.
 */
public class MarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks attendance for the student identified by their displayed index or matriculation number.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER [d/DATE]\n"
            + "Example: " + COMMAND_WORD + " 1 d/07-10-2026, " + COMMAND_WORD + " m/A1234567X";

    public static final String MESSAGE_SUCCESS = "Marked attendance for %1$s on %2$s";
    public static final String MESSAGE_DUPLICATE_DATE =
            "Attendance for %1$s on %2$s has already been recorded.";

    private final StudentIdentifier targetStudent;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an attendance request for the displayed person index and optional date.
     */
    public MarkAttendanceCommand(Index targetIndex, Optional<AttendanceDate> date) {
        this(new StudentIdentifier(targetIndex), date);
    }

    /**
     * Constructs an attendance request for the identified student and optional date.
     */
    public MarkAttendanceCommand(StudentIdentifier targetStudent, Optional<AttendanceDate> date) {
        this.targetStudent = requireNonNull(targetStudent);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        if (student.getAttendance().contains(attendanceDate)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_DATE, student.getName(), attendanceDate));
        }

        model.setPerson(student,
                createStudentWithAttendance(student, student.getAttendance().withDate(attendanceDate)));
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), attendanceDate));
    }

    /**
     * Creates a copy of the student with the given attendance history, preserving other details.
     */
    static Person createStudentWithAttendance(Person student, Attendance attendance) {
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), attendance, student.getClassCodes());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkAttendanceCommand otherMarkAttendanceCommand)) {
            return false;
        }

        return targetStudent.equals(otherMarkAttendanceCommand.targetStudent)
                && date.equals(otherMarkAttendanceCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("date", date)
                .toString();
    }
}
