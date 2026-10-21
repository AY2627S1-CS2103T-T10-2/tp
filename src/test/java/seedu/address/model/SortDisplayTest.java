package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class SortDisplayTest {

    @Test
    public void detailFor_none_returnsEmpty() {
        assertTrue(SortDisplay.NONE.detailFor(ALICE).isEmpty());
    }

    @Test
    public void detailFor_attendance_formatsRecordedDateCount() {
        Person attendedAlice = new PersonBuilder(ALICE).withAttendance("01-10-2026", "02-10-2026").build();

        assertEquals("Attendance dates: 2", SortDisplay.ATTENDANCE.detailFor(attendedAlice).orElseThrow());
        assertEquals("Attendance dates: 0", SortDisplay.ATTENDANCE.detailFor(ALICE).orElseThrow());
    }
}
