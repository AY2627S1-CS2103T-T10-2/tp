package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Displays an assignment definition and the marks of its assigned students.
 */
public class MarksCommand extends Command {
    public static final String COMMAND_WORD = "marks";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays an assignment and its student marks.\n"
            + "Parameters: " + PREFIX_ASSIGNMENT_ID + "MODULE:CODE\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ASSIGNMENT_ID + "CS2103T:A1";

    public static final String MESSAGE_NO_STUDENTS = "None assigned.";
    public static final String MESSAGE_UNMARKED = "Unmarked";

    private final AssignmentId assignmentId;

    /**
     * Creates a command that views one assignment's marks.
     */
    public MarksCommand(AssignmentId assignmentId) {
        this.assignmentId = requireNonNull(assignmentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ReadOnlyGradebook gradebook = model.getAddressBook().getGradebook();
        Assignment assignment = gradebook.findAssignment(assignmentId)
                .orElseThrow(() -> new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT));
        Map<MatriculationNumber, Person> students = model.getAddressBook().getPersonList().stream()
                .collect(Collectors.toMap(Person::getMatriculationNumber, Function.identity()));
        String maximum = assignment.getMaxMark().toPlainString();
        String rows = gradebook.getStudentMarks(assignmentId).entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .map(entry -> formatRow(entry.getKey(), students.get(entry.getKey()),
                        entry.getValue().map(mark -> mark + "/" + maximum).orElse(MESSAGE_UNMARKED)))
                .collect(Collectors.joining("\n"));
        String result = "Assignment: " + assignment + "\nStudent marks:\n"
                + (rows.isEmpty() ? MESSAGE_NO_STUDENTS : rows);
        return new CommandResult(result);
    }

    private static String formatRow(MatriculationNumber studentNumber, Person student, String result) {
        return studentNumber + " - " + student.getName() + ": " + result;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof MarksCommand otherCommand
                && assignmentId.equals(otherCommand.assignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentId", assignmentId)
                .toString();
    }
}
