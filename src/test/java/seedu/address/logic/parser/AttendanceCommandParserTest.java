package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.MatriculationNumber;

/**
 * Contains tests for {@code AttendanceCommandParser}.
 */
public class AttendanceCommandParserTest {

    private static final String MESSAGE_MISSING_IDENTIFIER =
            String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, AttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_MULTIPLE_IDENTIFIERS =
            String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, AttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNMARK_MISSING_IDENTIFIER =
            String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, UnmarkAttendanceCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNMARK_MULTIPLE_IDENTIFIERS =
            String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, UnmarkAttendanceCommand.MESSAGE_USAGE);
    private static final AttendanceDate VALID_DATE = new AttendanceDate("07-10-2026");
    private static final StudentIdentifier VALID_MATRICULATION_IDENTIFIER =
            new StudentIdentifier(new MatriculationNumber("A1234567X"));

    private AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_unmarkValidIndex_returnsUnmarkAttendanceCommand() {
        assertParseSuccess(parser, "unmark 1",
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty()));
    }

    @Test
    public void parse_unmarkValidMatriculationNumberAndDate_returnsUnmarkAttendanceCommand() {
        assertParseSuccess(parser, "unmark m/A1234567X d/07-10-2026",
                new UnmarkAttendanceCommand(VALID_MATRICULATION_IDENTIFIER, Optional.of(VALID_DATE)));
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
                new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty());
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
    public void parse_validArgs_returnsAttendanceCommand() {
        assertParseSuccess(parser, "1", new AttendanceCommand(INDEX_FIRST_PERSON, Optional.empty()));
        assertParseSuccess(parser, "1 d/07-10-2026",
                new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, " 1   d/07-10-2026 ",
                new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_validMatriculationNumber_returnsAttendanceCommand() {
        assertParseSuccess(parser, " m/A1234567X",
                new AttendanceCommand(VALID_MATRICULATION_IDENTIFIER, Optional.empty()));
        assertParseSuccess(parser, " m/a1234567x d/07-10-2026",
                new AttendanceCommand(VALID_MATRICULATION_IDENTIFIER, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, " d/07-10-2026 m/A1234567X",
                new AttendanceCommand(VALID_MATRICULATION_IDENTIFIER, Optional.of(VALID_DATE)));
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
        assertParseFailure(parser, "1 d/", AttendanceDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 d/7-10-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 d/32-01-2026", AttendanceDate.MESSAGE_CONSTRAINTS);
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
}
