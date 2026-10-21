package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;

/**
 * Tests that a {@code Person}'s {@code ClassCodes} or {@code ModuleCodes} matches any of the {@code ClassCodes} or {@code ModuleCodes} given.
 */
public class PersonInModuleOrClassPredicate implements Predicate<Person> {
    private final Set<ClassCode> classCodes;
    private final Set<ModuleCode> moduleCodes;

    /**
     * Constructs a PersonInModuleOrClassPredicate object.
     * @param classCodes Set of {@link ClassCode}(s) to check the Person against.
     */
    public PersonInModuleOrClassPredicate(Set<ClassCode> classCodes, Set<ModuleCode> moduleCodes) {
        requireNonNull(classCodes);
        requireNonNull(moduleCodes);
        this.classCodes = Set.copyOf(classCodes);
        this.moduleCodes = Set.copyOf(moduleCodes);
    }

    @Override
    public boolean test(Person person) {
        return person.getClassCodes().stream().anyMatch(code ->
                classCodes.contains(code) || moduleCodes.contains(code.getModuleCode()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonInModuleOrClassPredicate otherPersonContainsClassCodePredicate)) {
            return false;
        }

        return classCodes.equals(otherPersonContainsClassCodePredicate.classCodes) &&
                moduleCodes.equals(otherPersonContainsClassCodePredicate.moduleCodes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("classCodes", classCodes).add("moduleCodes", classCodes).toString();
    }
}
