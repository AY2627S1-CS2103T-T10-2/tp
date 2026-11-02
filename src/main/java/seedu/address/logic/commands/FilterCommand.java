package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonInModuleOrClassPredicate;

/**
 * Filters all persons in the address book according to class and/or module.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists students enrolled in any of "
            + "the specified classes or modules, with index numbers. "
            + "The keywords are case insensitive.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: "
            + COMMAND_WORD + " CS2103T-T10 CS3230";

    public static final String MESSAGE_SUCCESS = Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
    public static final String MESSAGE_NO_MATCHING_STUDENTS =
            "No student matched the given class/module code(s) together with any active find. "
                    + "If a find is active, use list to clear it, then filter again.";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);

    private final PersonInModuleOrClassPredicate predicate;

    /**
     * Creates a FilterCommand to filter Person List with the specified {@code ClassCodes} or {@code ModuleCodes}
     */
    public FilterCommand(PersonInModuleOrClassPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilterPredicate(predicate);
        int personListSize = model.getFilteredPersonList().size();
        logger.fine("Filter used: " + predicate.toString() + "\nFilter matched " + personListSize + " student(s).");
        return new CommandResult(
                personListSize == 0 ? MESSAGE_NO_MATCHING_STUDENTS
                        : String.format(MESSAGE_SUCCESS, personListSize));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
