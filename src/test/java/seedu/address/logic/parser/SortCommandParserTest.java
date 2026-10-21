package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.SortCommand.SortDirection.ASC;
import static seedu.address.logic.commands.SortCommand.SortDirection.DESC;
import static seedu.address.logic.commands.SortCommand.SortField.ATTENDANCE;
import static seedu.address.logic.commands.SortCommand.SortField.EMAIL;
import static seedu.address.logic.commands.SortCommand.SortField.MATRIC;
import static seedu.address.logic.commands.SortCommand.SortField.NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_emptyArgs_returnsNameAscendingSort() {
        assertParseSuccess(parser, "", new SortCommand());
        assertParseSuccess(parser, "  \t  ", new SortCommand());
    }

    @Test
    public void parse_validFieldsAndDirections_returnsSortCommand() {
        assertParseSuccess(parser, "name", new SortCommand(NAME, ASC));
        assertParseSuccess(parser, "matric desc", new SortCommand(MATRIC, DESC));
        assertParseSuccess(parser, " email \t asc ", new SortCommand(EMAIL, ASC));
        assertParseSuccess(parser, "attendance desc", new SortCommand(ATTENDANCE, DESC));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "3", expectedMessage);
        assertParseFailure(parser, "desc", expectedMessage);
        assertParseFailure(parser, "grade asc", expectedMessage);
        assertParseFailure(parser, "name sideways", expectedMessage);
        assertParseFailure(parser, "name asc extra", expectedMessage);
    }
}
