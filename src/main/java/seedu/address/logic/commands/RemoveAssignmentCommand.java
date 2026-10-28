package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.person.MatriculationNumber;

/**
 * Removes an assignment from one student or removes its shared definition and all student marks.
 */
public class RemoveAssignmentCommand extends Command {
    public static final String COMMAND_WORD = "removeassignment";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes an assignment and its marks.\n"
            + "Parameters: " + PREFIX_ASSIGNMENT_ID + "MODULE:CODE ["
            + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_ASSIGNMENT_ID + "CS2103T:A1 "
            + PREFIX_MATRICULATION_NUMBER + "A1234567X";

    public static final String MESSAGE_REMOVED_ASSIGNMENT = "Removed assignment %1$s and all student marks.";
    public static final String MESSAGE_UNASSIGNED_STUDENT = "Removed assignment %1$s from student %2$s.";

    private final AssignmentId assignmentId;
    private final Optional<MatriculationNumber> student;

    /**
     * Creates a command that removes a shared assignment and all of its student marks.
     */
    public RemoveAssignmentCommand(AssignmentId assignmentId) {
        this(assignmentId, Optional.empty());
    }

    /**
     * Creates a command that removes an assignment and mark from one student.
     */
    public RemoveAssignmentCommand(AssignmentId assignmentId, MatriculationNumber student) {
        this(assignmentId, Optional.of(requireNonNull(student)));
    }

    private RemoveAssignmentCommand(AssignmentId assignmentId, Optional<MatriculationNumber> student) {
        this.assignmentId = requireNonNull(assignmentId);
        this.student = requireNonNull(student);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getAddressBook().getGradebook().findAssignment(assignmentId).isEmpty()) {
            throw new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
        }

        if (student.isPresent()) {
            MatriculationNumber studentNumber = student.orElseThrow();
            boolean hasStudent = model.getAddressBook().getPersonList().stream()
                    .anyMatch(person -> person.getMatriculationNumber().equals(studentNumber));
            if (!hasStudent) {
                throw new CommandException(AddressBook.MESSAGE_STUDENT_NOT_FOUND);
            }
            try {
                model.unassignStudent(studentNumber, assignmentId);
            } catch (IllegalArgumentException ex) {
                throw new CommandException(ex.getMessage());
            }
            model.commitAddressBook();
            return new CommandResult(String.format(MESSAGE_UNASSIGNED_STUDENT, assignmentId, studentNumber));
        }

        model.removeAssignment(assignmentId);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_REMOVED_ASSIGNMENT, assignmentId));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RemoveAssignmentCommand otherCommand
                && assignmentId.equals(otherCommand.assignmentId)
                && student.equals(otherCommand.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentId, student);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("assignmentId", assignmentId)
                .add("student", student)
                .toString();
    }
}
