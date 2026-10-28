package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Deletes students identified by their displayed indices or a matriculation number.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes students identified by their displayed indices or a matriculation number.\n"
            + "Parameters: INDEX [MORE_INDICES]... or m/MATRICULATION_NUMBER\n"
            + "Examples: " + COMMAND_WORD + " 1; " + COMMAND_WORD + " 1 3; "
            + COMMAND_WORD + " m/A1234567X";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student: %1$s";

    public static final String MESSAGE_DELETE_STUDENTS_SUCCESS =
            "Deleted %1$d students:\n%2$s";

    public static final String MESSAGE_DUPLICATE_STUDENT =
            "The same student cannot be specified more than once. "
                    + "Remove duplicate targets and try again.";

    private final List<StudentIdentifier> targetStudents;

    /**
     * Creates a DeleteCommand that deletes a person using their displayed index.
     *
     * @param targetIndex Index of the person to delete
     */
    public DeleteCommand(Index targetIndex) {
        this(new StudentIdentifier(targetIndex));
    }

    /**
     * Creates a DeleteCommand that deletes a person using their matriculation number.
     *
     * @param targetMatriculationNumber Matriculation number of the person to delete
     */
    public DeleteCommand(MatriculationNumber targetMatriculationNumber) {
        this(new StudentIdentifier(targetMatriculationNumber));
    }

    /**
     * Creates a DeleteCommand that deletes the identified student.
     *
     * @param targetStudent Identifier of the student to delete
     */
    public DeleteCommand(StudentIdentifier targetStudent) {
        this(List.of(requireNonNull(targetStudent)));
    }

    /**
     * Creates a DeleteCommand that deletes the identified students.
     *
     * @param targetStudents Identifiers of the students to delete
     * @throws IllegalArgumentException if no identifiers are provided
     */
    public DeleteCommand(List<StudentIdentifier> targetStudents) {
        requireNonNull(targetStudents);
        if (targetStudents.isEmpty()) {
            throw new IllegalArgumentException("At least one student identifier is required.");
        }
        this.targetStudents = List.copyOf(targetStudents);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> studentsToDelete = new ArrayList<>();

        // Resolve all targets before deletion changes the displayed indices.
        for (StudentIdentifier targetStudent : targetStudents) {
            Person student = targetStudent.resolve(model);
            if (studentsToDelete.contains(student)) {
                throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
            }
            studentsToDelete.add(student);
        }

        StringJoiner deletedStudentDetails = new StringJoiner("\n");
        for (Person student : studentsToDelete) {
            model.deletePerson(student);
            deletedStudentDetails.add(Messages.format(student));
        }

        model.commitAddressBook();

        if (studentsToDelete.size() == 1) {
            return new CommandResult(String.format(
                    MESSAGE_DELETE_PERSON_SUCCESS, deletedStudentDetails.toString()));
        }

        return new CommandResult(String.format(
                MESSAGE_DELETE_STUDENTS_SUCCESS,
                studentsToDelete.size(), deletedStudentDetails.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetStudents.equals(otherDeleteCommand.targetStudents);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudents", targetStudents)
                .toString();
    }
}
