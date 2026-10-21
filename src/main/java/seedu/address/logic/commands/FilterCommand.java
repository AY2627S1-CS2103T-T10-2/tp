package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ClassContainsKeywordsPredicate;

/**
 * Filters all persons in the address book according to class.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters all students whose classes consists of "
            + "the specified class code (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: "
            + PREFIX_CLASS_CODE + "CLASS_CODE \n"
            + "Example: "
            + COMMAND_WORD + " CS2103T-T10";

    public static final String MESSAGE_SUCCESS = "Filtered all students by class code: %1$s";
    public static final String MESSAGE_NO_SUCH_CLASS = "This class code does not exist in the address book.";

    private final ClassContainsKeywordsPredicate predicate;

    public FilterCommand(ClassContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
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
