package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AssignmentSort;
import seedu.address.model.Model;
import seedu.address.model.SortDisplay;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.person.Person;

/**
 * Sorts the displayed students by a selected field and direction.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the displayed students by name, matriculation number, email, attendance count,"
            + " or one assignment mark.\n"
            + "Format: sort or sort FIELD [asc|desc], where FIELD is name, matric, email, or attendance;"
            + " or sort mark id/MODULE:CODE [asc|desc].\n"
            + "Examples: sort matric desc; sort mark id/CS2103T:A1 desc";
    public static final String MESSAGE_SUCCESS = "Sorted displayed students by %1$s in %2$s order.";

    /** Fields supported by the sort command. */
    public enum SortField {
        NAME("name"),
        MATRIC("matriculation number"),
        EMAIL("email address"),
        ATTENDANCE("attendance count"),
        MARK("assignment mark");

        private final String displayName;

        SortField(String displayName) {
            this.displayName = displayName;
        }
    }

    /** Directions supported by the sort command. */
    public enum SortDirection {
        ASC("ascending"),
        DESC("descending");

        private final String displayName;

        SortDirection(String displayName) {
            this.displayName = displayName;
        }
    }

    private final SortField field;
    private final SortDirection direction;
    private final Optional<AssignmentId> assignmentId;

    /** Creates a name-ascending sort command. */
    public SortCommand() {
        this(SortField.NAME, SortDirection.ASC);
    }

    /** Creates a sort command for the given field and direction. */
    public SortCommand(SortField field, SortDirection direction) {
        this.field = requireNonNull(field);
        this.direction = requireNonNull(direction);
        if (field == SortField.MARK) {
            throw new IllegalArgumentException("Mark sorting requires an assignment ID.");
        }
        this.assignmentId = Optional.empty();
    }

    /** Creates a sort command for one assignment's marks. */
    public SortCommand(AssignmentId assignmentId, SortDirection direction) {
        this.field = SortField.MARK;
        this.direction = requireNonNull(direction);
        this.assignmentId = Optional.of(requireNonNull(assignmentId));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (field == SortField.MARK) {
            return sortByMark(model);
        }

        Comparator<Person> comparator = switch (field) {
            case NAME -> Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case MATRIC -> Comparator.comparing(person -> person.getMatriculationNumber().value);
            case EMAIL -> Comparator.comparing(person -> person.getEmail().value, String.CASE_INSENSITIVE_ORDER);
            case ATTENDANCE -> Comparator.comparingInt(person -> person.getAttendance().getRecords().size());
            case MARK -> throw new AssertionError("Mark sorting is handled separately.");
        };
        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }
        SortDisplay sortDisplay = field == SortField.ATTENDANCE ? SortDisplay.ATTENDANCE : SortDisplay.NONE;
        model.sortPersons(comparator.thenComparing(Model.COMPARATOR_NAME_ASC), sortDisplay);
        return new CommandResult(String.format(MESSAGE_SUCCESS, field.displayName, direction.displayName));
    }

    private CommandResult sortByMark(Model model) throws CommandException {
        AssignmentId id = assignmentId.orElseThrow();
        var gradebook = model.getAddressBook().getGradebook();
        gradebook.findAssignment(id)
                .orElseThrow(() -> new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT));
        AssignmentSort sort = new AssignmentSort(id, direction == SortDirection.DESC);
        model.sortPersons(sort.createComparator(gradebook), sort.createDisplay(gradebook));
        return new CommandResult(String.format(MESSAGE_SUCCESS, id + " mark", direction.displayName));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand
                && field == otherCommand.field && direction == otherCommand.direction
                && assignmentId.equals(otherCommand.assignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, direction, assignmentId);
    }
}
