package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;

import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnenrollCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new UnenrollCommand object
 */
public class UnenrollCommandParser implements Parser<UnenrollCommand> {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrollCommand.MESSAGE_USAGE);

    /**
     * Parses the student identifier (displayed index or matriculation number) and either a class code or the
     * all-classes option.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public UnenrollCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE, PREFIX_ALL);
        StudentIdentifier targetStudent =
                ParserUtil.parseStudentIdentifier(argMultimap, UnenrollCommand.MESSAGE_USAGE);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS_CODE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ALL);

        Optional<String> classCodeValue = argMultimap.getValue(PREFIX_CLASS_CODE);
        Optional<String> allValue = argMultimap.getValue(PREFIX_ALL);
        validateUnenrollmentMode(classCodeValue, allValue);

        if (classCodeValue.isEmpty()) {
            return new UnenrollCommand(targetStudent, Optional.empty());
        }

        return new UnenrollCommand(targetStudent,
                Optional.of(ParserUtil.parseClassCode(classCodeValue.orElseThrow())));
    }

    /**
     * Requires exactly one mode, with no value after the all-classes prefix.
     */
    private static void validateUnenrollmentMode(Optional<String> classCodeValue, Optional<String> allValue)
            throws ParseException {
        boolean hasClassCode = classCodeValue.isPresent();
        boolean hasAllPrefix = allValue.isPresent();
        boolean hasAllPayload = allValue.filter(value -> !value.isEmpty()).isPresent();
        if (hasClassCode == hasAllPrefix || hasAllPayload) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }
    }
}
