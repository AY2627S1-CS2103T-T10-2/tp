package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_emptyArgument_returnsListCommand()
            throws ParseException {
        assertInstanceOf(ListCommand.class, parser.parse(""));
    }

    @Test
    public void parse_whitespaceArgument_returnsListCommand()
            throws ParseException {
        assertInstanceOf(ListCommand.class, parser.parse("   "));
    }

    @Test
    public void parse_nonEmptyArgument_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                ListCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "abc", expectedMessage);
    }
}
