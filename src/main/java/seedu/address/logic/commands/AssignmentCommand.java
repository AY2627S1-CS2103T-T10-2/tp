package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Creates an assignment for a student or assigns an existing definition to that student.
 */
public class AssignmentCommand extends Command {
    public static final String COMMAND_WORD = "assignment";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns work to a student.\n"
            + "The student must be enrolled in the assignment's module.\n"
            + "Parameters for new work: (INDEX | " + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER) "
            + PREFIX_ASSIGNMENT_ID + "MODULE:CODE " + PREFIX_NAME + "NAME "
            + PREFIX_MAX_MARK + "MAX_MARK\n"
            + "Parameters for existing work: (INDEX | " + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER) "
            + PREFIX_ASSIGNMENT_ID + "MODULE:CODE\n"
            + "Specify exactly one identifier. INDEX refers to the current displayed list.\n"
            + "Examples for new work:\n" + COMMAND_WORD + " 1 " + PREFIX_ASSIGNMENT_ID + "CS2103T:A1 "
            + PREFIX_NAME + "Assignment One " + PREFIX_MAX_MARK + "20\n"
            + COMMAND_WORD + " " + PREFIX_MATRICULATION_NUMBER + "A1234567X "
            + PREFIX_ASSIGNMENT_ID + "CS2103T:A1 " + PREFIX_NAME + "Assignment One "
            + PREFIX_MAX_MARK + "20\n"
            + "Examples for existing work:\n" + COMMAND_WORD + " 1 " + PREFIX_ASSIGNMENT_ID + "CS2103T:A1\n"
            + COMMAND_WORD + " " + PREFIX_MATRICULATION_NUMBER + "A1234567X "
            + PREFIX_ASSIGNMENT_ID + "CS2103T:A1";

    public static final String MESSAGE_SUCCESS = "Assigned %1$s (out of %3$s) to student %2$s.";
    public static final String MESSAGE_STUDENT_NOT_ENROLLED =
            "Cannot assign %1$s to student %2$s: the student is not enrolled in module %3$s. "
                    + "Enroll the student first.";

    private final StudentIdentifier student;
    private final AssignmentId assignmentId;
    private final Optional<Assignment> newAssignment;

    /**
     * Creates a command that assigns an existing assignment definition.
     */
    public AssignmentCommand(MatriculationNumber student, AssignmentId assignmentId) {
        this(new StudentIdentifier(student), assignmentId);
    }

    /**
     * Creates a command using a displayed index or matriculation number.
     */
    public AssignmentCommand(StudentIdentifier student, AssignmentId assignmentId) {
        this.student = requireNonNull(student);
        this.assignmentId = requireNonNull(assignmentId);
        newAssignment = Optional.empty();
    }

    /**
     * Creates a command that defines and assigns new work.
     */
    public AssignmentCommand(MatriculationNumber student, Assignment assignment) {
        this(new StudentIdentifier(student), assignment);
    }

    /**
     * Creates a command using a displayed index or matriculation number.
     */
    public AssignmentCommand(StudentIdentifier student, Assignment assignment) {
        this.student = requireNonNull(student);
        this.assignmentId = requireNonNull(assignment).getId();
        newAssignment = Optional.of(assignment);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person targetStudent = student.resolve(model);
        MatriculationNumber studentNumber = targetStudent.getMatriculationNumber();

        if (newAssignment.isEmpty()
                && model.getAddressBook().getGradebook().findAssignment(assignmentId).isEmpty()) {
            throw new CommandException(Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
        }
        boolean isEnrolledInModule = targetStudent.getClassCodes().stream()
                .anyMatch(classCode -> classCode.getModuleCode().equals(assignmentId.getModuleCode()));
        if (!isEnrolledInModule) {
            throw new CommandException(String.format(MESSAGE_STUDENT_NOT_ENROLLED,
                    assignmentId, studentNumber, assignmentId.getModuleCode()));
        }
        try {
            if (newAssignment.isPresent()) {
                model.addAssignmentForStudent(newAssignment.orElseThrow(), studentNumber);
            } else {
                model.assignStudent(studentNumber, assignmentId);
            }
        } catch (IllegalArgumentException ex) {
            throw new CommandException(ex.getMessage());
        }
        Assignment assignment = model.getAddressBook().getGradebook().findAssignment(assignmentId).orElseThrow();
        return new CommandResult(String.format(MESSAGE_SUCCESS, assignmentId, studentNumber,
                assignment.getMaxMark().toPlainString()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AssignmentCommand otherCommand
                && student.equals(otherCommand.student)
                && assignmentId.equals(otherCommand.assignmentId)
                && newAssignment.equals(otherCommand.newAssignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, assignmentId, newAssignment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("student", student)
                .add("assignmentId", assignmentId)
                .add("newAssignment", newAssignment)
                .toString();
    }
}
