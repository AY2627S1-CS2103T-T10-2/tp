package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
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
 * Marks every student enrolled in the given class present on the given date, or today if no date is given.
 * Students already recorded for that class and date are skipped.
 */
public class MarkClassAttendanceCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Marked attendance for %1$s in %2$s on %3$s";
    public static final String MESSAGE_SKIPPED = "\nAlready recorded, skipped: %1$s";
    public static final String MESSAGE_NO_ENROLLED_STUDENTS = "No students are enrolled in %1$s.";

    private final ClassCode classCode;
    private final Optional<AttendanceDate> date;

    /**
     * Constructs an attendance request for every student enrolled in the given class and optional date.
     */
    public MarkClassAttendanceCommand(ClassCode classCode, Optional<AttendanceDate> date) {
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        AttendanceDate attendanceDate = date.orElseGet(AttendanceDate::today);
        AttendanceRecord record = new AttendanceRecord(attendanceDate, classCode);
        List<Person> enrolledStudents = findEnrolledStudents(model);
        if (enrolledStudents.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_ENROLLED_STUDENTS, classCode));
        }

        List<Person> studentsToMark = new ArrayList<>();
        List<Person> skippedStudents = new ArrayList<>();
        for (Person student : enrolledStudents) {
            if (student.getAttendance().contains(record)) {
                skippedStudents.add(student);
            } else {
                studentsToMark.add(student);
            }
        }

        for (Person student : studentsToMark) {
            model.setPerson(student, MarkAttendanceCommand.createStudentWithAttendance(student,
                    student.getAttendance().withRecord(record)));
        }
        return new CommandResult(buildResultMessage(studentsToMark.size(), attendanceDate, skippedStudents));
    }

    /**
     * Returns enrolled students in stored order, independently of the displayed list.
     */
    private List<Person> findEnrolledStudents(Model model) {
        return model.getAddressBook().getPersonList().stream()
                .filter(student -> student.getClassCodes().contains(classCode))
                .toList();
    }

    /**
     * Builds the result with the marked count and, when present, skipped students in stored order.
     */
    private String buildResultMessage(int markedCount, AttendanceDate attendanceDate, List<Person> skippedStudents) {
        String message = String.format(MESSAGE_SUCCESS, formatStudentCount(markedCount), classCode, attendanceDate);
        if (skippedStudents.isEmpty()) {
            return message;
        }

        String skippedNames = skippedStudents.stream()
                .map(student -> student.getName().toString())
                .collect(Collectors.joining(", "));
        return message + String.format(MESSAGE_SKIPPED, skippedNames);
    }

    /**
     * Formats the student count with singular or plural wording.
     */
    private String formatStudentCount(int count) {
        return count == 1 ? "1 student" : count + " students";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkClassAttendanceCommand otherMarkClassAttendanceCommand)) {
            return false;
        }

        return classCode.equals(otherMarkClassAttendanceCommand.classCode)
                && date.equals(otherMarkClassAttendanceCommand.date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("classCode", classCode)
                .add("date", date)
                .toString();
    }
}
