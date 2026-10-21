package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using either their displayed index
 * or matriculation number.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the student identified by their displayed index or matriculation number.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER\n"
            + "Example: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " m/A1234567X";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student: %1$s";

    private final StudentIdentifier targetStudent;

    /**
     * Creates a DeleteCommand that deletes a person using their displayed index.
     *
     * @param targetIndex Index of the person to delete
     */
    public DeleteCommand(Index targetIndex) {
        this(new StudentIdentifier(targetIndex));
    }

    /**
     * Creates a DeleteCommand that deletes a person using their matriculation number.
     *
     * @param targetMatriculationNumber Matriculation number of the person to delete
     */
    public DeleteCommand(MatriculationNumber targetMatriculationNumber) {
        this(new StudentIdentifier(targetMatriculationNumber));
    }

    /**
     * Creates a DeleteCommand that deletes the identified student.
     *
     * @param targetStudent Identifier of the student to delete
     */
    public DeleteCommand(StudentIdentifier targetStudent) {
        this.targetStudent = requireNonNull(targetStudent);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToDelete = targetStudent.resolve(model);

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(
                MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetStudent.equals(otherDeleteCommand.targetStudent);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .toString();
    }
}
