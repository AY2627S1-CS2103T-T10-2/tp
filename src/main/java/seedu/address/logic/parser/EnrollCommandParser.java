package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EnrollCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;

/**
 * Parses input arguments and creates a new EnrollCommand object
 */
public class EnrollCommandParser implements Parser<EnrollCommand> {

    /**
     * Parses the displayed person index and class code.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EnrollCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASS_CODE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EnrollCommand.MESSAGE_USAGE),
                    pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS_CODE);

        String classCode;
        if (!argMultimap.getValue(PREFIX_CLASS_CODE).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EnrollCommand.MESSAGE_USAGE));
        }
        classCode = argMultimap.getValue(PREFIX_CLASS_CODE).get().trim();
        if (!ClassCode.isValidClassCode(classCode)) {
            throw new ParseException(ClassCode.MESSAGE_CONSTRAINTS);
        }
        ClassCode classCodeObj = new ClassCode(classCode);

        return new EnrollCommand(index, classCodeObj);
    }

}
