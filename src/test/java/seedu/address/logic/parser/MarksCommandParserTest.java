package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarksCommand;
import seedu.address.model.gradebook.AssignmentId;

public class MarksCommandParserTest {
    private final MarksCommandParser parser = new MarksCommandParser();

    @Test
    public void parse_validId_success() {
        assertParseSuccess(parser, " id/CS2103T:A1", new MarksCommand(new AssignmentId("CS2103T:A1")));
        assertParseSuccess(parser, " id/cs2103t:a1", new MarksCommand(new AssignmentId("CS2103T:A1")));
    }

    @Test
    public void parse_missingOrExtraInput_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarksCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", usage);
        assertParseFailure(parser, " id/CS2103T:A1 extra", AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "extra id/CS2103T:A1", usage);
    }

    @Test
    public void parse_duplicateId_failure() {
        assertParseFailure(parser, " id/CS2103T:A1 id/CS2103T:A2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ASSIGNMENT_ID));
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, " id/", AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " id/bad", AssignmentId.MESSAGE_CONSTRAINTS);
    }
}
