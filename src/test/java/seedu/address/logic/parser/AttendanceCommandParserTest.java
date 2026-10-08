package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.model.person.AttendanceDate;

/**
 * Contains tests for {@code AttendanceCommandParser}.
 */
public class AttendanceCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE);
    private static final AttendanceDate VALID_DATE = new AttendanceDate("07-10-2026");

    private AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_validArgs_returnsAttendanceCommand() {
        assertParseSuccess(parser, "1", new AttendanceCommand(INDEX_FIRST_PERSON, Optional.empty()));
        assertParseSuccess(parser, "1 d/07-10-2026",
                new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(VALID_DATE)));
        assertParseSuccess(parser, " 1   d/07-10-2026 ",
                new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(VALID_DATE)));
    }

    @Test
    public void parse_invalidIndex_throwsInvalidCommandFormat() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "a", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "d/07-10-2026", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 extra d/07-10-2026", MESSAGE_INVALID_FORMAT);
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
}
