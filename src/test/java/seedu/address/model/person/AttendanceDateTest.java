package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AttendanceDateTest {
    @Test
    public void constructor_invalidDate_throwsIllegalArgumentException() {
        String[] invalidDates = {"", " ", "1-01-2026", "01-1-2026", "2026-01-01", "01/01/2026",
            "31-02-2026", "31-04-2026", "29-02-2025", "00-01-2026", "01-13-2026", "01-01-0000",
            " 01-01-2026", "01-01-2026 ", "01-01-10000"};
        for (String date : invalidDates) {
            assertFalse(AttendanceDate.isValidAttendanceDate(date));
            assertThrows(IllegalArgumentException.class, () -> new AttendanceDate(date));
        }
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AttendanceDate(null));
        assertThrows(NullPointerException.class, () -> AttendanceDate.isValidAttendanceDate(null));
    }

    @Test
    public void constructor_validDates_preservesFormat() {
        String[] validDates = {"29-02-2024", "01-01-0001", "31-12-9999", "07-10-2026"};
        for (String date : validDates) {
            assertTrue(AttendanceDate.isValidAttendanceDate(date));
            assertEquals(date, new AttendanceDate(date).toString());
        }
    }

    @Test
    public void compareTo_differentYears_ordersChronologically() {
        AttendanceDate earlier = new AttendanceDate("31-12-2025");
        AttendanceDate later = new AttendanceDate("01-01-2026");
        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
        assertEquals(0, earlier.compareTo(new AttendanceDate("31-12-2025")));
    }

    @Test
    public void equalsAndHashCode() {
        AttendanceDate date = new AttendanceDate("07-10-2026");
        AttendanceDate copy = new AttendanceDate("07-10-2026");
        assertEquals(date, copy);
        assertEquals(date.hashCode(), copy.hashCode());
        assertFalse(date.equals(null));
        assertFalse(date.equals("07-10-2026"));
        assertFalse(date.equals(new AttendanceDate("06-10-2026")));
    }
}
