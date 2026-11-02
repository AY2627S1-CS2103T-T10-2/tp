package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import seedu.address.logic.commands.EnrollCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;

/**
 * Parses input arguments and creates a new EnrollCommand object
 */
public class EnrollCommandParser implements Parser<EnrollCommand> {

    /**
     * Parses the student identifier (displayed index or matriculation number) and class code.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EnrollCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE);

        StudentIdentifier targetStudent =
                ParserUtil.parseStudentIdentifier(argMultimap, EnrollCommand.MESSAGE_USAGE);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS_CODE);

        if (!argMultimap.getValue(PREFIX_CLASS_CODE).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EnrollCommand.MESSAGE_USAGE));
        }
        ClassCode classCode = ParserUtil.parseClassCode(argMultimap.getValue(PREFIX_CLASS_CODE).get());

        return new EnrollCommand(targetStudent, classCode);
    }

}
