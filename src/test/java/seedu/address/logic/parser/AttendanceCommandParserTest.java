package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.commands.MarkClassAttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.MatriculationNumber;

/**
 * Contains tests for {@code AttendanceCommandParser}.
 */
public class AttendanceCommandParserTest {

    private static final String MESSAGE_MISSING_IDENTIFIER =
            String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, MarkAttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_MULTIPLE_IDENTIFIERS =
            String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, MarkAttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNMARK_MISSING_IDENTIFIER =
            String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, UnmarkAttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNMARK_MULTIPLE_IDENTIFIERS =
            String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, UnmarkAttendanceCommand.MESSAGE_USAGE);
    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");

    private static final AttendanceDate VALID_DATE = new AttendanceDate("07-10-2026");
    private static final StudentIdentifier VALID_MATRICULATION_IDENTIFIER =
            new StudentIdentifier(new MatriculationNumber("A1234567X"));

    private static final AttendanceDate TODAY = new AttendanceDate("15-06-2026");
    private static final AttendanceDate YESTERDAY = new AttendanceDate("14-06-2026");
    private static final AttendanceDate TOMORROW = new AttendanceDate("16-06-2026");

    private AttendanceCommandParser parser = new AttendanceCommandParser();
    private final AttendanceCommandParser fixedDateParser = new AttendanceCommandParser(TODAY);

    @Test
    public void parse_markByIndexToday_returnsCommand() {
        assertParseSuccess(fixedDateParser, "1 c/CS2103T-T10 d/15-06-2026",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(TODAY)));
    }

    @Test
    public void parse_markByIndexYesterday_returnsCommand() {
        assertParseSuccess(fixedDateParser, "1 c/CS2103T-T10 d/14-06-2026",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(YESTERDAY)));
    }

    @Test
    public void parse_markByIndexTomorrow_throwsParseException() {
        assertParseFailure(fixedDateParser, "1 c/CS2103T-T10 d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_markByMatriculationNumberToday_returnsCommand() {
        assertParseSuccess(fixedDateParser, " m/A1234567X c/CS2103T-T10 d/15-06-2026",
                new MarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, CLASS_T10, Optional.of(TODAY)));
    }

    @Test
    public void parse_markByMatriculationNumberTomorrow_throwsParseException() {
        assertParseFailure(fixedDateParser, " m/A1234567X c/CS2103T-T10 d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_markWholeClassToday_returnsCommand() {
        assertParseSuccess(fixedDateParser, " c/CS2103T-T10 d/15-06-2026",
                new MarkClassAttendanceCommand(CLASS_T10, Optional.of(TODAY)));
    }

    @Test
    public void parse_markWholeClassTomorrow_throwsParseException() {
        assertParseFailure(fixedDateParser, " c/CS2103T-T10 d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_unmarkByIndexToday_returnsCommand() {
        assertParseSuccess(fixedDateParser, "unmark 1 d/15-06-2026",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                        Optional.empty(), Optional.of(TODAY)));
    }

    @Test
    public void parse_unmarkByIndexTomorrow_throwsParseException() {
        assertParseFailure(fixedDateParser, "unmark 1 d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_unmarkByMatriculationNumberTomorrow_throwsParseException() {
        assertParseFailure(fixedDateParser, "unmark m/A1234567X d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_unmarkTomorrowWithClass_throwsParseException() {
        assertParseFailure(fixedDateParser, "unmark 1 c/CS2103T-T10 d/16-06-2026",
                String.format(ParserUtil.MESSAGE_FUTURE_ATTENDANCE_DATE, TOMORROW));
    }

    @Test
    public void parse_dateOmitted_returnsCommandWithEmptyDate() {
        assertParseSuccess(fixedDateParser, "1 c/CS2103T-T10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.empty()));
        assertParseSuccess(fixedDateParser, " c/CS2103T-T10",
                new MarkClassAttendanceCommand(CLASS_T10, Optional.empty()));
        assertParseSuccess(fixedDateParser, "unmark 1",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                        Optional.empty(), Optional.empty()));
    }

    @Test
    public void parse_markFutureDateWithoutClass_reportsMissingClass() {
        assertParseFailure(fixedDateParser, "1 d/16-06-2026",
                String.format(AttendanceCommandParser.MESSAGE_MISSING_CLASS_CODE, MarkAttendanceCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateFutureDates_reportsDuplicatePrefix() {
        assertParseFailure(fixedDateParser, "1 c/CS2103T-T10 d/16-06-2026 d/17-06-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_invalidDate_reportsDateConstraints() {
        assertParseFailure(fixedDateParser, "1 c/CS2103T-T10 d/32-01-2099", AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unmarkValidIndex_returnsUnmarkAttendanceCommand() {
        assertParseSuccess(parser, "unmark 1",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty(),
                Optional.empty()));
    }

    @Test
    public void parse_unmarkValidMatriculationNumberAndDate_returnsUnmarkAttendanceCommand() {
        assertParseSuccess(parser, "unmark m/A1234567X d/07-10-2026",
                new UnmarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, Optional.empty(), Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_unmarkMissingIdentifier_throwsMissingIdentifierError() {
        assertParseFailure(parser, "unmark", MESSAGE_UNMARK_MISSING_IDENTIFIER);
    }

    @Test
    public void parse_unmarkBothIdentifiers_throwsMultipleIdentifiersError() {
        assertParseFailure(parser, "unmark 1 m/A1234567X", MESSAGE_UNMARK_MULTIPLE_IDENTIFIERS);
    }

    @Test
    public void parse_unmarkInvalidIndex_throwsIndexError() {
        assertParseFailure(parser, "unmark 1 extra", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_unmarkInvalidDate_throwsDateConstraints() {
        assertParseFailure(parser, "unmark 1 d/32-01-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unmarkDuplicateDate_throwsDuplicatePrefixError() {
        assertParseFailure(parser, "unmark 1 d/07-10-2026 d/08-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_unmarkCaseAndWhitespaceVariations_returnsUnmarkAttendanceCommand() {
        UnmarkAttendanceCommand expectedCommand =
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty(),
                Optional.empty());
        assertParseSuccess(parser, "UNMARK 1", expectedCommand);
        assertParseSuccess(parser, "Unmark 1", expectedCommand);
        assertParseSuccess(parser, " unmark   1 ", expectedCommand);
    }

    @Test
    public void parse_notUnmarkSubcommand_throwsIndexError() {
        assertParseFailure(parser, "unmarkx 1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "un mark 1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "1 unmark", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_validArgs_returnsMarkAttendanceCommand() {
        assertParseSuccess(parser, "1 c/CS2103T-T10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.empty()));
        assertParseSuccess(parser, "1 d/07-10-2026 c/CS2103T-T10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, " 1   d/07-10-2026  c/CS2103T-T10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_validMatriculationNumber_returnsMarkAttendanceCommand() {
        assertParseSuccess(parser, " m/A1234567X c/CS2103T-T10",
                new MarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, CLASS_T10, Optional.empty()));
        assertParseSuccess(parser, " m/a1234567x d/07-10-2026 c/CS2103T-T10",
                new MarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, CLASS_T10, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, " d/07-10-2026 m/A1234567X c/CS2103T-T10",
                new MarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, CLASS_T10, Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_invalidIndex_throwsSpecificError() {
        assertParseFailure(parser, "", MESSAGE_MISSING_IDENTIFIER);
        assertParseFailure(parser, "a", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "0", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "d/07-10-2026", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "1 extra d/07-10-2026", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_dateOnly_throwsMissingIdentifierError() {
        assertParseFailure(parser, " d/07-10-2026", MESSAGE_MISSING_IDENTIFIER);
    }

    @Test
    public void parse_bothIdentifiers_throwsMultipleIdentifiersError() {
        assertParseFailure(parser, "1 m/A1234567X", MESSAGE_MULTIPLE_IDENTIFIERS);
    }

    @Test
    public void parse_invalidMatriculationNumber_throwsMatriculationNumberConstraints() {
        assertParseFailure(parser, " m/A123", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidDate_throwsDateConstraints() {
        assertParseFailure(parser, "1 c/CS2103T-T10 d/", AttendanceDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 c/CS2103T-T10 d/7-10-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 c/CS2103T-T10 d/32-01-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateDate_throwsDuplicatePrefixError() {
        assertParseFailure(parser, "1 d/07-10-2026 d/08-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_duplicateMatriculationNumber_throwsDuplicatePrefixError() {
        assertParseFailure(parser, " m/A1234567X m/A1234567Y",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
    }

    @Test
    public void parse_duplicateMatriculationNumberAndDate_throwsDuplicatePrefixError() {
        assertParseFailure(parser, " m/A1234567X m/A1234567Y d/07-10-2026 d/08-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER, PREFIX_DATE));
    }

    @Test
    public void parse_duplicateDateAndBothIdentifiers_throwsDuplicatePrefixError() {
        assertParseFailure(parser, "1 d/07-10-2026 d/08-10-2026 m/A1234567X",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_invalidIdentifierAndDate_throwsIdentifierError() {
        assertParseFailure(parser, "a d/bad", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " m/bad d/bad", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_markMissingClass_throwsMissingClassError() {
        String expectedMessage = "Specify the class with c/MODULE-CLASS.\n" + MarkAttendanceCommand.MESSAGE_USAGE;
        assertParseFailure(parser, "1", expectedMessage);
        assertParseFailure(parser, "1 d/07-10-2026", expectedMessage);
        assertParseFailure(parser, " m/A1234567X", expectedMessage);
        assertParseFailure(parser, "1 d/bad", expectedMessage);
    }

    @Test
    public void parse_markInvalidClass_throwsClassConstraints() {
        for (String classCode : new String[] {"", "CS2103T", "CS2103T-T10-X", "C2103-T1", "bad"}) {
            assertParseFailure(parser, "1 c/" + classCode, ClassCode.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "1 c/bad d/bad", ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_markClassWithoutIdentifier_throwsIdentifierError() {
        assertParseFailure(parser, "a c/bad", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " m/bad c/bad d/bad", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_wholeClass_returnsMarkClassAttendanceCommand() {
        MarkClassAttendanceCommand expectedCommand = new MarkClassAttendanceCommand(CLASS_T10, Optional.empty());
        assertParseSuccess(parser, " c/CS2103T-T10", expectedCommand);
        assertParseSuccess(parser, "   c/CS2103T-T10   ", expectedCommand);
    }

    @Test
    public void parse_wholeClassWithDate_returnsMarkClassAttendanceCommand() {
        MarkClassAttendanceCommand expectedCommand =
                new MarkClassAttendanceCommand(CLASS_T10, Optional.of(VALID_DATE));
        assertParseSuccess(parser, " c/cs2103t-t10 d/07-10-2026", expectedCommand);
        assertParseSuccess(parser, " d/07-10-2026 c/CS2103T-T10", expectedCommand);
    }

    @Test
    public void parse_wholeClassInvalidClass_throwsClassConstraintsBeforeDate() {
        assertParseFailure(parser, " c/", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " c/CS2103T", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " c/bad d/bad", ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_wholeClassInvalidDate_throwsDateConstraints() {
        assertParseFailure(parser, " c/CS2103T-T10 d/32-01-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_wholeClassDuplicatePrefixes_throwsDuplicatePrefixError() {
        assertParseFailure(parser, " c/CS2103T-T10 c/CS2103T-T11",
                "Multiple values specified for the following single-valued field(s): c/");
        assertParseFailure(parser, " c/CS2103T-T10 d/07-10-2026 d/08-10-2026",
                "Multiple values specified for the following single-valued field(s): d/");
    }

    @Test
    public void parse_unmarkWholeClass_throwsMissingIdentifierError() {
        assertParseFailure(parser, "unmark c/CS2103T-T10", MESSAGE_UNMARK_MISSING_IDENTIFIER);
    }

    @Test
    public void messageUsage_wholeClassForm_isIncluded() {
        assertTrue(MarkAttendanceCommand.MESSAGE_USAGE.contains("c/MODULE-CLASS [d/DATE] for a whole class"));
    }

    @Test
    public void parse_markDuplicateClass_throwsDuplicatePrefixErrorFirst() {
        String expectedMessage = "Multiple values specified for the following single-valued field(s): c/";
        assertParseFailure(parser, "1 c/CS2103T-T10 c/CS2103T-T11", expectedMessage);
        assertParseFailure(parser, "a c/bad c/bad d/bad", expectedMessage);
    }

    @Test
    public void parse_markLowercaseClassAndPrefixOrder_returnsMarkAttendanceCommand() {
        assertParseSuccess(parser, "1 c/cs2103t-t10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.empty()));
        assertParseSuccess(parser, "1 c/CS2103T-T10 d/07-10-2026",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, "1 d/07-10-2026 c/CS2103T-T10",
                new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_unmarkExplicitClass_returnsUnmarkAttendanceCommand() {
        assertParseSuccess(parser, "unmark 1 c/CS2103T-T10 d/07-10-2026",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                        Optional.of(CLASS_T10), Optional.of(VALID_DATE)));
        assertParseSuccess(parser, "UNMARK m/A1234567X c/CS2103T-T10",
                new UnmarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER,
                        Optional.of(CLASS_T10), Optional.empty()));
        assertParseSuccess(parser, "unmark 1 d/07-10-2026 c/cs2103t-t11",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                        Optional.of(CLASS_T11), Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_unmarkInvalidClass_throwsClassConstraintsBeforeDate() {
        assertParseFailure(parser, "unmark 1 c/bad", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "unmark 1 c/", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "unmark 1 c/bad d/bad", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "unmark a c/bad d/bad", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_unmarkDuplicateClass_throwsDuplicatePrefixErrorFirst() {
        assertParseFailure(parser, "unmark 1 c/X c/Y",
                "Multiple values specified for the following single-valued field(s): c/");
        assertParseFailure(parser, "unmark a c/X c/Y d/bad",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CLASS_CODE));
    }

}
