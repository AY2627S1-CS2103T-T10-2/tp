package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.MatriculationNumberMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given arguments and returns a FindCommand for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Normalize whitespace because the tokenizer recognises prefixes after spaces.
        String tokenizableArgs = " " + trimmedArgs.replaceAll("\\s+", " ");
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(tokenizableArgs, PREFIX_MATRICULATION_NUMBER);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER);
        Optional<String> matriculationNumberValue =
                argMultimap.getValue(PREFIX_MATRICULATION_NUMBER);

        if (matriculationNumberValue.isPresent()) {
            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }

            MatriculationNumber matriculationNumber =
                    ParserUtil.parseMatriculationNumber(matriculationNumberValue.get());

            return new FindCommand(
                    new MatriculationNumberMatchesPredicate(matriculationNumber));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        return new FindCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }
}
