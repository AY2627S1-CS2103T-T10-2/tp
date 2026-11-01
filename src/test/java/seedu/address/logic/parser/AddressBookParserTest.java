package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_CODE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AssignmentCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EnrollCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.commands.MarkClassAttendanceCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.MarksCommand;
import seedu.address.logic.commands.RemoveAssignmentCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.commands.UnenrollCommand;
import seedu.address.logic.commands.UnmarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonInModuleOrClassPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_attendanceFutureDate_throwsParseException() {
        assertThrows(ParseException.class,
                "Attendance date 28-10-2099 is in the future. Use today or an earlier date.", ()
                -> parser.parseCommand("attendance 1 c/CS2103T-T10 d/28-10-2099"));
    }

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_assignment() throws Exception {
        MatriculationNumber student = new MatriculationNumber("A1234567X");
        AssignmentId id = new AssignmentId("CS2103T:A1");
        Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
        assertEquals(new AssignmentCommand(student, assignment), parser.parseCommand(
                "assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20"));
        assertEquals(new AssignmentCommand(student, id), parser.parseCommand(
                "assignment m/A1234567X id/CS2103T:A1"));
    }

    @Test
    public void parseCommand_mark() throws Exception {
        assertEquals(new MarkCommand(new MatriculationNumber("A1234567X"), new AssignmentId("CS2103T:A1"),
                new Mark(new BigDecimal("17.5"))), parser.parseCommand(
                "mark m/A1234567X id/CS2103T:A1 mark/17.5"));
    }

    @Test
    public void parseCommand_marks() throws Exception {
        assertEquals(new MarksCommand(new AssignmentId("CS2103T:A1")),
                parser.parseCommand("marks id/CS2103T:A1"));
    }

    @Test
    public void parseCommand_removeAssignment() throws Exception {
        AssignmentId id = new AssignmentId("CS2103T:A1");
        assertEquals(new RemoveAssignmentCommand(id), parser.parseCommand("removeassignment id/CS2103T:A1"));
        assertEquals(new RemoveAssignmentCommand(id, new MatriculationNumber("A1234567X")),
                parser.parseCommand("removeassignment id/CS2103T:A1 m/A1234567X"));
    }

    @Test
    public void parseCommand_attendance() throws Exception {
        assertEquals(new MarkAttendanceCommand(INDEX_FIRST_PERSON, new ClassCode("CS2103T-T10"), Optional.empty()),
                parser.parseCommand("attendance 1 c/CS2103T-T10"));
        assertEquals(new MarkAttendanceCommand(INDEX_FIRST_PERSON, new ClassCode("CS2103T-T10"),
                Optional.of(new AttendanceDate("07-10-2026"))),
                parser.parseCommand("attendance 1 c/CS2103T-T10 d/07-10-2026"));
        assertEquals(new MarkAttendanceCommand(new StudentIdentifier(new MatriculationNumber("A1234567X")),
                new ClassCode("CS2103T-T10"),
                Optional.empty()),
                parser.parseCommand("attendance m/A1234567X c/CS2103T-T10"));
        assertEquals(new MarkClassAttendanceCommand(new ClassCode("CS2103T-T10"),
                Optional.of(new AttendanceDate("07-10-2026"))),
                parser.parseCommand("attendance c/CS2103T-T10 d/07-10-2026"));
    }

    @Test
    public void parseCommand_unmarkAttendance() throws Exception {
        assertEquals(new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty(),
                Optional.empty()),
                parser.parseCommand("attendance unmark 1"));
        assertEquals(new UnmarkAttendanceCommand(new StudentIdentifier(new MatriculationNumber("A1234567X")),
                Optional.of(new ClassCode("CS2103T-T10")), Optional.of(new AttendanceDate("07-10-2026"))),
                parser.parseCommand("attendance unmark m/A1234567X c/CS2103T-T10 d/07-10-2026"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("Attendance unmark 1"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);

        assertThrows(ParseException.class, () ->
                parser.parseCommand(ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_sort() throws Exception {
        assertTrue(parser.parseCommand(SortCommand.COMMAND_WORD) instanceof SortCommand);
        assertThrows(ParseException.class, () -> parser.parseCommand(SortCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_enroll() throws Exception {
        EnrollCommand command = (EnrollCommand) parser.parseCommand(EnrollCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10);
        assertEquals(new EnrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), command);
    }

    @Test
    public void parseCommand_unenroll() throws Exception {
        UnenrollCommand command = (UnenrollCommand) parser.parseCommand(UnenrollCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_CLASS_CODE + VALID_CLASS_CODE_CS2103T_T10);
        assertEquals(new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), command);
    }

    @Test
    public void parseCommand_unenrollAll() throws Exception {
        UnenrollCommand command = (UnenrollCommand) parser.parseCommand(UnenrollCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_ALL);
        assertEquals(new UnenrollCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        Set<ClassCode> classCodes = Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                new ClassCode(VALID_CLASS_CODE_CS2103T_T10));
        Set<ModuleCode> moduleCodes = Set.of(new ModuleCode("CS2103T"),
                new ModuleCode("CS3230"));
        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD + " " + String.join(" ", VALID_CLASS_CODE_CS3230_T01,
                        VALID_CLASS_CODE_CS2103T_T10, "CS2103T", "CS3230"));
        assertEquals(new FilterCommand(new PersonInModuleOrClassPredicate(classCodes, moduleCodes)), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
