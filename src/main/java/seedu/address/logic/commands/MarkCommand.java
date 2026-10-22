package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.MatriculationNumber;

/**
 * Records or updates a numeric mark for a student's assigned work.
 */
public class MarkCommand extends Command {
    public static final String COMMAND_WORD = "mark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Records or updates an assignment mark.\n"
            + "Parameters: " + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER "
            + PREFIX_ASSIGNMENT_ID + "MODULE:CODE " + PREFIX_MARK + "MARK\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MATRICULATION_NUMBER + "A1234567X "
            + PREFIX_ASSIGNMENT_ID + "CS2103T:A1 " + PREFIX_MARK + "17.5";

    public static final String MESSAGE_RECORDED = "Recorded %1$s/%2$s for %3$s on %4$s.";
    public static final String MESSAGE_UPDATED = "Updated mark to %1$s/%2$s for %3$s on %4$s.";

    private final MatriculationNumber student;
    private final AssignmentId assignmentId;
    private final Mark mark;

    /**
     * Creates a command to record or replace a student's assignment mark.
     */
    public MarkCommand(MatriculationNumber student, AssignmentId assignmentId, Mark mark) {
        this.student = requireNonNull(student);
        this.assignmentId = requireNonNull(assignmentId);
        this.mark = requireNonNull(mark);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        boolean hasStudent = model.getAddressBook().getPersonList().stream()
                .anyMatch(person -> person.getMatriculationNumber().equals(student));
        if (!hasStudent) {
            throw new CommandException(AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        }

        ReadOnlyGradebook gradebook = model.getAddressBook().getGradebook();
        Assignment assignment = gradebook.findAssignment(assignmentId)
                .orElseThrow(() -> new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT));
        if (!gradebook.getMarksForStudent(student).containsKey(assignmentId)) {
            throw new CommandException(Gradebook.MESSAGE_UNASSIGNED_STUDENT);
        }

        Optional<Mark> previous;
        try {
            previous = model.recordMark(student, assignmentId, mark);
        } catch (IllegalArgumentException ex) {
            throw new CommandException(ex.getMessage());
        }
        String messageFormat = previous.isPresent() ? MESSAGE_UPDATED : MESSAGE_RECORDED;
        return new CommandResult(String.format(messageFormat, mark, assignment.getMaxMark().toPlainString(),
                student, assignmentId));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof MarkCommand otherCommand
                && student.equals(otherCommand.student)
                && assignmentId.equals(otherCommand.assignmentId)
                && mark.equals(otherCommand.mark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, assignmentId, mark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("student", student)
                .add("assignmentId", assignmentId)
                .add("mark", mark)
                .toString();
    }
}
