package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;

/**
 * Marks the student at the displayed index present on the given date, or today if no date is given.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks attendance for the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX (must be a positive integer) [d/DATE]\n"
            + "Example: " + COMMAND_WORD + " 1 d/07-10-2026";

    public static final String MESSAGE_SUCCESS = "Marked attendance for %1$s on %2$s";
    public static final String MESSAGE_DUPLICATE_DATE =
            "Attendance for %1$s on %2$s has already been recorded.";

    private final Index targetIndex;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an attendance request for the displayed person index and optional date.
     */
    public AttendanceCommand(Index targetIndex, Optional<AttendanceDate> date) {
        this.targetIndex = requireNonNull(targetIndex);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(targetIndex.getZeroBased());
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        if (student.getAttendance().contains(attendanceDate)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_DATE, student.getName(), attendanceDate));
        }

        model.setPerson(student, createMarkedStudent(student, attendanceDate));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), attendanceDate));
    }

    /**
     * Creates a student with the given attendance date added, preserving the student's other details.
     */
    private static Person createMarkedStudent(Person student, AttendanceDate attendanceDate) {
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), student.getAttendance().withDate(attendanceDate));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AttendanceCommand otherAttendanceCommand)) {
            return false;
        }

        return targetIndex.equals(otherAttendanceCommand.targetIndex)
                && date.equals(otherAttendanceCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("date", date)
                .toString();
    }
}
