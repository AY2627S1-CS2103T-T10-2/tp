package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.math.BigDecimal;
import java.util.Optional;

import seedu.address.logic.commands.AssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.person.MatriculationNumber;

/**
 * Parses the student, assignment ID, and optional new assignment details.
 */
public class AssignmentCommandParser implements Parser<AssignmentCommand> {
    public static final String MESSAGE_INVALID_MAX_MARK = "Maximum mark must be a valid number.";

    @Override
    public AssignmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER,
                PREFIX_ASSIGNMENT_ID, PREFIX_NAME, PREFIX_MAX_MARK);
        Optional<String> studentValue = arguments.getValue(PREFIX_MATRICULATION_NUMBER);
        Optional<String> idValue = arguments.getValue(PREFIX_ASSIGNMENT_ID);
        Optional<String> nameValue = arguments.getValue(PREFIX_NAME);
        Optional<String> maximumValue = arguments.getValue(PREFIX_MAX_MARK);
        if (!arguments.getPreamble().isEmpty() || studentValue.isEmpty() || idValue.isEmpty()
                || nameValue.isPresent() != maximumValue.isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_ASSIGNMENT_ID,
                PREFIX_NAME, PREFIX_MAX_MARK);

        MatriculationNumber student = ParserUtil.parseMatriculationNumber(studentValue.orElseThrow());
        String idText = idValue.orElseThrow().trim();
        if (!AssignmentId.isValidAssignmentId(idText)) {
            throw new ParseException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        AssignmentId id = new AssignmentId(idText);
        if (nameValue.isEmpty()) {
            return new AssignmentCommand(student, id);
        }

        String name = nameValue.orElseThrow();
        if (name.isBlank()) {
            throw new ParseException(Assignment.MESSAGE_NAME_CONSTRAINTS);
        }
        BigDecimal maximum;
        try {
            maximum = new BigDecimal(maximumValue.orElseThrow());
        } catch (NumberFormatException ex) {
            throw new ParseException(MESSAGE_INVALID_MAX_MARK, ex);
        }
        if (maximum.signum() <= 0) {
            throw new ParseException(Assignment.MESSAGE_MAX_MARK_CONSTRAINTS);
        }
        try {
            return new AssignmentCommand(student, new Assignment(id, name, maximum));
        } catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
    }
}
