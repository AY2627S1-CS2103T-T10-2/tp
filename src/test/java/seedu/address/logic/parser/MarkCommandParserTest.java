package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ASSIGNMENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

public class MarkCommandParserTest {
    private final MarkCommandParser parser = new MarkCommandParser();
    private final MatriculationNumber student = new MatriculationNumber("A1234567X");
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final String required = " m/A1234567X id/CS2103T:A1 mark/17.5";

    @Test
    public void parse_validMark_success() {
        MarkCommand expected = new MarkCommand(student, id, new Mark(new BigDecimal("17.5")));
        assertParseSuccess(parser, required, expected);
        assertParseSuccess(parser, " mark/17.5 id/cs2103t:a1 m/a1234567x", expected);
        assertParseSuccess(parser, " m/A1234567X id/CS2103T:A1 mark/0",
                new MarkCommand(student, id, new Mark(BigDecimal.ZERO)));
    }

    @Test
    public void parse_missingFieldOrPreamble_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", usage);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1", usage);
        assertParseFailure(parser, " m/A1234567X mark/17.5", usage);
        assertParseFailure(parser, " id/CS2103T:A1 mark/17.5", usage);
        assertParseFailure(parser, "extra" + required, usage);
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
        assertParseFailure(parser, " m/A1234567X id/bad mark/17.5", AssignmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/",
                MarkCommandParser.MESSAGE_INVALID_MARK);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/letter",
                MarkCommandParser.MESSAGE_INVALID_MARK);
        assertParseFailure(parser, " m/A1234567X id/CS2103T:A1 mark/-1", Mark.MESSAGE_CONSTRAINTS);
    }
}
