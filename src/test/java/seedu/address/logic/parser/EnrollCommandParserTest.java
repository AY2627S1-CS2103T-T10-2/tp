package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRICULATION_NUMBER_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EnrollCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.MatriculationNumber;

public class EnrollCommandParserTest {
    private static final String INVALID_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            EnrollCommand.MESSAGE_USAGE);
    private static final String MISSING_IDENTIFIER = String.format(
            ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, EnrollCommand.MESSAGE_USAGE);
    private static final String MULTIPLE_IDENTIFIERS = String.format(
            ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, EnrollCommand.MESSAGE_USAGE);

    private final EnrollCommandParser parser = new EnrollCommandParser();

    @Test
    public void parse_validArgsWithIndex_returnsEnrollCommand() {
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                new EnrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        // lower case input is accepted and normalised
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + "cs2103t-t10",
                new EnrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
    }

    @Test
    public void parse_validArgsWithMatriculationNumber_returnsEnrollCommand() {
        EnrollCommand expected = new EnrollCommand(
                new StudentIdentifier(new MatriculationNumber(VALID_MATRICULATION_NUMBER_AMY)),
                new ClassCode(VALID_CLASS_CODE_CS2103T_T10));

        assertParseSuccess(parser, " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, expected);
        // prefixes may appear in either order
        assertParseSuccess(parser, " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10
                + " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY, expected);
    }

    @Test
    public void parse_missingIdentifier_failure() {
        assertParseFailure(parser, " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MISSING_IDENTIFIER);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, " -1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " 0 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " a " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidMatriculationNumber_failure() {
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + "A123 "
                + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + "invalid "
                + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_indexAndMatriculationNumber_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MULTIPLE_IDENTIFIERS);
    }

    @Test
    public void parse_duplicateMatriculationNumberPrefix_failure() {
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY
                + " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
    }

    @Test
    public void parse_missingClassCodePrefix_failure() {
        assertParseFailure(parser, " 1", INVALID_FORMAT);
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY,
                INVALID_FORMAT);
    }

    @Test
    public void parse_invalidClassCode_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + "CS2103T", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY
                + " " + PREFIX_CLASS_CODE + "CS2103T", ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateClassCodePrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS3230_T01,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CLASS_CODE));
    }
}
