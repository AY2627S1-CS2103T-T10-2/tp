package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;

/**
 * Sets a student's participation points for a class on a particular date.
 * An existing score for the same class and date is replaced, not added to.
 */
public class ParticipationCommand extends Command {

    public static final String COMMAND_WORD = "participation";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets participation points for a student in a class on a given date.\n"
            + "An existing score for the same class and date is replaced.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER c/MODULE-CLASS d/DATE p/POINTS\n"
            + "DATE must be in dd-MM-yyyy format. POINTS must be a non-negative whole number.\n"
            + "Example: " + COMMAND_WORD + " m/A1234567X c/CS2103T-T10 d/28-10-2026 p/2";

    public static final String MESSAGE_SUCCESS =
            "Set participation for %1$s in %2$s on %3$s to %4$s point(s).";

    public static final String MESSAGE_NOT_ENROLLED =
            "%1$s is not enrolled in %2$s. Enroll the student in this class first.";

    public static final String MESSAGE_FUTURE_DATE =
            "Attendance date %s is in the future. Use today or an earlier date.";

    private static final Logger logger = LogsCenter.getLogger(ParticipationCommand.class);

    private final StudentIdentifier targetStudent;
    private final ClassCode classCode;
    private final AttendanceDate date;
    private final ParticipationPoints points;

    /**
     * Creates a request to set participation points for an identified student.
     * All arguments must be non-null.
     */
    public ParticipationCommand(StudentIdentifier targetStudent, ClassCode classCode,
                                AttendanceDate date, ParticipationPoints points) {
        this.targetStudent = requireNonNull(targetStudent);
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
        this.points = requireNonNull(points);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (date.toLocalDate().isAfter(LocalDate.now())) {
            throw new CommandException(String.format(MESSAGE_FUTURE_DATE, date));
        }

        Person student = targetStudent.resolve(model);
        if (!student.isEnrolledIn(classCode)) {
            throw new CommandException(
                    String.format(MESSAGE_NOT_ENROLLED, student.getName(), classCode));
        }

        ParticipationRecord record =
                new ParticipationRecord(classCode, date.toLocalDate(), points);
        ParticipationHistory updatedHistory =
                student.getParticipationHistory().withRecord(record);
        Person updatedStudent = student.withParticipationHistory(updatedHistory);

        model.setPerson(student, updatedStudent);

        logger.fine("Updated participation history in model; session count: "
                + updatedHistory.getRecords().size());

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, student.getName(), classCode, date, points));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ParticipationCommand otherCommand)) {
            return false;
        }

        return targetStudent.equals(otherCommand.targetStudent)
                && classCode.equals(otherCommand.classCode)
                && date.equals(otherCommand.date)
                && points.equals(otherCommand.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetStudent, classCode, date, points);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("classCode", classCode)
                .add("date", date)
                .add("points", points)
                .toString();
    }
}
