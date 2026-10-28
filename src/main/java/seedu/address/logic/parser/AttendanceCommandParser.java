package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.commands.MarkClassAttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into an {@code AttendanceCommand}: a {@code MarkAttendanceCommand},
 * {@code MarkClassAttendanceCommand}, or {@code UnmarkAttendanceCommand}.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {
    public static final String MESSAGE_MISSING_CLASS_CODE =
            "Specify the class with " + PREFIX_CLASS_CODE + "MODULE-CLASS.\n%1$s";

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses a single-student mark or unmark request, or a whole-class mark request.
     * Accepts a displayed student index or matriculation number for single-student requests,
     * a class (required for marking), and an optional attendance date.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (matcher.matches()
                && matcher.group("subcommandWord").equalsIgnoreCase(UnmarkAttendanceCommand.SUBCOMMAND_WORD)) {
            return parseUnmark(matcher.group("arguments"));
        }
        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);
        if (isWholeClassRequest(argMultimap)) {
            return parseMarkClass(argMultimap);
        }
        return parseMark(argMultimap);
    }

    /**
     * Parses a mark request with a required class code.
     */
    private static MarkAttendanceCommand parseMark(ArgumentMultimap argMultimap) throws ParseException {
        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap,
                MarkAttendanceCommand.MESSAGE_USAGE);
        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap).orElseThrow(() ->
                new ParseException(String.format(MESSAGE_MISSING_CLASS_CODE, MarkAttendanceCommand.MESSAGE_USAGE)));

        Optional<AttendanceDate> date = ParserUtil.parseOptionalAttendanceDate(argMultimap);
        return new MarkAttendanceCommand(targetStudent, classCode, date);
    }

    /**
     * Returns whether the arguments specify a class without a student identifier.
     */
    private static boolean isWholeClassRequest(ArgumentMultimap argMultimap) {
        return argMultimap.getPreamble().isEmpty()
                && argMultimap.getValue(PREFIX_MATRICULATION_NUMBER).isEmpty()
                && argMultimap.getValue(PREFIX_CLASS_CODE).isPresent();
    }

    /**
     * Parses a whole-class mark request whose class prefix is present.
     */
    private static MarkClassAttendanceCommand parseMarkClass(ArgumentMultimap argMultimap) throws ParseException {
        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap).orElseThrow();
        Optional<AttendanceDate> date = ParserUtil.parseOptionalAttendanceDate(argMultimap);
        return new MarkClassAttendanceCommand(classCode, date);
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
