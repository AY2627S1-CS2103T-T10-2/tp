package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Objects;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sorts the displayed students by a selected field and direction.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the displayed students by name, matriculation number, or email.\n"
            + "Format: sort or sort FIELD [asc|desc], where FIELD is name, matric, or email.\n"
            + "Examples: " + COMMAND_WORD + "; " + COMMAND_WORD + " matric desc";
    public static final String MESSAGE_SUCCESS = "Sorted displayed students by %1$s in %2$s order.";

    /** Fields supported by the sort command. */
    public enum SortField {
        NAME("name"),
        MATRIC("matriculation number"),
        EMAIL("email address");

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

    /** Creates a name-ascending sort command. */
    public SortCommand() {
        this(SortField.NAME, SortDirection.ASC);
    }

    /** Creates a sort command for the given field and direction. */
    public SortCommand(SortField field, SortDirection direction) {
        this.field = requireNonNull(field);
        this.direction = requireNonNull(direction);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        Comparator<Person> comparator = switch (field) {
            case NAME -> Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case MATRIC -> Comparator.comparing(person -> person.getMatriculationNumber().value);
            case EMAIL -> Comparator.comparing(person -> person.getEmail().value, String.CASE_INSENSITIVE_ORDER);
        };
        if (direction == SortDirection.DESC) {
            comparator = comparator.reversed();
        }
        model.sortPersons(comparator.thenComparing(Model.COMPARATOR_NAME_ASC));
        return new CommandResult(String.format(MESSAGE_SUCCESS, field.displayName, direction.displayName));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand
                && field == otherCommand.field && direction == otherCommand.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, direction);
    }
}
