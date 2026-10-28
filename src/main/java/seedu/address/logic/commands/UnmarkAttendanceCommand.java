package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
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
            + "Parameters: INDEX or m/MATRICULATION_NUMBER [c/MODULE-CLASS] [d/DATE]\n"
            + "Example: " + MarkAttendanceCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 d/07-10-2026, "
            + MarkAttendanceCommand.COMMAND_WORD + " " + SUBCOMMAND_WORD + " m/A1234567X c/CS2103T-T10 d/07-10-2026";
    public static final String MESSAGE_SUCCESS = "Unmarked attendance for %1$s in %2$s on %3$s";
    public static final String MESSAGE_DATE_NOT_RECORDED = "No attendance recorded for %1$s on %2$s.";
    public static final String MESSAGE_RECORD_NOT_FOUND = "No attendance recorded for %1$s in %2$s on %3$s.";
    public static final String MESSAGE_MULTIPLE_CLASSES =
            "%1$s has attendance recorded in more than one class on %2$s: %3$s. "
                    + "Specify the class with c/MODULE-CLASS.";
    private final Optional<ClassCode> classCode;

    private final StudentIdentifier targetStudent;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an unmark attendance request for the identified student and optional class and date.
     */
    public UnmarkAttendanceCommand(StudentIdentifier targetStudent, Optional<ClassCode> classCode,
            Optional<AttendanceDate> date) {
        this.targetStudent = requireNonNull(targetStudent);
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        AttendanceRecord record = findRecordToRemove(student, attendanceDate);
        model.setPerson(student, MarkAttendanceCommand.createStudentWithAttendance(student,
                student.getAttendance().withoutRecord(record)));
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                student.getName(), record.getClassCode(), attendanceDate));
    }

    /**
     * Finds the requested record, inferring its class only when the date has exactly one record.
     */
    private AttendanceRecord findRecordToRemove(Person student, AttendanceDate attendanceDate) throws CommandException {
        if (classCode.isPresent()) {
            AttendanceRecord record = new AttendanceRecord(attendanceDate, classCode.orElseThrow());
            if (!student.getAttendance().contains(record)) {
                throw new CommandException(String.format(MESSAGE_RECORD_NOT_FOUND,
                        student.getName(), record.getClassCode(), attendanceDate));
            }
            return record;
        }
        List<AttendanceRecord> records = student.getAttendance().getRecordsOn(attendanceDate);
        if (records.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_DATE_NOT_RECORDED, student.getName(), attendanceDate));
        }
        if (records.size() > 1) {
            String classes = records.stream().map(record -> record.getClassCode().toString())
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_MULTIPLE_CLASSES,
                    student.getName(), attendanceDate, classes));
        }
        return records.getFirst();
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
                && classCode.equals(otherUnmarkAttendanceCommand.classCode)
                && date.equals(otherUnmarkAttendanceCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("classCode", classCode)
                .add("date", date)
                .toString();
    }
}
