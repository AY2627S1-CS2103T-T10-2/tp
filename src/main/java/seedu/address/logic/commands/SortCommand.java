package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Sorts all persons in the address book.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Sorts all entries by name.\n"
                    + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Sorted all students by name.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.sortPersons(Model.COMPARATOR_NAME_ASC);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
