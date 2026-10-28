package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Represents a predicate that is used to filter the person list during find operations.
 */
public interface FindPredicate extends Predicate<Person> {

    // This predicate is used to determine whether the active filter should be ignored when filtering the person list.
    boolean ignoresActiveFilter();
}
