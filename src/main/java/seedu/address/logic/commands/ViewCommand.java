package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Displays the details of a student identified by its displayed index.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays the details of the student identified by the index number used in the displayed "
            + "student list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_PERSON_SUCCESS = "Student details:\n%1$s";

    private final Index targetIndex;

    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToView = lastShownList.get(targetIndex.getZeroBased());
        String attendanceDates = personToView.getAttendance().getDates().stream()
                .map(AttendanceDate::toString)
                .collect(Collectors.joining(", "));
        String attendance = attendanceDates.isEmpty() ? "None recorded" : attendanceDates;
        String tags = personToView.getTags().stream()
                .map(Tag::toString)
                .sorted()
                .collect(Collectors.joining(" "));
        String details = String.join("\n",
                "Name: " + personToView.getName(),
                "Matriculation Number: " + personToView.getMatriculationNumber(),
                "Email: " + personToView.getEmail(),
                "Address: " + personToView.getAddress(),
                "Tags: " + (tags.isEmpty() ? "None" : tags),
                "Attendance: " + attendance);
        return new CommandResult(String.format(MESSAGE_VIEW_PERSON_SUCCESS, details));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
