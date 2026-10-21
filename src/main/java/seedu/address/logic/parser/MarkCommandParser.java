package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.math.BigDecimal;
import java.util.Optional;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

/**
 * Parses the student, assignment ID, and numeric mark for a MarkCommand.
 */
public class MarkCommandParser implements Parser<MarkCommand> {
    public static final String MESSAGE_INVALID_MARK = "Mark must be a valid number.";

    @Override
    public MarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER,
                PREFIX_ASSIGNMENT_ID, PREFIX_MARK);
        Optional<String> studentValue = arguments.getValue(PREFIX_MATRICULATION_NUMBER);
        Optional<String> idValue = arguments.getValue(PREFIX_ASSIGNMENT_ID);
        Optional<String> markValue = arguments.getValue(PREFIX_MARK);
        if (!arguments.getPreamble().isEmpty() || studentValue.isEmpty() || idValue.isEmpty()
                || markValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_ASSIGNMENT_ID, PREFIX_MARK);

        MatriculationNumber student = ParserUtil.parseMatriculationNumber(studentValue.orElseThrow());
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
