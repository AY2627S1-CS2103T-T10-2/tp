package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.PersonContainsClassCodePredicate;

public class FilterCommandParserTest {

    private FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() {
        // no leading and trailing whitespaces
        FilterCommand expectedFilterCommand =
                new FilterCommand(new PersonContainsClassCodePredicate(Set.of(
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10), new ClassCode(VALID_CLASS_CODE_CS3230_T01))));
        assertParseSuccess(parser, VALID_CLASS_CODE_CS2103T_T10 + " " + VALID_CLASS_CODE_CS3230_T01,
                expectedFilterCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n " + VALID_CLASS_CODE_CS2103T_T10 + "\n \t " + VALID_CLASS_CODE_CS3230_T01
                + "  \t", expectedFilterCommand);
    }

}
