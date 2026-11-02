package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.GradebookNumberTestData.INVALID_EXPONENT_INPUTS;
import static seedu.address.testutil.GradebookNumberTestData.OVERSIZED_INPUTS;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignmentCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.person.MatriculationNumber;

public class AssignmentCommandParserTest {
    private final AssignmentCommandParser parser = new AssignmentCommandParser();
    private final MatriculationNumber student = new MatriculationNumber("A1234567X");
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final String required = " m/A1234567X id/CS2103T:A1";

    @Test
    public void parse_nonnumericIdentifier_explainsBothForms() {
        for (String identifier : new String[] {"abc", "A1234567X", "a1234567x"}) {
            String message = assertThrows(ParseException.class, () -> parser.parse(" " + identifier
                    + " id/CS2103T:A1")).getMessage();
            assertTrue(message.contains("Specify the student by either INDEX or m/MATRICULATION_NUMBER."));
            assertTrue(message.contains("INDEX refers to the current displayed list."));
            assertTrue(message.contains("assignment 1 id/CS2103T:A1 n/Assignment One max/20"));
            assertTrue(message.contains("assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20"));
        }
    }

    @Test
    public void parse_missingFields_includesWorkedExamples() {
        for (String input : new String[] {" 1", " m/A1234567X", " 1 id/CS2103T:A1 n/Assignment One"}) {
            String message = assertThrows(ParseException.class, () -> parser.parse(input)).getMessage();
            assertTrue(message.contains("assignment 1 id/CS2103T:A1 n/Assignment One max/20"));
            assertTrue(message.contains("assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20"));
            assertTrue(message.contains("\nassignment 1 id/CS2103T:A1\n"));
            assertTrue(message.contains("\nassignment m/A1234567X id/CS2103T:A1"));
        }
    }

    @Test
    public void parse_displayedIndex_successAndInvalidIndicesRejected() {
        assertParseSuccess(parser, " 1 id/CS2103T:A1",
                new AssignmentCommand(new StudentIdentifier(Index.fromOneBased(1)), id));
        assertParseSuccess(parser, " 1 id/CS2103T:A1 n/Assignment One max/20",
                new AssignmentCommand(new StudentIdentifier(Index.fromOneBased(1)),
                        new Assignment(id, "Assignment One", new BigDecimal("20"))));
        for (String input : new String[] {"0", "-1", "1.5"}) {
            assertParseFailure(parser, " " + input + " id/CS2103T:A1",
                    ParserUtil.MESSAGE_INVALID_INDEX);
        }
        assertParseFailure(parser, " 1 2 id/CS2103T:A1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " 2147483648 id/CS2103T:A1", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void parse_mistypedIdPrefix_throwsUsageError() {
        assertParseFailure(parser, " 1id/CS2103T:A1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " mat/A1234567X id/CS2103T:A1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_newAssignment_success() {
        Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20.5"));
        assertParseSuccess(parser, required + " n/Assignment One max/20.5",
                new AssignmentCommand(student, assignment));
        assertParseSuccess(parser, " max/20.5 n/Assignment One id/cs2103t:a1 m/a1234567x",
                new AssignmentCommand(student, assignment));
    }

    @Test
    public void parse_validScientificNotation_success() {
        String[][] cases = {
            {"1.7500e1", "17.5"},
            {"100e-2", "1"},
            {"1e63", "1" + "0".repeat(63)},
            {"1e-62", "0." + "0".repeat(61) + "1"}
        };
        for (String[] testCase : cases) {
            Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal(testCase[1]));
            assertParseSuccess(parser, required + " n/Assignment One max/" + testCase[0],
                    new AssignmentCommand(student, assignment));
        }
    }

    @Test
    public void parse_oversizedScientificNotation_failure() {
        for (String input : OVERSIZED_INPUTS) {
            assertParseFailure(parser, required + " n/Assignment One max/" + input,
                    Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidExponent_failure() {
        for (String input : INVALID_EXPONENT_INPUTS) {
            assertParseFailure(parser, required + " n/Assignment One max/" + input,
                    AssignmentCommandParser.MESSAGE_INVALID_MAX_MARK);
        }
    }

    @Test
    public void parse_scientificZeroMaximum_failure() {
        for (String input : new String[] {"0e2147483647", "0e-2147483647"}) {
            assertParseFailure(parser, required + " n/Assignment One max/" + input,
                    Assignment.MESSAGE_MAX_MARK_CONSTRAINTS);
        }
    }

    @Test
    public void parse_existingAssignment_success() {
        assertParseSuccess(parser, required, new AssignmentCommand(student, id));
    }

    @Test
    public void parse_missingOrExtraFields_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER,
                AssignmentCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " id/CS2103T:A1", String.format(ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER,
                AssignmentCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " m/A1234567X", usage);
        assertParseFailure(parser, required + " n/Assignment One", usage);
        assertParseFailure(parser, required + " max/20", usage);
        assertParseFailure(parser, "1" + required, String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS,
                AssignmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_repeatedField_failure() {
        assertParseFailure(parser, required + " id/CS2103T:A2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ASSIGNMENT_ID));
        assertParseFailure(parser, required + " m/A2345678Y",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
        assertParseFailure(parser, required + " n/First n/Second max/20",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, required + " n/First max/20 max/30",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MAX_MARK));
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, " m/bad id/CS2103T:A1", MatriculationNumber.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/bad", AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, required + " n/ max/20", Assignment.MESSAGE_NAME_CONSTRAINTS);
        assertParseFailure(parser, required + " n/Assignment One max/zero",
                AssignmentCommandParser.MESSAGE_INVALID_MAX_MARK);
        assertParseFailure(parser, required + " n/Assignment One max/0",
                Assignment.MESSAGE_MAX_MARK_CONSTRAINTS);
        assertParseFailure(parser, required + " n/Assignment One max/-1",
                Assignment.MESSAGE_MAX_MARK_CONSTRAINTS);
        assertParseFailure(parser, required + " n/Assignment One max/1e1000000",
                Assignment.MESSAGE_MAX_MARK_LENGTH_CONSTRAINTS);
    }
}
