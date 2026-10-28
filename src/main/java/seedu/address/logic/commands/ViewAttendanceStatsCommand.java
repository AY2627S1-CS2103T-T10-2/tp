
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Person;

/**
 * Displays the number of students who attended a class on a given date,
 * together with the number of students currently enrolled in that class.
 */
public class ViewAttendanceStatsCommand extends Command {

    public static final String SUBCOMMAND_WORD = "stats";

    public static final String MESSAGE_USAGE = "attendance stats: Shows attendance statistics"
            + " for a class on a given date.\n"
            + "Parameters: c/MODULE-CLASS d/DATE\n"
            + "Example: attendance stats c/CS2103T-T10 d/28-10-2026";

    public static final String MESSAGE_SUCCESS =
            "Attendance statistics for %1$s on %2$s\n"
                    + "Students attended: %3$d\n"
                    + "Total students: %4$d";

    private final ClassCode classCode;
    private final AttendanceDate date;

    /**
     * Creates a request to view attendance statistics for a class session.
     */
    public ViewAttendanceStatsCommand(ClassCode classCode, AttendanceDate date) {
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> enrolledStudents = model.getAddressBook().getPersonList().stream()
                .filter(student -> student.getClassCodes().contains(classCode))
                .toList();

        AttendanceRecord targetRecord = new AttendanceRecord(date, classCode);

        long attendedCount = enrolledStudents.stream()
                .filter(student -> student.getAttendance().contains(targetRecord))
                .count();

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                classCode, date, attendedCount, enrolledStudents.size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewAttendanceStatsCommand otherCommand)) {
            return false;
        }

        return classCode.equals(otherCommand.classCode)
                && date.equals(otherCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classCode", classCode)
                .add("date", date)
                .toString();
    }
}
