package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;

import seedu.address.model.person.Person;

/**
 * Describes the extra student detail shown while sorting by a field absent from the student card.
 */
public final class SortDisplay {
    public static final SortDisplay NONE = new SortDisplay(Optional.empty());
    public static final SortDisplay ATTENDANCE = withDetail(
            person -> "Attendance dates: " + person.getAttendance().getDates().size());

    private final Optional<Function<Person, String>> detailFormatter;

    private SortDisplay(Optional<Function<Person, String>> detailFormatter) {
        this.detailFormatter = detailFormatter;
    }

    /** Creates a display description using the given student detail formatter. */
    public static SortDisplay withDetail(Function<Person, String> detailFormatter) {
        return new SortDisplay(Optional.of(requireNonNull(detailFormatter)));
    }

    /** Returns the extra detail to display for a student, if any. */
    public Optional<String> detailFor(Person person) {
        requireNonNull(person);
        return detailFormatter.map(formatter -> formatter.apply(person));
    }
}
