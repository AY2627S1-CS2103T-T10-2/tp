package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION_POINTS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.ParticipationCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.ParticipationPoints;

/**
 * Tests parsing of participation commands and registration in the main parser.
 */
public class ParticipationCommandParserTest {

    private static final String CLASS_AND_DATE = " c/CS2103T-T10 d/28-10-2026";

    private final ParticipationCommandParser parser = new ParticipationCommandParser();

    @Test
    public void parse_futureDate_returnsCommand() {
        ParticipationCommand expectedCommand = new ParticipationCommand(
                new StudentIdentifier(Index.fromOneBased(1)), new ClassCode("CS2103T-T10"),
                new AttendanceDate("28-10-2099"), new ParticipationPoints("2"));
        assertParseSuccess(parser, "1 c/CS2103T-T10 d/28-10-2099 p/2", expectedCommand);
    }

    @Test
    public void parse_validIndex_returnsCommand() {
        ParticipationCommand expectedCommand =
                createCommand(new StudentIdentifier(Index.fromOneBased(1)), "2");

        assertParseSuccess(parser, "1" + CLASS_AND_DATE + " p/2", expectedCommand);
        assertParseSuccess(parser, "1 p/2 d/28-10-2026 c/CS2103T-T10", expectedCommand);
        assertParseSuccess(parser, " \t1 \tc/CS2103T-T10\td/28-10-2026\tp/002 ", expectedCommand);
    }

    @Test
    public void parse_validMatriculationNumber_returnsCommand() {
        ParticipationCommand expectedCommand =
                createCommand(new StudentIdentifier(new MatriculationNumber("A1234567X")), "2");

        assertParseSuccess(parser, "m/A1234567X" + CLASS_AND_DATE + " p/2", expectedCommand);
        assertParseSuccess(parser, "c/CS2103T-T10 p/2 m/A1234567X d/28-10-2026", expectedCommand);
    }

    @Test
    public void parse_pointsAtBoundaries_returnsCommand() {
        StudentIdentifier identifier = new StudentIdentifier(Index.fromOneBased(1));

        assertParseSuccess(parser, "1" + CLASS_AND_DATE + " p/0",
                createCommand(identifier, "0"));
        assertParseSuccess(parser, "1" + CLASS_AND_DATE + " p/2147483647",
                createCommand(identifier, "2147483647"));
    }

    @Test
    public void parse_missingStudentIdentifier_throwsParseException() {
        String expectedMessage = String.format(
                ParserUtil.MESSAGE_MISSING_STUDENT_IDENTIFIER, ParticipationCommand.MESSAGE_USAGE);

        assertParseFailure(parser, CLASS_AND_DATE + " p/2", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
    }

    @Test
    public void parse_missingRequiredPrefix_throwsParseException() {
        assertParseFailure(parser, "1 d/28-10-2026 p/2",
                String.format(ParticipationCommandParser.MESSAGE_MISSING_CLASS_CODE,
                        ParticipationCommand.MESSAGE_USAGE));

        assertParseFailure(parser, "1 c/CS2103T-T10 p/2",
                String.format(ParticipationCommandParser.MESSAGE_MISSING_DATE,
                        ParticipationCommand.MESSAGE_USAGE));

        assertParseFailure(parser, "1" + CLASS_AND_DATE,
                String.format(ParticipationCommandParser.MESSAGE_MISSING_POINTS,
                        ParticipationCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIdentifier_throwsParseException() {
        assertParseFailure(parser, "0" + CLASS_AND_DATE + " p/2",
                ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "1 2" + CLASS_AND_DATE + " p/2",
                ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "m/A123" + CLASS_AND_DATE + " p/2",
                MatriculationNumber.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_bothIdentifiers_throwsParseException() {
        assertParseFailure(parser, "1 m/A1234567X" + CLASS_AND_DATE + " p/2",
                String.format(ParserUtil.MESSAGE_MULTIPLE_STUDENT_IDENTIFIERS,
                        ParticipationCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyClassCode_throwsParseException() {
        assertParseFailure(parser, "1 c/ d/28-10-2026 p/2",
                ClassCode.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        for (String date : List.of("", "31-02-2026", "29-02-2025", "2026-10-28", "28-10-0000")) {
            assertParseFailure(parser, "1 c/CS2103T-T10 d/" + date + " p/2",
                    ParticipationCommandParser.MESSAGE_INVALID_DATE);
        }
    }

    @Test
    public void parse_invalidPoints_throwsParseException() {
        for (String points : List.of("", "-1", "2.5", "two", "2147483648", "2 extra", "2 x/ignored")) {
            assertParseFailure(parser, "1" + CLASS_AND_DATE + " p/" + points,
                    ParticipationPoints.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        assertDuplicatePrefixFailure(
                "m/A1234567X m/A7654321Z" + CLASS_AND_DATE + " p/2",
                PREFIX_MATRICULATION_NUMBER);

        assertDuplicatePrefixFailure(
                "1" + CLASS_AND_DATE + " c/CS2103T-T11 p/2",
                PREFIX_CLASS_CODE);

        assertDuplicatePrefixFailure(
                "1" + CLASS_AND_DATE + " d/29-10-2026 p/2",
                PREFIX_DATE);

        assertDuplicatePrefixFailure(
                "1" + CLASS_AND_DATE + " p/2 p/3",
                PREFIX_PARTICIPATION_POINTS);
    }

    @Test
    public void parseCommand_participation_returnsParticipationCommand() throws Exception {
        ParticipationCommand expectedCommand =
                createCommand(new StudentIdentifier(Index.fromOneBased(1)), "2");

        assertEquals(expectedCommand,
                new AddressBookParser().parseCommand("participation 1" + CLASS_AND_DATE + " p/2"));
    }

    private static ParticipationCommand createCommand(StudentIdentifier identifier, String points) {
        return new ParticipationCommand(identifier, new ClassCode("CS2103T-T10"),
                new AttendanceDate("28-10-2026"), new ParticipationPoints(points));
    }

    private void assertDuplicatePrefixFailure(String input, Prefix prefix) {
        assertParseFailure(parser, input, Messages.getErrorMessageForDuplicatePrefixes(prefix));
    }
}
