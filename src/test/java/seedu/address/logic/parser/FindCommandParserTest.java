package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.MatriculationNumberMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // No leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // Multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validMatriculationNumber_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("A1234567X")));

        assertParseSuccess(parser, "m/A1234567X", expectedFindCommand);
        assertParseSuccess(parser, " m/a1234567x", expectedFindCommand);
        assertParseSuccess(parser, " \n m/ \t A1234567X \t", expectedFindCommand);
    }

    @Test
    public void parse_invalidMatriculationNumber_throwsParseException() {
        assertParseFailure(parser, "m/", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "m/invalid", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "m/A123", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "m/A1234567X extra", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateMatriculationNumberPrefix_throwsParseException() {
        String expectedMessage =
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER);

        assertParseFailure(parser, "m/A1234567X m/A7654321Z", expectedMessage);
        assertParseFailure(parser, "m/A1234567X\tm/A7654321Z", expectedMessage);
    }

    @Test
    public void parse_nameAndMatriculationNumber_throwsParseException() {
        String expectedMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "Alice m/A1234567X", expectedMessage);
        assertParseFailure(parser, "Alice\tm/A1234567X", expectedMessage);
    }

    @Test
    public void parse_matriculationNumberWithoutPrefix_returnsNameFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("A1234567X")));

        assertParseSuccess(parser, "A1234567X", expectedFindCommand);
    }
}
