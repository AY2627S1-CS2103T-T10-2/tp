package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into an {@code AttendanceCommand} or {@code UnmarkAttendanceCommand}.
 */
public class AttendanceCommandParser implements Parser<Command> {

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses the optional unmark subcommand, displayed student index or matriculation number,
     * and optional attendance date.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (matcher.matches()
                && matcher.group("subcommandWord").equalsIgnoreCase(UnmarkAttendanceCommand.SUBCOMMAND_WORD)) {
            return parseAttendanceArguments(matcher.group("arguments"), UnmarkAttendanceCommand.MESSAGE_USAGE,
                    UnmarkAttendanceCommand::new);
        }

        return parseAttendanceArguments(args, AttendanceCommand.MESSAGE_USAGE, AttendanceCommand::new);
    }

    /**
     * Parses the student identifier and optional date shared by the attendance subcommands.
     * The {@code messageUsage} is shown in identifier errors.
     */
    private static Command parseAttendanceArguments(String args, String messageUsage,
            BiFunction<StudentIdentifier, Optional<AttendanceDate>, Command> commandFactory) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER, PREFIX_DATE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_DATE);
        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap, messageUsage);

        Optional<AttendanceDate> date = ParserUtil.parseOptionalAttendanceDate(argMultimap);

        return commandFactory.apply(targetStudent, date);
    }
}
