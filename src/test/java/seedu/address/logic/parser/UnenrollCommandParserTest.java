package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRICULATION_NUMBER_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnenrollCommand;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.MatriculationNumber;

public class UnenrollCommandParserTest {
    private static final String INVALID_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            UnenrollCommand.MESSAGE_USAGE);
    private static final String MISSING_IDENTIFIER = String.format(
            ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, UnenrollCommand.MESSAGE_USAGE);
    private static final String MULTIPLE_IDENTIFIERS = String.format(
            ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS, UnenrollCommand.MESSAGE_USAGE);
    private static final String MATRIC_ARG = " " + PREFIX_MATRICULATION_NUMBER + VALID_MATRICULATION_NUMBER_AMY;

    private final UnenrollCommandParser parser = new UnenrollCommandParser();
    private final StudentIdentifier matricIdentifier =
            new StudentIdentifier(new MatriculationNumber(VALID_MATRICULATION_NUMBER_AMY));

    @Test
    public void parse_validArgsWithIndex_returnsUnenrollCommand() {
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        // lower case input is accepted and normalised
        assertParseSuccess(parser, " 1 " + PREFIX_CLASS_CODE + "cs2103t-t10",
                new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        // all is accepted
        assertParseSuccess(parser, " 1 " + PREFIX_ALL,
                new UnenrollCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validArgsWithMatriculationNumber_returnsUnenrollCommand() {
        UnenrollCommand fromClass = new UnenrollCommand(matricIdentifier,
                Optional.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        UnenrollCommand fromAll = new UnenrollCommand(matricIdentifier, Optional.empty());

        assertParseSuccess(parser, MATRIC_ARG + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                fromClass);
        assertParseSuccess(parser, MATRIC_ARG + " " + PREFIX_ALL, fromAll);
        // prefixes may appear in either order
        assertParseSuccess(parser, " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10 + MATRIC_ARG,
                fromClass);
        assertParseSuccess(parser, " " + PREFIX_ALL + MATRIC_ARG, fromAll);
    }

    @Test
    public void parse_missingIdentifier_failure() {
        assertParseFailure(parser, " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MISSING_IDENTIFIER);
        assertParseFailure(parser, " " + PREFIX_ALL, MISSING_IDENTIFIER);
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
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + "A123 " + PREFIX_ALL,
                MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " " + PREFIX_MATRICULATION_NUMBER + "invalid "
                + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10, MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_indexAndMatriculationNumber_failure() {
        assertParseFailure(parser, " 1" + MATRIC_ARG + " " + PREFIX_ALL, MULTIPLE_IDENTIFIERS);
        assertParseFailure(parser, " 1" + MATRIC_ARG + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10,
                MULTIPLE_IDENTIFIERS);
    }

    @Test
    public void parse_duplicateMatriculationNumberPrefix_failure() {
        assertParseFailure(parser, MATRIC_ARG + MATRIC_ARG + " " + PREFIX_ALL,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
    }

    @Test
    public void parse_invalidClassCode_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + "CS2103T", ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE, ClassCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, MATRIC_ARG + " " + PREFIX_CLASS_CODE + "CS2103T",
                ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_missingPrefix_failure() {
        assertParseFailure(parser, " 1 ", INVALID_FORMAT);
        assertParseFailure(parser, MATRIC_ARG, INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateClassCodePrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10
                + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS3230_T01,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CLASS_CODE));
    }

    @Test
    public void parse_duplicateAllPrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_ALL + " " + PREFIX_ALL,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ALL));
    }

    @Test
    public void parse_bothClassCodeAndAllPrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_CLASS_CODE + "CS2103T " + PREFIX_ALL, INVALID_FORMAT);
        assertParseFailure(parser, MATRIC_ARG + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10
                + " " + PREFIX_ALL, INVALID_FORMAT);
    }

    @Test
    public void parse_invalidAllPrefix_failure() {
        assertParseFailure(parser, " 1 " + PREFIX_ALL + VALID_CLASS_CODE_CS2103T_T10, INVALID_FORMAT);
        assertParseFailure(parser, MATRIC_ARG + " " + PREFIX_ALL + VALID_CLASS_CODE_CS2103T_T10,
                INVALID_FORMAT);
    }
}
