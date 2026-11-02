
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
import seedu.address.logic.commands.MarkClassAttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.logic.commands.ViewAttendanceStatsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;

/**
 * Parses attendance arguments into a mark, unmark, whole-class mark,
 * or statistics command. Future-date checks do not apply to statistics.
 */
public class AttendanceCommandParser implements Parser<Command> {

    public static final String MESSAGE_MISSING_CLASS_CODE =
            "Specify the class with " + PREFIX_CLASS_CODE + "MODULE-CLASS.\n%1$s";

    private static final Pattern SUBCOMMAND_FORMAT =
            Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    private final AttendanceDate today;

    /**
     * Creates a parser using today's date.
     */
    public AttendanceCommandParser() {
        this(AttendanceDate.today());
    }

    /**
     * Creates a parser with a fixed date so tests can control today.
     */
    AttendanceCommandParser(AttendanceDate today) {
        this.today = requireNonNull(today);
    }

    /**
     * Parses attendance command arguments.
     *
     * @throws ParseException if the input does not conform to the expected format.
     */
    public Command parse(String args) throws ParseException {
        requireNonNull(args);

        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());

        if (matcher.matches()) {
            String subcommand = matcher.group("subcommandWord");

            if (subcommand.equalsIgnoreCase(UnmarkAttendanceCommand.SUBCOMMAND_WORD)) {
                return parseUnmark(matcher.group("arguments"));
            }

            if (subcommand.equalsIgnoreCase(ViewAttendanceStatsCommand.SUBCOMMAND_WORD)) {
                return parseStats(matcher.group("arguments"));
            }
        }

        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);

        if (isWholeClassRequest(argMultimap)) {
            return parseMarkClass(argMultimap);
        }

        return parseMark(argMultimap);
    }

    /**
     * Parses a request to mark one student's attendance.
     */
    private MarkAttendanceCommand parseMark(ArgumentMultimap argMultimap)
            throws ParseException {
        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap,
                MarkAttendanceCommand.MESSAGE_USAGE);

        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap)
                .orElseThrow(() -> new ParseException(String.format(
                        MESSAGE_MISSING_CLASS_CODE, MarkAttendanceCommand.MESSAGE_USAGE)));

        Optional<AttendanceDate> date = ParserUtil.parseOptionalPastOrPresentAttendanceDate(argMultimap, today);

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
     * Parses a request to mark the attendance of a whole class.
     */
    private MarkClassAttendanceCommand parseMarkClass(ArgumentMultimap argMultimap)
            throws ParseException {
        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap).orElseThrow();
        Optional<AttendanceDate> date = ParserUtil.parseOptionalPastOrPresentAttendanceDate(argMultimap, today);

        return new MarkClassAttendanceCommand(classCode, date);
    }

    /**
     * Parses a request to unmark one student's attendance.
     */
    private UnmarkAttendanceCommand parseUnmark(String args)
            throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);

        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap,
                UnmarkAttendanceCommand.MESSAGE_USAGE);

        Optional<ClassCode> classCode = ParserUtil.parseOptionalClassCode(argMultimap);
        Optional<AttendanceDate> date = ParserUtil.parseOptionalPastOrPresentAttendanceDate(argMultimap, today);

        return new UnmarkAttendanceCommand(targetStudent, classCode, date);
    }

    /**
     * Parses a read-only attendance statistics request.
     * Both the class and date must be specified.
     */
    private static ViewAttendanceStatsCommand parseStats(String args)
            throws ParseException {
        ArgumentMultimap argMultimap = tokenizeAttendanceArguments(args);

        if (!argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_MATRICULATION_NUMBER).isPresent()) {
            throw new ParseException(ViewAttendanceStatsCommand.MESSAGE_USAGE);
        }

        ClassCode classCode = ParserUtil.parseOptionalClassCode(argMultimap)
                .orElseThrow(() -> new ParseException(
                        ViewAttendanceStatsCommand.MESSAGE_USAGE));

        AttendanceDate date = ParserUtil.parseOptionalAttendanceDate(argMultimap)
                .orElseThrow(() -> new ParseException(
                        ViewAttendanceStatsCommand.MESSAGE_USAGE));

        return new ViewAttendanceStatsCommand(classCode, date);
    }

    /**
     * Tokenizes attendance arguments, rejecting duplicate prefixes.
     */
    private static ArgumentMultimap tokenizeAttendanceArguments(String args)
            throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE, PREFIX_DATE);

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE, PREFIX_DATE);

        return argMultimap;
    }
}
