package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;

import seedu.address.model.person.Person;

/**
 * Describes the extra student detail shown while sorting by a field absent from the student card.
 */
public final class SortDisplay {
    public static final SortDisplay NONE = new SortDisplay(Optional.empty(), Optional.empty());
    public static final SortDisplay ATTENDANCE = createWithDetail(
            person -> "Attendance records: " + person.getAttendance().getRecords().size());

    private final Optional<Function<Person, String>> detailFormatter;
    private final Optional<AssignmentSort> assignmentSort;

    private SortDisplay(Optional<Function<Person, String>> detailFormatter, Optional<AssignmentSort> assignmentSort) {
        this.detailFormatter = detailFormatter;
        this.assignmentSort = assignmentSort;
    }

    /** Creates a display description using the given student detail formatter. */
    public static SortDisplay createWithDetail(Function<Person, String> detailFormatter) {
        return new SortDisplay(Optional.of(requireNonNull(detailFormatter)), Optional.empty());
    }

    /** Creates a display description for a sort on one assignment's marks. */
    public static SortDisplay createForAssignment(AssignmentSort assignmentSort,
            Function<Person, String> detailFormatter) {
        return new SortDisplay(Optional.of(requireNonNull(detailFormatter)),
                Optional.of(requireNonNull(assignmentSort)));
    }

    /** Returns whether this display belongs to a sort on assignment marks. */
    public boolean isAssignmentSort() {
        return assignmentSort.isPresent();
    }

    /** Returns the assignment sort to rebuild after restoring student records and marks. */
    public Optional<AssignmentSort> getAssignmentSort() {
        return assignmentSort;
    }

    /** Returns the extra detail to display for a student, if any. */
    public Optional<String> getDetailFor(Person person) {
        requireNonNull(person);
        return detailFormatter.map(formatter -> formatter.apply(person));
    }
}
