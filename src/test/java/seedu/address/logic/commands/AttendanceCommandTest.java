package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains tests for {@code AttendanceCommand}.
 */
public class AttendanceCommandTest {

    private static final AttendanceDate ATTENDANCE_DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexWithDate_success() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person markedStudent = new PersonBuilder(student).withAttendance(ATTENDANCE_DATE.toString()).build();
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));

        String expectedMessage = String.format(AttendanceCommand.MESSAGE_SUCCESS, student.getName(), ATTENDANCE_DATE);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexWithoutDate_marksToday() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        AttendanceDate attendanceDate = AttendanceDate.today();
        Person markedStudent = new PersonBuilder(student).withAttendance(attendanceDate.toString()).build();
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.empty());

        String expectedMessage = String.format(AttendanceCommand.MESSAGE_SUCCESS, student.getName(), attendanceDate);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingDifferentDate_keepsBothDates() {
        Person originalStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person student = new PersonBuilder(originalStudent).withAttendance(OTHER_DATE.toString()).build();
        model.setPerson(originalStudent, student);
        Person markedStudent = new PersonBuilder(student)
                .withAttendance(OTHER_DATE.toString(), ATTENDANCE_DATE.toString()).build();
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));

        String expectedMessage = String.format(AttendanceCommand.MESSAGE_SUCCESS, student.getName(), ATTENDANCE_DATE);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person markedStudent = new PersonBuilder(student).withAttendance(ATTENDANCE_DATE.toString()).build();
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));

        String expectedMessage = String.format(AttendanceCommand.MESSAGE_SUCCESS, student.getName(), ATTENDANCE_DATE);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AttendanceCommand command = new AttendanceCommand(outOfBoundIndex, Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        AttendanceCommand command = new AttendanceCommand(INDEX_SECOND_PERSON, Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_dateAlreadyRecorded_throwsCommandException() {
        Person originalStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person student = new PersonBuilder(originalStudent).withAttendance(ATTENDANCE_DATE.toString()).build();
        model.setPerson(originalStudent, student);
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));

        String expectedMessage = String.format(AttendanceCommand.MESSAGE_DUPLICATE_DATE,
                student.getName(), ATTENDANCE_DATE);

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() {
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AttendanceCommand(INDEX_SECOND_PERSON, Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(OTHER_DATE))));
        assertFalse(command.equals(new AttendanceCommand(INDEX_FIRST_PERSON, Optional.empty())));
    }

    @Test
    public void toStringMethod() {
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));
        String expected = AttendanceCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_PERSON + ", date=" + Optional.of(ATTENDANCE_DATE) + "}";
        assertEquals(expected, command.toString());
    }
}
