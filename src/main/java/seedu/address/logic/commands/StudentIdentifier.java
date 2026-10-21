package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Represents one student identified by either their displayed index or their matriculation number.
 * Exactly one identifier is present.
 */
public class StudentIdentifier {

    private final Optional<Index> index;
    private final Optional<MatriculationNumber> matriculationNumber;

    /**
     * Creates an identifier using the student's displayed {@code index}.
     */
    public StudentIdentifier(Index index) {
        this.index = Optional.of(requireNonNull(index));
        this.matriculationNumber = Optional.empty();
    }

    /**
     * Creates an identifier using the student's {@code matriculationNumber}.
     */
    public StudentIdentifier(MatriculationNumber matriculationNumber) {
        this.index = Optional.empty();
        this.matriculationNumber = Optional.of(requireNonNull(matriculationNumber));
    }

    /**
     * Returns the identified student in {@code model}.
     *
     * @throws CommandException if the displayed index is out of bounds or no stored student has
     *     the matriculation number.
     */
    public Person resolve(Model model) throws CommandException {
        requireNonNull(model);
        if (index.isPresent()) {
            return resolveByIndex(model, index.get());
        }
        return resolveByMatriculationNumber(model, matriculationNumber.orElseThrow());
    }

    private Person resolveByIndex(Model model, Index index) throws CommandException {
        List<Person> lastShownPersons = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownPersons.get(index.getZeroBased());
    }

    private Person resolveByMatriculationNumber(Model model, MatriculationNumber matriculationNumber)
            throws CommandException {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getMatriculationNumber().equals(matriculationNumber))
                .findFirst()
                .orElseThrow(() -> new CommandException(
                        String.format(Messages.MESSAGE_STUDENT_NOT_FOUND, matriculationNumber)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof StudentIdentifier otherStudentIdentifier)) {
            return false;
        }
        return index.equals(otherStudentIdentifier.index)
                && matriculationNumber.equals(otherStudentIdentifier.matriculationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, matriculationNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("matriculationNumber", matriculationNumber)
                .toString();
    }
}
