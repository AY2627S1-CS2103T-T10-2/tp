package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;

/**
 * Tests that a {@code Person}'s {@code ClassCodes} matches any of the {@code ClassCodes} given.
 */
public class PersonContainsClassCodePredicate implements Predicate<Person> {
    private final List<ClassCode> classCodes;

    public PersonContainsClassCodePredicate(List<ClassCode> classCodes) {
        this.classCodes = classCodes;
    }

    @Override
    public boolean test(Person person) {
        return classCodes.stream()
                .anyMatch(classCode -> person.getClassCodes().contains(classCode));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonContainsClassCodePredicate otherPersonContainsClassCodePredicate)) {
            return false;
        }

        return classCodes.equals(otherPersonContainsClassCodePredicate.classCodes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("classCodes", classCodes).toString();
    }
}
