package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;

import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into an {@code AttendanceCommand}.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    /**
     * Parses the displayed student index or matriculation number and optional attendance date.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER, PREFIX_DATE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_DATE);
        StudentIdentifier targetStudent =
                ParserUtil.parseStudentIdentifier(argMultimap, AttendanceCommand.MESSAGE_USAGE);

        Optional<AttendanceDate> date = Optional.empty();
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            date = Optional.of(ParserUtil.parseAttendanceDate(argMultimap.getValue(PREFIX_DATE).get()));
        }

        return new AttendanceCommand(targetStudent, date);
    }
}
