package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class ClassContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public ClassContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> person.getClassCodes().contains(new ClassCode(keyword)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClassContainsKeywordsPredicate otherClassContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherClassContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
