package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Person;

/**
 * Marks the student identified by displayed index or matriculation number present in the given class
 * on the given date, or today if no date is given.
 */
public class MarkAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks attendance for the student identified by their displayed index or matriculation number"
            + " in the given class.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER c/MODULE-CLASS [d/DATE]\n"
            + "Example: " + COMMAND_WORD + " 1 c/CS2103T-T10 d/07-10-2026, "
            + COMMAND_WORD + " m/A1234567X c/CS2103T-T10";

    public static final String MESSAGE_SUCCESS = "Marked attendance for %1$s in %2$s on %3$s";
    public static final String MESSAGE_DUPLICATE_RECORD =
            "Attendance for %1$s in %2$s on %3$s has already been recorded.";

    public static final String MESSAGE_NOT_ENROLLED = "%1$s is not enrolled in %2$s.";
    public static final String MESSAGE_NO_CLASSES =
            "%1$s is not enrolled in any class. Enroll them in a class first.";

    private final ClassCode classCode;
    private final StudentIdentifier targetStudent;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an attendance request for the displayed person index given class and optional date.
     */
    public MarkAttendanceCommand(Index targetIndex, ClassCode classCode, Optional<AttendanceDate> date) {
        this(new StudentIdentifier(targetIndex), classCode, date);
    }

    /**
     * Constructs an attendance request for the identified student given class and optional date.
     */
    public MarkAttendanceCommand(StudentIdentifier targetStudent, ClassCode classCode, Optional<AttendanceDate> date) {
        this.targetStudent = requireNonNull(targetStudent);
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        requireEnrolled(student, classCode);
        AttendanceRecord record = new AttendanceRecord(attendanceDate, classCode);
        if (student.getAttendance().contains(record)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_RECORD,
                    student.getName(), classCode, attendanceDate));
        }

        model.setPerson(student,
                createStudentWithAttendance(student, student.getAttendance().withRecord(record)));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), classCode, attendanceDate));
    }

    /**
     * Rejects marking attendance unless the student is enrolled in the given class.
     */
    private static void requireEnrolled(Person student, ClassCode classCode) throws CommandException {
        if (student.getClassCodes().isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_CLASSES, student.getName()));
        }
        if (!student.getClassCodes().contains(classCode)) {
            throw new CommandException(String.format(MESSAGE_NOT_ENROLLED, student.getName(), classCode));
        }
    }

    /**
     * Creates a copy of the student with the given attendance history, preserving other details.
     */
    static Person createStudentWithAttendance(Person student, Attendance attendance) {
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), attendance, student.getClassCodes(), student.getParticipationHistory());
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
                && classCode.equals(otherMarkAttendanceCommand.classCode)
                && date.equals(otherMarkAttendanceCommand.date);
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
