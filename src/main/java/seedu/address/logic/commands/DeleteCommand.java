package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

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
            + "Example: " + COMMAND_WORD + " 1", " + COMMAND_WORD + " m/A1234567X";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student: %1$s";

    public static final String MESSAGE_PERSON_NOT_FOUND =
            "No student with matriculation number %1$s found.";

    private final Optional<Index> targetIndex;
    private final Optional<MatriculationNumber> targetMatriculationNumber;

    /**
     * Creates a DeleteCommand that deletes a person using their displayed index.
     *
     * @param targetIndex Index of the person to delete
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = Optional.of(requireNonNull(targetIndex));
        this.targetMatriculationNumber = Optional.empty();
    }

    /**
     * Creates a DeleteCommand that deletes a person using their matriculation number.
     *
     * @param targetMatriculationNumber Matriculation number of the person to delete
     */
    public DeleteCommand(MatriculationNumber targetMatriculationNumber) {
        this.targetIndex = Optional.empty();
        this.targetMatriculationNumber = Optional.of(requireNonNull(targetMatriculationNumber));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToDelete;

        if (targetIndex.isPresent()) {
            Index index = targetIndex.get();
            List<Person> lastShownList = model.getFilteredPersonList();

            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            personToDelete = lastShownList.get(index.getZeroBased());
        } else {
            MatriculationNumber matriculationNumber = targetMatriculationNumber.orElseThrow();

            personToDelete = model.getAddressBook().getPersonList().stream()
                    .filter(person -> person.getMatriculationNumber().equals(matriculationNumber))
                    .findFirst()
                    .orElseThrow(() -> new CommandException(
                            String.format(MESSAGE_PERSON_NOT_FOUND, matriculationNumber)));
        }

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

        return targetIndex.equals(otherDeleteCommand.targetIndex)
                && targetMatriculationNumber.equals(otherDeleteCommand.targetMatriculationNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("targetMatriculationNumber", targetMatriculationNumber)
                .toString();
    }
}
