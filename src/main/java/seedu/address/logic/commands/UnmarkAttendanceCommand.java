package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;

/**
 * Removes the attendance record of the student identified by displayed index or matriculation number
 * on the given date, or today if no date is given.
 */
public class UnmarkAttendanceCommand extends Command {

    public static final String SUBCOMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = MarkAttendanceCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Removes the attendance record for the student identified by their displayed index"
            + " or matriculation number.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER [d/DATE]\n"
            + "Example: " + MarkAttendanceCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 d/07-10-2026, "
            + MarkAttendanceCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD + " m/A1234567X";

    public static final String MESSAGE_SUCCESS = "Unmarked attendance for %1$s on %2$s";
    public static final String MESSAGE_DATE_NOT_RECORDED = "No attendance recorded for %1$s on %2$s.";

    private final StudentIdentifier targetStudent;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an unmark attendance request for the identified student and optional date.
     */
    public UnmarkAttendanceCommand(StudentIdentifier targetStudent, Optional<AttendanceDate> date) {
        this.targetStudent = requireNonNull(targetStudent);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        if (!student.getAttendance().contains(attendanceDate)) {
            throw new CommandException(String.format(MESSAGE_DATE_NOT_RECORDED, student.getName(), attendanceDate));
        }

        model.setPerson(student, MarkAttendanceCommand.createStudentWithAttendance(student,
                student.getAttendance().withoutDate(attendanceDate)));
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), attendanceDate));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkAttendanceCommand otherUnmarkAttendanceCommand)) {
            return false;
        }

        return targetStudent.equals(otherUnmarkAttendanceCommand.targetStudent)
                && date.equals(otherUnmarkAttendanceCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("date", date)
                .toString();
    }
}
