package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Enrolls a student in a class.
 */
public class EnrollCommand extends Command {
    public static final String COMMAND_WORD = "enroll";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Enrolls a student identified by their index or matriculation number in a class.\n"
            + "Parameters: INDEX or m/MATRICULATION_NUMBER c/MODULE-CLASS\n "
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_CLASS_CODE + "CS2103T-T10,\n"
            + COMMAND_WORD + " m/A1234567X " + PREFIX_CLASS_CODE + "CS2103T-T10";

    public static final String MESSAGE_SUCCESS = "Enrolled student %1$s in class %2$s.";
    public static final String MESSAGE_DUPLICATE_CLASS =
            "Student is already enrolled in class %1$s.";

    private static final Logger logger = LogsCenter.getLogger(EnrollCommand.class);

    private final ClassCode classCode;
    private final StudentIdentifier targetStudent;

    /**
     * Creates an EnrollCommand to enroll the specified {@code Person} at index in the specified {@code ClassCode}
     */
    public EnrollCommand(Index index, ClassCode classCode) {
        this(new StudentIdentifier(index), classCode);
    }

    /**
     * Creates an EnrollCommand to enroll the specified {@code Person} with the specified {@code MatriculationNumber} in
     * the specified {@code ClassCode}
     */
    public EnrollCommand(MatriculationNumber matriculationNumber, ClassCode classCode) {
        this(new StudentIdentifier(matriculationNumber), classCode);
    }

    /**
     * Creates an EnrollCommand to enroll the specified {@code Person} in the specified {@code ClassCode}
     */
    public EnrollCommand(StudentIdentifier targetStudent, ClassCode classCode) {
        this.classCode = requireNonNull(classCode);
        this.targetStudent = requireNonNull(targetStudent);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person student = targetStudent.resolve(model);
        assert student != null : "Student should not be null after resolving from model";
        logger.fine("Executing enroll: targetStudent=" + targetStudent.toString() + ", classCode=" + classCode);

        if (student.isEnrolledIn(classCode)) {
            logger.info("Enroll rejected: " + student.getName() + " already in " + classCode);
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CLASS, classCode));
        }
        model.setPerson(student, student.withClassCode(classCode));
        logger.info("Enrolled " + student.getName() + " in " + classCode);

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), classCode));
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

        return targetStudent.equals(otherEnrollCommand.targetStudent)
                && classCode.equals(otherEnrollCommand.classCode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudent", targetStudent)
                .add("classCode", classCode)
                .toString();
    }

}
