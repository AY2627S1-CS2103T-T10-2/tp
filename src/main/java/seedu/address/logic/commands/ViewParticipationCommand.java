package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;

/**
 * Shows one student's participation history without changing student data.
 */
public class ViewParticipationCommand extends Command {

    public static final String SUBCOMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = ParticipationCommand.COMMAND_WORD
            + " " + SUBCOMMAND_WORD + ": Shows a student's participation records.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER\n"
            + "Example: participation view 1";

    public static final String MESSAGE_EMPTY =
            "No participation records for %1$s.";

    public static final String MESSAGE_SUCCESS =
            "Participation history for %1$s (%2$d record(s)):\n%3$s";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final StudentIdentifier targetStudent;

    /**
     * Creates a request to view the identified student's participation history.
     */
    public ViewParticipationCommand(StudentIdentifier targetStudent) {
        this.targetStudent = requireNonNull(targetStudent);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);

        List<ParticipationRecord> records =
                new ArrayList<>(student.getParticipationHistory().getRecords());

        if (records.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_EMPTY, student.getName()));
        }

        records.sort(Comparator.comparing((ParticipationRecord record) -> record.getClassCode().toString())
                .thenComparing(ParticipationRecord::getDate));

        StringJoiner details = new StringJoiner("\n");
        for (ParticipationRecord record : records) {
            details.add(String.format("%s | %s | %s point(s)",
                    record.getClassCode(), record.getDate().format(DATE_FORMAT), record.getPoints()));
        }

        return new CommandResult(String.format(
                MESSAGE_SUCCESS, student.getName(), records.size(), details.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewParticipationCommand otherCommand)) {
            return false;
        }
        return targetStudent.equals(otherCommand.targetStudent);
    }

    @Override
    public int hashCode() {
        return targetStudent.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .toString();
    }
}
