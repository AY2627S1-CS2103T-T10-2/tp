package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final AttendanceDate TODAY = new AttendanceDate("15-06-2026");
    private static final String USAGE = "usage";
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_MATRICULATION_NUMBER = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_MATRICULATION_NUMBER = "A1234567A";
    private static final String VALID_ATTENDANCE_DATE = "07-10-2026";
    private static final String VALID_CLASS_CODE = "CS2103T-T10";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseStudentIdentifier_validIndex_returnsIdentifier() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1", PREFIX_MATRICULATION_NUMBER);
        assertEquals(new StudentIdentifier(INDEX_FIRST_PERSON), ParserUtil.parseStudentIdentifier(arguments, USAGE));
    }

    @Test
    public void parseStudentIdentifier_validMatriculationNumber_returnsIdentifier() throws Exception {
        StudentIdentifier expected = new StudentIdentifier(new MatriculationNumber("A1234567X"));
        for (String input : List.of(" m/A1234567X", " m/a1234567x", " m/ A1234567X ")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(input, PREFIX_MATRICULATION_NUMBER);
            assertEquals(expected, ParserUtil.parseStudentIdentifier(arguments, USAGE));
        }
    }

    @Test
    public void parseStudentIdentifier_missingIdentifier_throwsParseException() {
        String expectedMessage = "Specify the student by either INDEX or m/MATRICULATION_NUMBER.\nusage";
        assertStudentIdentifierParseFailure("", expectedMessage);
        assertStudentIdentifierParseFailure(" ", expectedMessage);
    }

    @Test
    public void parseStudentIdentifier_bothIdentifiers_throwsParseException() {
        assertStudentIdentifierParseFailure(" 1 m/A1234567X",
                "Specify the student by either INDEX or m/MATRICULATION_NUMBER, not both.\nusage");
    }

    @Test
    public void parseStudentIdentifier_duplicateMatriculationNumbers_throwsParseException() {
        assertStudentIdentifierParseFailure(" m/A1234567X m/A1234567Y",
                "Multiple values specified for the following single-valued field(s): m/");
    }

    @Test
    public void parseStudentIdentifier_multipleProblems_throwsHigherPriorityError() {
        assertStudentIdentifierParseFailure(" 1 m/A1234567X m/A1234567Y",
                "Multiple values specified for the following single-valued field(s): m/");
        assertStudentIdentifierParseFailure(" 1 m/bad",
                "Specify the student by either INDEX or m/MATRICULATION_NUMBER, not both.\nusage");
    }

    @Test
    public void parseStudentIdentifier_malformedIndex_throwsParseException() {
        for (String input : List.of(" 0", " -1", " a", " 1 extra")) {
            assertStudentIdentifierParseFailure(input, "Index must be a positive integer.");
        }
    }

    @Test
    public void parseStudentIdentifier_malformedMatriculationNumber_throwsParseException() {
        for (String input : List.of(" m/A123456X", " m/A12345678X", " m/A123", " m/", " m/B1234567X")) {
            assertStudentIdentifierParseFailure(input, MatriculationNumber.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parseStudentIdentifier_nullMultimap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentIdentifier(null, USAGE));
    }

    private void assertStudentIdentifierParseFailure(String input, String expectedMessage) {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(input, PREFIX_MATRICULATION_NUMBER);
        assertThrows(ParseException.class, expectedMessage, ()
                -> ParserUtil.parseStudentIdentifier(arguments, USAGE));
    }

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseAttendanceDate_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAttendanceDate(null));
    }

    @Test
    public void parseAttendanceDate_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseAttendanceDate("7-10-2026"));
        assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseAttendanceDate("32-01-2026"));
        assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseAttendanceDate("29-02-2026"));
        assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseAttendanceDate(""));
    }

    @Test
    public void parseAttendanceDate_validValueWithoutWhitespace_returnsAttendanceDate() throws Exception {
        AttendanceDate expectedDate = new AttendanceDate(VALID_ATTENDANCE_DATE);
        assertEquals(expectedDate, ParserUtil.parseAttendanceDate(VALID_ATTENDANCE_DATE));
    }

    @Test
    public void parseAttendanceDate_validValueWithWhitespace_returnsTrimmedAttendanceDate() throws Exception {
        String dateWithWhitespace = WHITESPACE + VALID_ATTENDANCE_DATE + WHITESPACE;
        AttendanceDate expectedDate = new AttendanceDate(VALID_ATTENDANCE_DATE);
        assertEquals(expectedDate, ParserUtil.parseAttendanceDate(dateWithWhitespace));
    }

    @Test
    public void parseOptionalAttendanceDate_nullMultimap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOptionalAttendanceDate(null));
    }

    @Test
    public void parseOptionalAttendanceDate_absentDate_returnsEmptyOptional() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1", PREFIX_DATE);
        assertEquals(Optional.empty(), ParserUtil.parseOptionalAttendanceDate(arguments));
    }

    @Test
    public void parseOptionalAttendanceDate_validDate_returnsAttendanceDate() throws Exception {
        for (String date : List.of("01-01-0001", "31-12-9999", "29-02-2028", VALID_ATTENDANCE_DATE)) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1 d/" + date, PREFIX_DATE);
            assertEquals(Optional.of(new AttendanceDate(date)), ParserUtil.parseOptionalAttendanceDate(arguments));
        }
    }

    @Test
    public void parseOptionalAttendanceDate_validDateWithWhitespace_returnsTrimmedAttendanceDate() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1 d/ " + VALID_ATTENDANCE_DATE + " ", PREFIX_DATE);
        assertEquals(Optional.of(new AttendanceDate(VALID_ATTENDANCE_DATE)),
                ParserUtil.parseOptionalAttendanceDate(arguments));
    }

    @Test
    public void parseOptionalAttendanceDate_invalidDate_throwsParseException() {
        for (String date : List.of("00-01-0000", "", "7-10-2026", "32-01-2026", "29-02-2026")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/" + date, PREFIX_DATE);
            assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                    -> ParserUtil.parseOptionalAttendanceDate(arguments));
        }
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_absentDate_returnsEmptyOptional() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1", PREFIX_DATE);
        assertEquals(Optional.empty(), ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_today_returnsDate() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/15-06-2026", PREFIX_DATE);
        assertEquals(Optional.of(TODAY), ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_dayBeforeToday_returnsDate() throws Exception {
        AttendanceDate expectedDate = new AttendanceDate("14-06-2026");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/14-06-2026", PREFIX_DATE);
        assertEquals(Optional.of(expectedDate),
                ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_earlierYearLaterDayAndMonth_returnsDate() throws Exception {
        AttendanceDate expectedDate = new AttendanceDate("31-12-2025");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/31-12-2025", PREFIX_DATE);
        assertEquals(Optional.of(expectedDate),
                ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_dayAfterToday_throwsParseException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/16-06-2026", PREFIX_DATE);
        assertThrows(ParseException.class,
                "Attendance date 16-06-2026 is in the future. Use today or an earlier date.", ()
                -> ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_nextYearEarlierDayAndMonth_throwsParseException() {
        AttendanceDate today = new AttendanceDate("31-12-2026");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/01-01-2027", PREFIX_DATE);
        assertThrows(ParseException.class,
                "Attendance date 01-01-2027 is in the future. Use today or an earlier date.", ()
                -> ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, today));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_invalidDate_throwsParseException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/32-01-2099", PREFIX_DATE);
        assertThrows(ParseException.class, AttendanceDate.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, TODAY));
    }

    @Test
    public void parseOptionalPastOrPresentAttendanceDate_nullArguments_throwsNullPointerException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" d/15-06-2026", PREFIX_DATE);
        assertThrows(NullPointerException.class, ()
                -> ParserUtil.parseOptionalPastOrPresentAttendanceDate(null, TODAY));
        assertThrows(NullPointerException.class, ()
                -> ParserUtil.parseOptionalPastOrPresentAttendanceDate(arguments, null));
    }

    @Test
    public void parseClassCode_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClassCode(null));
    }

    @Test
    public void parseClassCode_validValue_returnsClassCode() throws Exception {
        for (String classCode : List.of("CS1010-T1", "ABC1234XY-ABCDE", VALID_CLASS_CODE)) {
            assertEquals(new ClassCode(classCode), ParserUtil.parseClassCode(classCode));
        }
    }

    @Test
    public void parseClassCode_lowerCaseWithWhitespace_returnsTrimmedClassCode() throws Exception {
        assertEquals(new ClassCode(VALID_CLASS_CODE), ParserUtil.parseClassCode("  cs2103t-t10  "));
    }

    @Test
    public void parseClassCode_invalidValue_throwsParseException() {
        for (String classCode : List.of("", "CS2103T", "CS2103T-", "-T10", "CS2103T-T10-X",
                "C2103-T1", "CS2103T-ABCDEF", "CS2103T T10")) {
            assertThrows(ParseException.class, ClassCode.MESSAGE_CONSTRAINTS, ()
                    -> ParserUtil.parseClassCode(classCode));
        }
    }

    @Test
    public void parseOptionalClassCode_nullMultimap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseOptionalClassCode(null));
    }

    @Test
    public void parseOptionalClassCode_absentClassCode_returnsEmptyOptional() throws Exception {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1", PREFIX_CLASS_CODE);
        assertEquals(Optional.empty(), ParserUtil.parseOptionalClassCode(arguments));
    }

    @Test
    public void parseOptionalClassCode_validClassCode_returnsClassCode() throws Exception {
        for (String input : List.of(" 1 c/CS2103T-T10", " 1 c/ CS2103T-T10 ")) {
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(input, PREFIX_CLASS_CODE);
            assertEquals(Optional.of(new ClassCode(VALID_CLASS_CODE)), ParserUtil.parseOptionalClassCode(arguments));
        }
    }

    @Test
    public void parseOptionalClassCode_emptyClassCode_throwsParseException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" 1 c/", PREFIX_CLASS_CODE);
        assertThrows(ParseException.class, ClassCode.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseOptionalClassCode(arguments));
    }

    @Test
    public void parseMatriculationNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMatriculationNumber((String) null));
    }

    @Test
    public void parseMatriculationNumber_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseMatriculationNumber(INVALID_MATRICULATION_NUMBER));
    }

    @Test
    public void parseMatriculationNumber_validValueWithoutWhitespace_returnsMatriculationNumber() throws Exception {
        MatriculationNumber expectedMatriculationNumber = new MatriculationNumber(VALID_MATRICULATION_NUMBER);
        assertEquals(expectedMatriculationNumber, ParserUtil.parseMatriculationNumber(VALID_MATRICULATION_NUMBER));
    }

    @Test
    public void parseMatriculationNumber_validValueWithWhitespace_returnsTrimmedMatriculationNumber() throws Exception {
        String matriculationNumberWithWhitespace = WHITESPACE + VALID_MATRICULATION_NUMBER + WHITESPACE;
        MatriculationNumber expectedMatriculationNumber = new MatriculationNumber(VALID_MATRICULATION_NUMBER);
        assertEquals(expectedMatriculationNumber,
                ParserUtil.parseMatriculationNumber(matriculationNumberWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
