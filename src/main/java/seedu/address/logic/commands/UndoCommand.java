package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Undoes the most recent command that changed storage data.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_SUCCESS = "Undone the previous operation.";
    public static final String MESSAGE_FAILURE = "There is no operation to undo.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_SUCCESS, false, true);
    }

}
