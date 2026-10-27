package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnenrollCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;

/**
 * Parses input arguments and creates a new UnenrollCommand object
 */
public class UnenrollCommandParser implements Parser<UnenrollCommand> {

    /**
     * Parses the displayed person index and class code.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public UnenrollCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASS_CODE, PREFIX_ALL);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrollCommand.MESSAGE_USAGE),
                    pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS_CODE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ALL);

        // Both prefixes present
        if (argMultimap.getValue(PREFIX_CLASS_CODE).isPresent() && argMultimap.getValue(PREFIX_ALL).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrollCommand.MESSAGE_USAGE));
        }

        // None of the prefixes present
        if (argMultimap.getValue(PREFIX_CLASS_CODE).isEmpty() && argMultimap.getValue(PREFIX_ALL).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrollCommand.MESSAGE_USAGE));
        }

        // ClassCode absent -> All present
        if (argMultimap.getValue(PREFIX_CLASS_CODE).isEmpty()) {
            if (!argMultimap.getValue(PREFIX_ALL).get().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrollCommand.MESSAGE_USAGE));
            }
            return new UnenrollCommand(index);
        }

        // ClassCode present -> check validity
        String classCode = argMultimap.getValue(PREFIX_CLASS_CODE).get().trim();
        if (!ClassCode.isValidClassCode(classCode)) {
            throw new ParseException(ClassCode.MESSAGE_CONSTRAINTS);
        }
        ClassCode classCodeObj = new ClassCode(classCode);

        return new UnenrollCommand(index, classCodeObj);
    }

}
