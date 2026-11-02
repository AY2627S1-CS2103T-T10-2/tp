package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.Person;

/**
 * Removes one participation record while preserving the student's other information.
 */
public class RemoveParticipationCommand extends Command {

    public static final String SUBCOMMAND_WORD = "remove";

    public static final String MESSAGE_USAGE = ParticipationCommand.COMMAND_WORD
            + " " + SUBCOMMAND_WORD + ": Removes one participation record.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER c/MODULE-CLASS d/DATE\n"
            + "Example: participation remove 1 c/CS2103T-T10 d/28-10-2026";

    public static final String MESSAGE_SUCCESS =
            "Removed participation for %1$s in %2$s on %3$s.";

    public static final String MESSAGE_RECORD_NOT_FOUND =
            "No participation record for %1$s in %2$s on %3$s. "
                    + "Use participation view to check the student's records.";

    private static final Logger logger = LogsCenter.getLogger(RemoveParticipationCommand.class);

    private final StudentIdentifier targetStudent;
    private final ClassCode classCode;
    private final AttendanceDate date;

    /**
     * Creates a request to remove a record for the identified student, class and date.
     */
    public RemoveParticipationCommand(StudentIdentifier targetStudent, ClassCode classCode,
                                      AttendanceDate date) {
        this.targetStudent = requireNonNull(targetStudent);
        this.classCode = requireNonNull(classCode);
        this.date = requireNonNull(date);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        ParticipationHistory history = student.getParticipationHistory();

        if (history.findRecord(classCode, date.toLocalDate()).isEmpty()) {
            throw new CommandException(String.format(
                    MESSAGE_RECORD_NOT_FOUND, student.getName(), classCode, date));
        }

        ParticipationHistory updatedHistory = history.withoutRecord(classCode, date.toLocalDate());
        Person updatedStudent = student.withParticipationHistory(updatedHistory);
        model.setPerson(student, updatedStudent);

        logger.fine("Removed participation record from model; remaining session count: "
                + updatedHistory.getRecords().size());

        return new CommandResult(String.format(
                MESSAGE_SUCCESS, student.getName(), classCode, date));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RemoveParticipationCommand otherCommand)) {
            return false;
        }
        return targetStudent.equals(otherCommand.targetStudent)
                && classCode.equals(otherCommand.classCode)
                && date.equals(otherCommand.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetStudent, classCode, date);
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
