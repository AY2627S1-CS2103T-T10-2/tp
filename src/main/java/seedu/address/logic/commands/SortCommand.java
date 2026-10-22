package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.SortDisplay;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;
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
            case ATTENDANCE -> Comparator.comparingInt(person -> person.getAttendance().getDates().size());
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
        Assignment assignment = gradebook.findAssignment(id)
                .orElseThrow(() -> new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT));
        Map<MatriculationNumber, Optional<Mark>> marks = gradebook.getStudentMarks(id);
        Comparator<Person> comparator = (first, second) -> {
            Optional<Mark> firstMark = marks.getOrDefault(first.getMatriculationNumber(), Optional.empty());
            Optional<Mark> secondMark = marks.getOrDefault(second.getMatriculationNumber(), Optional.empty());
            if (firstMark.isEmpty() || secondMark.isEmpty()) {
                return Boolean.compare(firstMark.isEmpty(), secondMark.isEmpty());
            }
            int result = firstMark.orElseThrow().getValue().compareTo(secondMark.orElseThrow().getValue());
            return direction == SortDirection.DESC ? -result : result;
        };
        String maximum = assignment.getMaxMark().toPlainString();
        SortDisplay display = SortDisplay.createForAssignment(id, person -> {
            MatriculationNumber student = person.getMatriculationNumber();
            String value = marks.containsKey(student)
                    ? marks.get(student).map(mark -> mark + "/" + maximum).orElse("Unmarked")
                    : "Not assigned";
            return id + ": " + value;
        });
        model.sortPersons(comparator.thenComparing(Model.COMPARATOR_NAME_ASC), display);
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
