package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Person;

public class EnrollCommand extends Command {
    public static final String COMMAND_WORD = "enroll";

    private final Index index;
    private final ClassCode classCode;

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Enrolls a student in a class.\n"
            + "Parameters: INDEX (must be a positive integer)" + PREFIX_CLASS_CODE + "MODULE-CLASS\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_CLASS_CODE + "CS2103-T01";

    public static final String MESSAGE_SUCCESS = "Enrolled student %1$s in class %2$s";
    public static final String MESSAGE_DUPLICATE_DATE =
            "Student is already enrolled in class %1$s.";
    
    public EnrollCommand(Index index, ClassCode classCode) {
        this.index = requireNonNull(index);
        this.classCode = requireNonNull(classCode);
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
        if (student.getClassCodes().contains(classCode)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_DATE, classCode));
        }
        model.setPerson(student, createEnrolledStudent(student, classCode));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), classCode));
    }

    /**
     * Creates a student with the given class code added, preserving the student's other details.
     */
    private static Person createEnrolledStudent(Person student, ClassCode classCode) {
        Set<ClassCode> updatedClassCodes = new HashSet<>(student.getClassCodes());
        updatedClassCodes.add(classCode);
        return new Person(student.getName(), student.getMatriculationNumber(), student.getEmail(),
                student.getTags(), student.getAttendance(), updatedClassCodes);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EnrollCommand otherEnrollCommand)) {
            return false;
        }

        return index.equals(otherEnrollCommand.index) &&
                classCode.equals(otherEnrollCommand.classCode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("classCode", classCode)
                .toString();
    }
    
}
