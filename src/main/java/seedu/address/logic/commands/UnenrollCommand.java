package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Person;

/**
 * Unenrolls a student from a class. The student must exist in the class. If no class is specified, the student is
 * unenrolled from all classes.
 */
public class UnenrollCommand extends Command {
    public static final String COMMAND_WORD = "unenroll";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unenrolls a student from a class or from all classes.\n"
            + "Parameters: (INDEX (must be a positive integer) | " + PREFIX_MATRICULATION_NUMBER
            + "MATRICULATION_NUMBER) " + PREFIX_CLASS_CODE + "MODULE-CLASS\n"
            + "Alternate Parameters: (INDEX (must be a positive integer) | " + PREFIX_MATRICULATION_NUMBER
            + "MATRICULATION_NUMBER) " + PREFIX_ALL + "\n"
            + "Examples: " + COMMAND_WORD + " 1 " + PREFIX_CLASS_CODE + "CS2103T-T10\n"
            + "          " + COMMAND_WORD + " " + PREFIX_MATRICULATION_NUMBER + "A1234567X "
            + PREFIX_ALL;

    public static final String MESSAGE_SUCCESS = "Unenrolled student %1$s from class %2$s.";
    public static final String MESSAGE_SUCCESS_ALL = "Unenrolled student %1$s from all classes.";
    public static final String MESSAGE_MISSING_CLASS =
            "Student is not enrolled in class %1$s.";
    public static final String MESSAGE_MISSING_CLASS_ALL = "Student is not enrolled in any class.";

    private final StudentIdentifier targetStudent;
    private final Optional<ClassCode> classCode;

    /**
     * Creates an UnenrollCommand to unenroll the student at {@code index} from all classes.
     */
    public UnenrollCommand(Index index) {
        this(new StudentIdentifier(index), Optional.empty());
    }

    /**
     * Creates an UnenrollCommand to unenroll the student at {@code index} from the specified {@code ClassCode}.
     */
    public UnenrollCommand(Index index, ClassCode classCode) {
        this(new StudentIdentifier(index), Optional.of(classCode));
    }

    /**
     * Creates an UnenrollCommand to unenroll the student identified by {@code targetStudent} from the specified
     * {@code classCode}, or from all classes if {@code classCode} is empty.
     */
    public UnenrollCommand(StudentIdentifier targetStudent, Optional<ClassCode> classCode) {
        this.targetStudent = requireNonNull(targetStudent);
        this.classCode = requireNonNull(classCode);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);

        if (classCode.isPresent()) {
            return unenrollFromClass(model, student, classCode.orElseThrow());
        }
        return unenrollFromAllClasses(model, student);
    }

    /**
     * Unenrolls the student from the specified class.
     *
     * @throws CommandException if the student is not enrolled in the class.
     */
    private CommandResult unenrollFromClass(Model model, Person student, ClassCode targetClassCode)
            throws CommandException {
        if (!student.isEnrolledIn(targetClassCode)) {
            throw new CommandException(String.format(MESSAGE_MISSING_CLASS, targetClassCode));
        }
        model.setPerson(student, student.withoutClassCode(targetClassCode));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), targetClassCode));
    }

    /**
     * Unenrolls the student from all classes.
     *
     * @throws CommandException if the student is not enrolled in any class.
     */
    private CommandResult unenrollFromAllClasses(Model model, Person student) throws CommandException {
        if (!student.isEnrolled()) {
            throw new CommandException(MESSAGE_MISSING_CLASS_ALL);
        }
        model.setPerson(student, student.withoutClassCodes());
        return new CommandResult(String.format(MESSAGE_SUCCESS_ALL, student.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnenrollCommand otherUnenrollCommand)) {
            return false;
        }

        return targetStudent.equals(otherUnenrollCommand.targetStudent)
                && classCode.equals(otherUnenrollCommand.classCode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("classCode", classCode.orElse(null))
                .toString();
    }

}
