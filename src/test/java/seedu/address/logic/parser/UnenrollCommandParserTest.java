package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.UnenrollCommand;
import seedu.address.model.classcode.ClassCode;

public class UnenrollCommandParserTest {
    private static final String INVALID_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            UnenrollCommand.MESSAGE_USAGE);
    private final UnenrollCommandParser parser = new UnenrollCommandParser();

    @Test
    public void parse_validArgs_returnsUnenrollCommand() {
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        // lower case input is accepted and normalised
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + "cs2103t-t10",
                new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        //no class(es) is accepted
        assertParseSuccess(parser, " 1 ",
                new UnenrollCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, " -1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, INVALID_FORMAT);
        assertParseFailure(parser, " a " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, INVALID_FORMAT);
    }

    @Test
    public void parse_invalidClassCode_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + "CS2103T", ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateClassCodePrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS3230_T01,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CLASS_CODE));
    }
}
