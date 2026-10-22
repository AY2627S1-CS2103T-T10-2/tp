package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;

import seedu.address.logic.commands.RemoveAssignmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.person.MatriculationNumber;

/**
 * Parses a shared assignment ID and an optional student for a RemoveAssignmentCommand.
 */
public class RemoveAssignmentCommandParser implements Parser<RemoveAssignmentCommand> {
    @Override
    public RemoveAssignmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_ASSIGNMENT_ID,
                PREFIX_MATRICULATION_NUMBER);
        Optional<String> idValue = arguments.getValue(PREFIX_ASSIGNMENT_ID);
        if (!arguments.getPreamble().isEmpty() || idValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemoveAssignmentCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_ASSIGNMENT_ID, PREFIX_MATRICULATION_NUMBER);

        String idText = idValue.orElseThrow().trim();
        if (!AssignmentId.isValidAssignmentId(idText)) {
            throw new ParseException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        AssignmentId id = new AssignmentId(idText);
        if (arguments.getValue(PREFIX_MATRICULATION_NUMBER).isEmpty()) {
            return new RemoveAssignmentCommand(id);
        }
        MatriculationNumber student = ParserUtil.parseMatriculationNumber(
                arguments.getValue(PREFIX_MATRICULATION_NUMBER).orElseThrow());
        return new RemoveAssignmentCommand(id, student);
    }
}
