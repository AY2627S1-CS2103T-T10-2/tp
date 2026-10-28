package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into a {@code MarkAttendanceCommand} or {@code UnmarkAttendanceCommand}.
 */
public class AttendanceCommandParser implements Parser<Command> {
    public static final String MESSAGE_MISSING_CLASS_CODE =
            "Specify the class with " + PREFIX_CLASS_CODE + "MODULE-CLASS.\n%1$s";

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses the optional unmark subcommand, displayed student index or matriculation number,
     * class (required for marking), and optional attendance date.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (matcher.matches()
                && matcher.group("subcommandWord").equalsIgnoreCase(UnmarkAttendanceCommand.SUBCOMMAND_WORD)) {
            return parseUnmark(matcher.group("arguments"));
        }
        return parseMark(args);
    }

    /**
     * Parses a mark request with a required class code.
     */
    private static MarkAttendanceCommand parseMark(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);
        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap,
                MarkAttendanceCommand.MESSAGE_USAGE);
        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap).orElseThrow(() ->
                new ParseException(String.format(MESSAGE_MISSING_CLASS_CODE, MarkAttendanceCommand.MESSAGE_USAGE)));

        Optional<AttendanceDate> date = ParserUtil.parseOptionalAttendanceDate(argMultimap);
        return new MarkAttendanceCommand(targetStudent, classCode, date);
    }

    /**
     * Parses an unmark request with an optional class code.
     */
    private static UnmarkAttendanceCommand parseUnmark(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);
        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap,
                UnmarkAttendanceCommand.MESSAGE_USAGE);
        Optional<ClassCode> classCode = ParserUtil.parseOptionalClassCode(argMultimap);
        Optional<AttendanceDate> date = ParserUtil.parseOptionalAttendanceDate(argMultimap);
        return new UnmarkAttendanceCommand(targetStudent, classCode, date);
    }

    /**
     * Tokenizes attendance arguments, rejecting duplicate prefixes before parsing their values.
     */
    private static ArgumentMultimap tokenizeAttendanceArguments(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE, PREFIX_DATE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE, PREFIX_DATE);
        return argMultimap;
    }
}
