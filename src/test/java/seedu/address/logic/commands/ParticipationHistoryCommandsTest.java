package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPATION_POINTS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParticipationCommandParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;

/**
 * Tests viewing and removing participation records through the parser and model.
 */
public class ParticipationHistoryCommandsTest {

    private static final ClassCode CLASS_CODE = new ClassCode("CS2103T-T10");
    private static final AttendanceDate FIRST_DATE = new AttendanceDate("28-10-2026");
    private static final AttendanceDate SECOND_DATE = new AttendanceDate("29-10-2026");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final ParticipationCommandParser parser = new ParticipationCommandParser();

    private Person student;

    @BeforeEach
    public void setUp() {
        Person originalStudent = model.getFilteredPersonList().get(0);

        // Store later date first to verify that view sorts its output.
        ParticipationHistory history = new ParticipationHistory(List.of(
                new ParticipationRecord(CLASS_CODE, SECOND_DATE.toLocalDate(),
                        new ParticipationPoints("0")),
                new ParticipationRecord(CLASS_CODE, FIRST_DATE.toLocalDate(),
                        new ParticipationPoints("2"))));

        student = originalStudent.withClassCode(CLASS_CODE).withParticipationHistory(history);
        model.setPerson(originalStudent, student);
    }

    @Test
    public void execute_view_showsSortedRecordsWithoutChangingStudentData() throws Exception {
        Command command = parser.parse(" view m/" + student.getMatriculationNumber());

        String details = CLASS_CODE + " | 28-10-2026 | 2 point(s)\n"
                + CLASS_CODE + " | 29-10-2026 | 0 point(s)";
        String expectedMessage = String.format(
                ViewParticipationCommand.MESSAGE_SUCCESS, student.getName(), 2, details);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_viewEmptyHistory_reportsNoRecords() throws Exception {
        Person studentWithoutRecords = student.withParticipationHistory(ParticipationHistory.empty());
        model.setPerson(student, studentWithoutRecords);

        Command command = parser.parse(" view 1");
        String expectedMessage = String.format(
                ViewParticipationCommand.MESSAGE_EMPTY, student.getName());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_remove_preservesOtherRecordsAndStudentDetails() throws Exception {
        Command command = parser.parse(" remove 1 c/CS2103T-T10 d/28-10-2026");
        ParticipationRecord remainingRecord = new ParticipationRecord(
                CLASS_CODE, SECOND_DATE.toLocalDate(), new ParticipationPoints("0"));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(student, student.withParticipationHistory(
                new ParticipationHistory(List.of(remainingRecord))));

        String expectedMessage = String.format(
                RemoveParticipationCommand.MESSAGE_SUCCESS, student.getName(), CLASS_CODE, FIRST_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeMissingRecord_leavesDataAndDisplayUnchanged() throws Exception {
        List<Person> studentsBefore = List.copyOf(model.getAddressBook().getPersonList());
        List<Person> displayedBefore = List.copyOf(model.getFilteredPersonList());
        Command command = parser.parse(" remove 1 c/CS2103T-T10 d/30-10-2026");

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(RemoveParticipationCommand.MESSAGE_RECORD_NOT_FOUND,
                student.getName(), CLASS_CODE, "30-10-2026"), exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
        assertEquals(displayedBefore, model.getFilteredPersonList());
    }

    @Test
    public void execute_removeAfterUnenrollment_allowsHistoricalCorrection() throws Exception {
        Person unenrolledStudent = student.withoutClassCodes();
        model.setPerson(student, unenrolledStudent);

        Command command = parser.parse(" remove m/" + student.getMatriculationNumber()
                + " c/CS2103T-T10 d/28-10-2026");

        ParticipationRecord remainingRecord = new ParticipationRecord(
                CLASS_CODE, SECOND_DATE.toLocalDate(), new ParticipationPoints("0"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(unenrolledStudent, unenrolledStudent.withParticipationHistory(
                new ParticipationHistory(List.of(remainingRecord))));

        String expectedMessage = String.format(
                RemoveParticipationCommand.MESSAGE_SUCCESS, student.getName(), CLASS_CODE, FIRST_DATE);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void parse_pointsOnViewOrRemove_rejected() {
        assertParseFailure(parser, " view 1 p/2",
                String.format(ParticipationCommandParser.MESSAGE_UNEXPECTED_PREFIX,
                        PREFIX_PARTICIPATION_POINTS, ViewParticipationCommand.MESSAGE_USAGE));

        assertParseFailure(parser, " remove 1 c/CS2103T-T10 d/28-10-2026 p/2",
                String.format(ParticipationCommandParser.MESSAGE_UNEXPECTED_PREFIX,
                        PREFIX_PARTICIPATION_POINTS, RemoveParticipationCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_removeWithoutDate_rejected() {
        assertParseFailure(parser, " remove 1 c/CS2103T-T10",
                String.format(ParticipationCommandParser.MESSAGE_MISSING_DATE,
                        RemoveParticipationCommand.MESSAGE_USAGE));
    }
}
