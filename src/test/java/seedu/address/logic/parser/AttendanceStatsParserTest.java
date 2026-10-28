
package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ViewAttendanceStatsCommand;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;

/**
 * Contains tests for the stats subcommand in {@code AttendanceCommandParser}.
 */
public class AttendanceStatsParserTest {

    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final AttendanceDate DATE = new AttendanceDate("07-10-2026");

    private AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_validArgs_returnsStatsCommand() {
        ViewAttendanceStatsCommand expectedCommand =
                new ViewAttendanceStatsCommand(CLASS_T10, DATE);

        assertParseSuccess(parser,
                "stats c/CS2103T-T10 d/07-10-2026", expectedCommand);

        assertParseSuccess(parser,
                "stats d/07-10-2026 c/CS2103T-T10", expectedCommand);

        assertParseSuccess(parser,
                "stats c/cs2103t-t10 d/07-10-2026", expectedCommand);
    }

    @Test
    public void parse_caseAndWhitespaceVariations_returnsStatsCommand() {
        ViewAttendanceStatsCommand expectedCommand =
                new ViewAttendanceStatsCommand(CLASS_T10, DATE);

        assertParseSuccess(parser,
                "STATS c/CS2103T-T10 d/07-10-2026", expectedCommand);

        assertParseSuccess(parser,
                "Stats c/CS2103T-T10 d/07-10-2026", expectedCommand);

        assertParseSuccess(parser,
                "  stats   c/CS2103T-T10   d/07-10-2026  ",
                expectedCommand);
    }

    @Test
    public void parse_missingClass_throwsUsageError() {
        assertParseFailure(parser,
                "stats d/07-10-2026",
                ViewAttendanceStatsCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "stats",
                ViewAttendanceStatsCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_missingDate_throwsUsageError() {
        assertParseFailure(parser,
                "stats c/CS2103T-T10",
                ViewAttendanceStatsCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_studentIdentifier_throwsUsageError() {
        assertParseFailure(parser,
                "stats 1 c/CS2103T-T10 d/07-10-2026",
                ViewAttendanceStatsCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "stats m/A1234567X c/CS2103T-T10 d/07-10-2026",
                ViewAttendanceStatsCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidClass_throwsClassConstraints() {
        assertParseFailure(parser,
                "stats c/bad d/07-10-2026",
                ClassCode.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser,
                "stats c/ d/07-10-2026",
                ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidDate_throwsDateConstraints() {
        assertParseFailure(parser,
                "stats c/CS2103T-T10 d/31-02-2026",
                AttendanceDate.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser,
                "stats c/CS2103T-T10 d/",
                AttendanceDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateClass_throwsDuplicatePrefixError() {
        assertParseFailure(parser,
                "stats c/CS2103T-T10 c/CS2103T-T11 d/07-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CLASS_CODE));
    }

    @Test
    public void parse_duplicateDate_throwsDuplicatePrefixError() {
        assertParseFailure(parser,
                "stats c/CS2103T-T10 d/07-10-2026 d/08-10-2026",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_usageMessage_containsRequiredParameters() {
        assertTrue(ViewAttendanceStatsCommand.MESSAGE_USAGE.contains("c/MODULE-CLASS d/DATE"));
    }
}
