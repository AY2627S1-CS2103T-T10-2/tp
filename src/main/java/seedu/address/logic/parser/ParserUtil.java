package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_FUTURE_ATTENDANCE_DATE =
            "Attendance date %1$s is in the future. Use today or an earlier date.";
    public static final String MESSAGE_MISSING_STUDENT_IDENTIFIER = "Specify the student by either INDEX or "
            + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER.\n%1$s";
    public static final String MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS = "Specify the student by either INDEX or "
            + PREFIX_MATRICULATION_NUMBER + "MATRICULATION_NUMBER, not both.\n%1$s";

    /**
     * Parses the student identifier in {@code argMultimap}: either an index in the preamble or a
     * matriculation number after m/, but not both.
     * {@code messageUsage} is the calling command's usage, appended to the missing/both errors.
     *
     * @throws ParseException if m/ is repeated, both or neither identifier is given, or the given one is invalid.
     */
    public static StudentIdentifier parseStudentIdentifier(ArgumentMultimap argMultimap, String messageUsage)
            throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MATRICULATION_NUMBER);
        String preamble = argMultimap.getPreamble();
        Optional<String> matriculationNumber = argMultimap.getValue(PREFIX_MATRICULATION_NUMBER);
        if (!preamble.isEmpty() && matriculationNumber.isPresent()) {
            throw new ParseException(String.format(MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, messageUsage));
        }
        if (matriculationNumber.isPresent()) {
            return new StudentIdentifier(parseMatriculationNumber(matriculationNumber.get()));
        }
        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_MISSING_STUDENT_IDENTIFIER, messageUsage));
        }
        return new StudentIdentifier(parseIndex(preamble));
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String date} into an {@code AttendanceDate}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code date} is invalid.
     */
    public static AttendanceDate parseAttendanceDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        if (!AttendanceDate.isValidAttendanceDate(trimmedDate)) {
            throw new ParseException(AttendanceDate.MESSAGE_CONSTRAINTS);
        }
        return new AttendanceDate(trimmedDate);
    }

    /**
     * Parses the optional attendance date after d/ in {@code argMultimap}.
     * Returns an empty {@code Optional} if d/ is absent, so the command can apply its default date.
     *
     * @throws ParseException if the given date is invalid.
     */
    public static Optional<AttendanceDate> parseOptionalAttendanceDate(ArgumentMultimap argMultimap)
            throws ParseException {
        requireNonNull(argMultimap);
        Optional<String> date = argMultimap.getValue(PREFIX_DATE);
        if (date.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parseAttendanceDate(date.get()));
    }

    /**
     * Parses the optional attendance date after d/ and rejects dates later than {@code today}.
     * Returns an empty {@code Optional} if d/ is absent, so the command can apply its default date.
     *
     * @param argMultimap Arguments containing the optional attendance date.
     * @param today Latest accepted attendance date.
     * @throws ParseException if the given date is invalid or later than today.
     */
    public static Optional<AttendanceDate> parseOptionalPastOrPresentAttendanceDate(ArgumentMultimap argMultimap,
            AttendanceDate today) throws ParseException {
        requireNonNull(today);
        Optional<AttendanceDate> date = parseOptionalAttendanceDate(argMultimap);
        if (date.isPresent() && date.get().compareTo(today) > 0) {
            throw new ParseException(String.format(MESSAGE_FUTURE_ATTENDANCE_DATE, date.get()));
        }
        return date;
    }

    /**
     * Parses a {@code String classCode} into a {@code ClassCode}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code classCode} is invalid.
     */
    public static ClassCode parseClassCode(String classCode) throws ParseException {
        requireNonNull(classCode);
        String trimmedClassCode = classCode.trim();
        if (!ClassCode.isValidClassCode(trimmedClassCode)) {
            throw new ParseException(ClassCode.MESSAGE_CONSTRAINTS);
        }
        return new ClassCode(trimmedClassCode);
    }

    /**
     * Parses the optional class code after c/ in {@code argMultimap}.
     * Returns an empty {@code Optional} if c/ is absent.
     * Duplicate-prefix checking is the caller's responsibility.
     *
     * @throws ParseException if the given class code is invalid.
     */
    public static Optional<ClassCode> parseOptionalClassCode(ArgumentMultimap argMultimap) throws ParseException {
        requireNonNull(argMultimap);
        Optional<String> classCode = argMultimap.getValue(PREFIX_CLASS_CODE);
        if (classCode.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parseClassCode(classCode.get()));
    }

    /**
     * Parses a {@code String matriculationNumber} into a {@code MatriculationNumber}. Leading and trailing whitespaces
     * will be trimmed.
     *
     * @throws ParseException if the given {@code matriculationNumber} is invalid.
     */
    public static MatriculationNumber parseMatriculationNumber(String matriculationNumber) throws ParseException {
        requireNonNull(matriculationNumber);
        String trimmedMatriculationNumber = matriculationNumber.trim();
        if (!MatriculationNumber.isValidMatriculationNumber(trimmedMatriculationNumber)) {
            throw new ParseException(MatriculationNumber.MESSAGE_CONSTRAINTS);
        }
        return new MatriculationNumber(trimmedMatriculationNumber);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
