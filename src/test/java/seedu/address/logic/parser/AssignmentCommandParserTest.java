package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AssignmentCommand;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.person.MatriculationNumber;

public class AssignmentCommandParserTest {
    private final AssignmentCommandParser parser = new AssignmentCommandParser();
    private final MatriculationNumber student = new MatriculationNumber("A1234567X");
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final String required = " m/A1234567X id/CS2103T:A1";

    @Test
    public void parse_newAssignment_success() {
        Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20.5"));
        assertParseSuccess(parser, required + " n/Assignment One max/20.5",
                new AssignmentCommand(student, assignment));
        assertParseSuccess(parser, " max/20.5 n/Assignment One id/cs2103t:a1 m/a1234567x",
                new AssignmentCommand(student, assignment));
    }

    @Test
    public void parse_existingAssignment_success() {
        assertParseSuccess(parser, required, new AssignmentCommand(student, id));
    }

    @Test
    public void parse_missingOrExtraFields_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", usage);
        assertParseFailure(parser, " id/CS2103T:A1", usage);
        assertParseFailure(parser, " m/A1234567X", usage);
        assertParseFailure(parser, required + " n/Assignment One", usage);
        assertParseFailure(parser, required + " max/20", usage);
        assertParseFailure(parser, "unexpected" + required, usage);
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
