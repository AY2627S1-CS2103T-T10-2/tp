package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Person;

/**
 * Unenrolls a student in a class. The student must exist in the class.
 * If no class is specified, the student is unenrolled from all classes.
 */
public class UnenrollCommand extends Command {
    public static final String COMMAND_WORD = "unenroll";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unenrolls a student in a class. If no class is "
            + "specified, student is unenrolled in all classes.\n"
            + "Parameters: INDEX (must be a positive integer) [" + PREFIX_CLASS_CODE + "MODULE-CLASS]\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_CLASS_CODE + "CS2103T-T10";

    public static final String MESSAGE_SUCCESS = "Unenrolled student %1$s in class %2$s.";
    public static final String MESSAGE_SUCCESS_ALL = "Unenrolled student %1$s in all classes.";
    public static final String MESSAGE_MISSING_CLASS =
            "Student is not enrolled in class %1$s.";
    public static final String MESSAGE_MISSING_CLASS_ALL = "Student is not enrolled in any class.";

    private final Index index;
    private final ClassCode classCode;
    private final boolean unenrollAll;

    /**
     * Creates an UnenrollCommand to unenroll the specified {@code Person} in all classes.
     */
    public UnenrollCommand(Index index) {
        this.index = requireNonNull(index);
        this.classCode = null;
        this.unenrollAll = true;
    }

    /**
     * Creates an UnenrollCommand to unenroll the specified {@code Person} in the specified {@code ClassCode}
     */
    public UnenrollCommand(Index index, ClassCode classCode) {
        this.index = requireNonNull(index);
        this.classCode = requireNonNull(classCode);
        this.unenrollAll = false;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student;
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        student = lastShownList.get(index.getZeroBased());
        if (!unenrollAll) {
            if (!student.isEnrolledIn(classCode)) {
                throw new CommandException(String.format(MESSAGE_MISSING_CLASS, classCode));
            }
            model.setPerson(student, createUnenrolledStudent(student, classCode));
            return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), classCode));
        } else {
            if (!student.isEnrolled()) {
                throw new CommandException(String.format(MESSAGE_MISSING_CLASS_ALL, student.getName()));
            }
            model.setPerson(student, student.withoutClassCodes());
            return new CommandResult(String.format(MESSAGE_SUCCESS_ALL, student.getName()));
        }
    }

    /**
     * Creates a student with the given class code removed, preserving the student's other details.
     * Precondition: The student must be enrolled in the given class.
     */
    private static Person createUnenrolledStudent(Person student, ClassCode classCode) {
        return student.withoutClassCode(classCode);
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

        // unenrollAll is True
        if (unenrollAll) {
            return otherUnenrollCommand.unenrollAll && index.equals(otherUnenrollCommand.index);
        }

        return index.equals(otherUnenrollCommand.index)
                && ((unenrollAll && otherUnenrollCommand.unenrollAll)
                || classCode.equals(otherUnenrollCommand.classCode));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("classCode", classCode)
                .toString();
    }

}
