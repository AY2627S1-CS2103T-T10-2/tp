package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.MatriculationNumber;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validMatriculationNumber_returnsDeleteCommand() {
        MatriculationNumber matriculationNumber = new MatriculationNumber("A1234567X");
        assertParseSuccess(parser, " m/A1234567X", new DeleteCommand(matriculationNumber));
    }

    @Test
    public void parse_lowercaseMatriculationNumber_returnsDeleteCommand() {
        MatriculationNumber matriculationNumber = new MatriculationNumber("A1234567X");
        assertParseSuccess(parser, " m/a1234567x", new DeleteCommand(matriculationNumber));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidMatriculationNumber_throwsParseException() {
        assertParseFailure(parser, " m/invalid", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A123", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_indexAndMatriculationNumber_throwsParseException() {
        assertParseFailure(parser, "1 m/A1234567X",
                String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS,
                        DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateMatriculationPrefix_throwsParseException() {
        assertParseFailure(parser, " m/A1234567X m/A7654321Z",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
    }
}
