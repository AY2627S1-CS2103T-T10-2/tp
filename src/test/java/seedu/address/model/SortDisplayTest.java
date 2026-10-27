package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class SortDisplayTest {

    @Test
    public void getDetailFor_none_returnsEmpty() {
        assertTrue(SortDisplay.NONE.getDetailFor(ALICE).isEmpty());
    }

    @Test
    public void getDetailFor_attendance_formatsRecordedDateCount() {
        Person attendedAlice = new PersonBuilder(ALICE)
                .withAttendance("CS2103T-T10", "01-10-2026", "02-10-2026").build();

        assertEquals("Attendance dates: 2", SortDisplay.ATTENDANCE.getDetailFor(attendedAlice).orElseThrow());
        assertEquals("Attendance dates: 0", SortDisplay.ATTENDANCE.getDetailFor(ALICE).orElseThrow());
    }
}
