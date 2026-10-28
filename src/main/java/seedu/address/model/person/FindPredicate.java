package seedu.address.model.person;

import java.util.function.Predicate;

public interface FindPredicate extends Predicate<Person> {

    // This predicate is used to determine whether the active filter should be ignored when filtering the person list.
    boolean ignoresActiveFilter();
}
