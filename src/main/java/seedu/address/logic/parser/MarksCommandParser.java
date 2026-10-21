package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;

import java.util.Optional;

import seedu.address.logic.commands.MarksCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;

/**
 * Parses an assignment ID for a MarksCommand.
 */
public class MarksCommandParser implements Parser<MarksCommand> {
    @Override
    public MarksCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT_ID);
        Optional<String> idValue = arguments.getValue(PREFIX_ASSIGNMENT_ID);
        if (!arguments.getPreamble().isEmpty() || idValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarksCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT_ID);

        String idText = idValue.orElseThrow().trim();
        if (!AssignmentId.isValidAssignmentId(idText)) {
            throw new ParseException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        return new MarksCommand(new AssignmentId(idText));
    }
}
