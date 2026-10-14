package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MatriculationNumber;

/**
 * Parses input arguments and creates a new DeleteCommand object.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap argMultimap =
                    ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER);

            if (argMultimap.getValue(PREFIX_MATRICULATION_NUMBER).isPresent()) {
                if (!argMultimap.getPreamble().isEmpty()) {
                    throw new ParseException(String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
                }

                argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER);

                MatriculationNumber matriculationNumber =
                        ParserUtil.parseMatriculationNumber(
                                argMultimap.getValue(PREFIX_MATRICULATION_NUMBER).get());

                return new DeleteCommand(matriculationNumber);
            }

            Index index = ParserUtil.parseIndex(args);
            return new DeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }
}
