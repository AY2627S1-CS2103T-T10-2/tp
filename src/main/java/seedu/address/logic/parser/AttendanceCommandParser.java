package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into an {@code AttendanceCommand}.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    /**
     * Parses the displayed person index and optional attendance date.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE),
                    pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE);

        Optional<AttendanceDate> date = Optional.empty();
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            date = Optional.of(ParserUtil.parseAttendanceDate(argMultimap.getValue(PREFIX_DATE).get()));
        }

        return new AttendanceCommand(index, date);
    }
}
