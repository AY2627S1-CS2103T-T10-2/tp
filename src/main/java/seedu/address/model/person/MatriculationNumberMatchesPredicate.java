package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's matriculation number matches the given number.
 */
public class MatriculationNumberMatchesPredicate implements Predicate<Person> {

    private final MatriculationNumber matriculationNumber;

    /**
     * Creates a predicate that matches the given matriculation number.
     */
    public MatriculationNumberMatchesPredicate(MatriculationNumber matriculationNumber) {
        this.matriculationNumber = requireNonNull(matriculationNumber);
    }

    @Override
    public boolean test(Person person) {
        return person.getMatriculationNumber().equals(matriculationNumber);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MatriculationNumberMatchesPredicate otherPredicate)) {
            return false;
        }

        return matriculationNumber.equals(otherPredicate.matriculationNumber);
    }

    @Override
    public int hashCode() {
        return matriculationNumber.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("matriculationNumber", matriculationNumber)
                .toString();
    }
}
