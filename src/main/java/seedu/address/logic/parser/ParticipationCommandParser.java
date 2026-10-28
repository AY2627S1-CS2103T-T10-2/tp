package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION_POINTS;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ParticipationCommand;
import seedu.address.logic.commands.RemoveParticipationCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.ViewParticipationCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.ParticipationPoints;

/**
 * Parses requests to set, view or remove participation records.
 */
public class ParticipationCommandParser implements Parser<Command> {

    public static final String MESSAGE_MISSING_CLASS_CODE =
            "Specify the class with c/MODULE-CLASS.\n%1$s";
    public static final String MESSAGE_MISSING_DATE =
            "Specify the participation date with d/DATE (dd-MM-yyyy).\n%1$s";
    public static final String MESSAGE_MISSING_POINTS =
            "Specify participation points with p/POINTS.\n%1$s";
    public static final String MESSAGE_INVALID_DATE =
            "Participation dates must be valid calendar dates in the format dd-MM-yyyy, "
                    + "with a year from 0001 to 9999.";
    public static final String MESSAGE_UNEXPECTED_PREFIX =
            "Prefix %1$s is not supported for this subcommand.\n%2$s";

    /**
     * Parses a participation command or one of its subcommands.
     *
     * @throws ParseException if arguments are missing, repeated or invalid
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);

        // Participation arguments contain no free-text fields.
        String normalizedArgs = args.trim().replaceAll("\\s+", " ");
        String[] parts = normalizedArgs.split(" ", 2);
        String remainingArgs = parts.length == 2 ? parts[1] : "";

        if (ViewParticipationCommand.SUBCOMMAND_WORD.equalsIgnoreCase(parts[0])) {
            return parseView(remainingArgs);
        }
        if (RemoveParticipationCommand.SUBCOMMAND_WORD.equalsIgnoreCase(parts[0])) {
            return parseRemove(remainingArgs);
        }
        return parseSet(normalizedArgs);
    }

    /**
     * Parses the original request to set participation points.
     */
    private static ParticipationCommand parseSet(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenize(args);
        String usage = ParticipationCommand.MESSAGE_USAGE;

        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap, usage);
        ClassCode classCode = ParserUtil.parseClassCode(
                requireValue(argMultimap, PREFIX_CLASS_CODE, MESSAGE_MISSING_CLASS_CODE, usage));
        AttendanceDate date = parseDate(
                requireValue(argMultimap, PREFIX_DATE, MESSAGE_MISSING_DATE, usage));
        ParticipationPoints points = parsePoints(
                requireValue(argMultimap, PREFIX_PARTICIPATION_POINTS, MESSAGE_MISSING_POINTS, usage));

        return new ParticipationCommand(targetStudent, classCode, date, points);
    }

    /**
     * Parses a view request containing only a student identifier.
     */
    private static ViewParticipationCommand parseView(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenize(args);
        String usage = ViewParticipationCommand.MESSAGE_USAGE;

        rejectUnexpectedPrefixes(argMultimap, usage,
                PREFIX_CLASS_CODE, PREFIX_DATE, PREFIX_PARTICIPATION_POINTS);

        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap, usage);
        return new ViewParticipationCommand(targetStudent);
    }

    /**
     * Parses a remove request with a student, class and date, but no points.
     */
    private static RemoveParticipationCommand parseRemove(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenize(args);
        String usage = RemoveParticipationCommand.MESSAGE_USAGE;

        rejectUnexpectedPrefixes(argMultimap, usage, PREFIX_PARTICIPATION_POINTS);

        StudentIdentifier targetStudent = ParserUtil.parseStudentIdentifier(argMultimap, usage);
        ClassCode classCode = ParserUtil.parseClassCode(
                requireValue(argMultimap, PREFIX_CLASS_CODE, MESSAGE_MISSING_CLASS_CODE, usage));
        AttendanceDate date = parseDate(
                requireValue(argMultimap, PREFIX_DATE, MESSAGE_MISSING_DATE, usage));

        return new RemoveParticipationCommand(targetStudent, classCode, date);
    }

    /**
     * Recognizes participation prefixes and rejects repetitions.
     */
    private static ArgumentMultimap tokenize(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args,
                PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE,
                PREFIX_DATE, PREFIX_PARTICIPATION_POINTS);

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_MATRICULATION_NUMBER, PREFIX_CLASS_CODE,
                PREFIX_DATE, PREFIX_PARTICIPATION_POINTS);

        return argMultimap;
    }

    /**
     * Rejects recognized prefixes that do not belong to the selected subcommand.
     */
    private static void rejectUnexpectedPrefixes(ArgumentMultimap argMultimap, String usage,
                                                 Prefix... prefixes) throws ParseException {
        for (Prefix prefix : prefixes) {
            if (argMultimap.getValue(prefix).isPresent()) {
                throw new ParseException(String.format(MESSAGE_UNEXPECTED_PREFIX, prefix, usage));
            }
        }
    }

    /**
     * Returns a required value, leaving validation of empty values to the value parser.
     */
    private static String requireValue(ArgumentMultimap argMultimap, Prefix prefix,
                                       String missingMessage, String usage) throws ParseException {
        return argMultimap.getValue(prefix).orElseThrow(() ->
                new ParseException(String.format(missingMessage, usage)));
    }

    /**
     * Reuses calendar-date validation with a participation-specific error message.
     */
    private static AttendanceDate parseDate(String date) throws ParseException {
        try {
            return ParserUtil.parseAttendanceDate(date);
        } catch (ParseException exception) {
            throw new ParseException(MESSAGE_INVALID_DATE, exception);
        }
    }

    /**
     * Parses a non-negative whole number of participation points.
     */
    private static ParticipationPoints parsePoints(String points) throws ParseException {
        String trimmedPoints = points.trim();
        if (!ParticipationPoints.isValidPoints(trimmedPoints)) {
            throw new ParseException(ParticipationPoints.MESSAGE_CONSTRAINTS);
        }
        return new ParticipationPoints(trimmedPoints);
    }
}
