package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;

/**
 * Parses the student, assignment ID, and numeric mark for a MarkCommand.
 */
public class MarkCommandParser implements Parser<MarkCommand> {
    public static final String MESSAGE_INVALID_MARK = "Mark must be a valid number.";
    public static final String MESSAGE_MISSING_REQUIRED_PREFIX = "Missing required prefix: %s\n%s";

    @Override
    public MarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER,
                PREFIX_ASSIGNMENT_ID, PREFIX_MARK);
        for (Prefix prefix : List.of(PREFIX_ASSIGNMENT_ID, PREFIX_MARK)) {
            if (arguments.getValue(prefix).isEmpty()) {
                throw new ParseException(String.format(MESSAGE_MISSING_REQUIRED_PREFIX,
                        prefix, MarkCommand.MESSAGE_USAGE));
            }
        }
        StudentIdentifier student = ParserUtil.parseStudentIdentifier(arguments, MarkCommand.MESSAGE_USAGE);
        Optional<String> idValue = arguments.getValue(PREFIX_ASSIGNMENT_ID);
        Optional<String> markValue = arguments.getValue(PREFIX_MARK);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_ASSIGNMENT_ID, PREFIX_MARK);

        String idText = idValue.orElseThrow().trim();
        if (!AssignmentId.isValidAssignmentId(idText)) {
            throw new ParseException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        BigDecimal value;
        try {
            value = new BigDecimal(markValue.orElseThrow());
        } catch (NumberFormatException ex) {
            throw new ParseException(MESSAGE_INVALID_MARK, ex);
        }
        if (value.signum() < 0) {
            throw new ParseException(Mark.MESSAGE_CONSTRAINTS);
        }
        try {
            return new MarkCommand(student, new AssignmentId(idText), new Mark(value));
        } catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
    }
}
