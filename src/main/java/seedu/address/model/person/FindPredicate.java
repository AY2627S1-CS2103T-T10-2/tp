package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Represents a predicate that is used to filter the person list during find operations.
 */
public interface FindPredicate extends Predicate<Person> {

    /**
     * Returns true if the predicate ignores the active filter, false otherwise.
     */
    boolean ignoresActiveFilter();
}
