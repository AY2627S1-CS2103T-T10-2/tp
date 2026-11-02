package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.GradebookNumberTestData.INVALID_EXPONENT_INPUTS;
import static seedu.address.testutil.GradebookNumberTestData.OVERSIZED_INPUTS;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

public class MarkCommandParserTest {
    private final MarkCommandParser parser = new MarkCommandParser();
    private final MatriculationNumber student = new MatriculationNumber("A1234567X");
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final String required = " m/A1234567X id/CS2103T:A1 mark/17.5";

    @Test
    public void parse_nonnumericIdentifier_explainsBothForms() {
        for (String identifier : new String[] {"abc", "A1234567X", "a1234567x"}) {
            String message = assertThrows(ParseException.class, () -> parser.parse(" " + identifier
                    + " id/CS2103T:A1 mark/5")).getMessage();
            assertTrue(message.contains("Specify the student by either INDEX or m/MATRICULATION_NUMBER."));
            assertTrue(message.contains("INDEX refers to the current displayed list."));
            assertTrue(message.contains("mark 1 id/CS2103T:A1 mark/17.5"));
            assertTrue(message.contains("mark m/A1234567X id/CS2103T:A1 mark/17.5"));
        }
    }

    @Test
    public void parse_missingFields_includesWorkedExamples() {
        for (String input : new String[] {" 1 mark/5", " 1 id/CS2103T:A1"}) {
            String message = assertThrows(ParseException.class, () -> parser.parse(input)).getMessage();
            assertTrue(message.contains("mark 1 id/CS2103T:A1 mark/17.5"));
            assertTrue(message.contains("mark m/A1234567X id/CS2103T:A1 mark/17.5"));
        }
    }

    @Test
    public void parse_displayedIndex_successAndInvalidIndicesRejected() {
        assertParseSuccess(parser, " 1 id/CS2103T:A1 mark/10",
                new MarkCommand(new StudentIdentifier(Index.fromOneBased(1)), id, new Mark(BigDecimal.TEN)));
        for (String input : new String[] {"0", "-1", "1.5"}) {
            assertParseFailure(parser, " " + input + " id/CS2103T:A1 mark/10",
                    ParserUtil.MESSAGE_INVALID_INDEX);
        }
        assertParseFailure(parser, " 1 2 id/CS2103T:A1 mark/10",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " 2147483648 id/CS2103T:A1 mark/10",
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void parse_validMark_success() {
        MarkCommand expected = new MarkCommand(student, id, new Mark(new BigDecimal("17.5")));
        assertParseSuccess(parser, required, expected);
        assertParseSuccess(parser, " mark/17.5 id/cs2103t:a1 m/a1234567x", expected);
        assertParseSuccess(parser, " m/A1234567X id/CS2103T:A1 mark/0",
                new MarkCommand(student, id, new Mark(BigDecimal.ZERO)));
    }

    @Test
    public void parse_validScientificNotation_success() {
        String[][] cases = {
                {"1.7500e1", "17.5"},
                {"100e-2", "1"},
                {"1e63", "1" + "0".repeat(63)},
                {"1e-62", "0." + "0".repeat(61) + "1"},
                {"0e2147483647", "0"},
                {"0e-2147483647", "0"}
        };
        for (String[] testCase : cases) {
            assertParseSuccess(parser, " m/A1234567X id/CS2103T:A1 mark/" + testCase[0],
                    new MarkCommand(student, id, new Mark(new BigDecimal(testCase[1]))));
        }
    }

    @Test
    public void parse_oversizedScientificNotation_failure() {
        for (String input : OVERSIZED_INPUTS) {
            assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/" + input,
                    Mark.MESSAGE_LENGTH_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidExponent_failure() {
        for (String input : INVALID_EXPONENT_INPUTS) {
            assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/" + input,
                    MarkCommandParser.MESSAGE_INVALID_MARK);
        }
    }

    @Test
    public void parse_missingRequiredPrefix_failure() {
        assertMissingPrefix("", "id/");
        assertMissingPrefix(" m/A1234567X id/CS2103T:A1", "mark/");
        assertMissingPrefix(" m/A1234567X mark/17.5", "id/");
        assertMissingPrefix(" m/A1234567X", "id/");
        assertMissingPrefix(" 1 mark/17.5", "id/");
        assertMissingPrefix(" 1 id/CS2103T:A1", "mark/");
    }

    @Test
    public void parse_missingStudentIdentifier_failure() {
        assertParseFailure(parser, " id/CS2103T:A1 mark/17.5",
                String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER,
                        MarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mistypedPrefix_failure() {
        assertMissingPrefix(" m/A1234567X i/CS2103T:A1 mark/5", "id/");
        assertMissingPrefix(" m/A1234567X id/CS2103T:A1 marks/5", "mark/");
        assertParseFailure(parser, " mat/A1234567X id/CS2103T:A1 mark/5",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mistypedPrefixWithIndex_reportsMissingPrefix() {
        assertMissingPrefix(" 1 i/CS2103T:A1 mark/5", "id/");
        assertMissingPrefix(" 1id/CS2103T:A1 mark/5", "id/");
    }

    @Test
    public void parse_multipleStudentIdentifiers_failure() {
        String expectedMessage = String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS,
                MarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1" + required, expectedMessage);
        assertParseFailure(parser, "extra" + required, expectedMessage);
    }

    @Test
    public void parse_repeatedField_failure() {
        assertParseFailure(parser, required + " m/A2345678Y",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
        assertParseFailure(parser, required + " id/CS2103T:A2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ASSIGNMENT_ID));
        assertParseFailure(parser, required + " mark/18",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MARK));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " m/bad id/CS2103T:A1 mark/17.5",
                MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/ id/CS2103T:A1 mark/17.5",
                MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/ mark/17.5",
                AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A/1 mark/17.5",
                AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/5 i/6",
                MarkCommandParser.MESSAGE_INVALID_MARK);
        assertParseFailure(parser, " m/A1234567X id/bad mark/17.5",
                AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/",
                MarkCommandParser.MESSAGE_INVALID_MARK);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/letter",
                MarkCommandParser.MESSAGE_INVALID_MARK);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/-1",
                Mark.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/1e1000000",
                Mark.MESSAGE_LENGTH_CONSTRAINTS);
    }

    private void assertMissingPrefix(String input, String prefix) {
        assertParseFailure(parser, input, String.format(MarkCommandParser.MESSAGE_MISSING_REQUIRED_PREFIX,
                prefix, MarkCommand.MESSAGE_USAGE));
    }
}
