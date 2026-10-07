package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AttendanceDate;

/**
 * Contains tests for {@code AttendanceCommand}.
 */
public class AttendanceCommandTest {

    private static final AttendanceDate ATTENDANCE_DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_withoutDate_returnsPlaceholder() {
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.empty());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model, "Attendance recording is not implemented yet. Index: 1", expectedModel);
    }

    @Test
    public void execute_withDate_returnsPlaceholder() {
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, Optional.of(ATTENDANCE_DATE));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                "Attendance recording is not implemented yet. Index: 1, date: 07-10-2026", expectedModel);
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
