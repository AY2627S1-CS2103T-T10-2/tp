package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.AttendanceDate;

/**
 * Represents an attendance request. Recording attendance is deferred until v1.3.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks attendance for the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) [d/DATE]\n"
            + "Example: " + COMMAND_WORD + " 1 d/07-10-2026";

    public static final String MESSAGE_PLACEHOLDER = "Attendance recording is not implemented yet.";

    private static final String MESSAGE_RESULT = "%s Index: %s%s";
    private static final String MESSAGE_DATE_PREFIX = ", date: ";

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
    public CommandResult execute(Model model) {
        requireNonNull(model);
        String datePart = date.map(value -> MESSAGE_DATE_PREFIX + value).orElse("");
        return new CommandResult(String.format(MESSAGE_RESULT, MESSAGE_PLACEHOLDER, targetIndex.getOneBased(),
                datePart));
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
