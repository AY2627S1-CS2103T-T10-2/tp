package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemoveAssignmentCommand;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.person.MatriculationNumber;

public class RemoveAssignmentCommandParserTest {
    private final RemoveAssignmentCommandParser parser = new RemoveAssignmentCommandParser();

    @Test
    public void parse_validForms_success() {
        AssignmentId id = new AssignmentId("CS2103T:A1");
        MatriculationNumber student = new MatriculationNumber("A1234567X");
        assertParseSuccess(parser, " id/cs2103t:a1", new RemoveAssignmentCommand(id));
        assertParseSuccess(parser, " m/A1234567X id/CS2103T:A1", new RemoveAssignmentCommand(id, student));
    }

    @Test
    public void parse_missingAndInvalidValues_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveAssignmentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", usage);
        assertParseFailure(parser, " m/A1234567X", usage);
        assertParseFailure(parser, "extra id/CS2103T:A1", usage);
        assertParseFailure(parser, " id/bad", AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " id/CS2103T:A1 m/bad", MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, " id/CS2103T:A1 id/CS2103T:A2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ASSIGNMENT_ID));
        assertParseFailure(parser, " id/CS2103T:A1 m/A1234567X m/A2345678Y",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MATRICULATION_NUMBER));
    }
}
