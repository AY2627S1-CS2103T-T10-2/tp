package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.classcode.ClassCode;

public class AttendanceRecordTest {
    private static final AttendanceDate DATE = new AttendanceDate("07-10-2026");
    private static final ClassCode CLASS_CODE = new ClassCode("CS2103T-T10");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AttendanceRecord(null, CLASS_CODE));
        assertThrows(NullPointerException.class, () -> new AttendanceRecord(DATE, null));
    }

    @Test
    public void constructor_validFields_preservesValues() {
        AttendanceRecord record = new AttendanceRecord(DATE, CLASS_CODE);
        assertEquals(DATE, record.getDate());
        assertEquals(CLASS_CODE, record.getClassCode());
    }

    @Test
    public void compareTo_differentDates_ordersByDateBeforeClassCode() {
        AttendanceRecord earlier = new AttendanceRecord(new AttendanceDate("01-10-2026"),
                new ClassCode("CS2103T-T11"));
        AttendanceRecord later = new AttendanceRecord(new AttendanceDate("02-10-2026"), CLASS_CODE);
        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
    }

    @Test
    public void compareTo_sameDate_ordersByClassCode() {
        AttendanceRecord first = new AttendanceRecord(DATE, CLASS_CODE);
        AttendanceRecord second = new AttendanceRecord(DATE, new ClassCode("CS2103T-T11"));
        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(new AttendanceRecord(DATE, CLASS_CODE)));
        assertEquals(0, first.compareTo(new AttendanceRecord(DATE, new ClassCode("cs2103t-t10"))));
    }

    @Test
    public void equalsAndHashCode() {
        AttendanceRecord record = new AttendanceRecord(DATE, CLASS_CODE);
        AttendanceRecord copy = new AttendanceRecord(new AttendanceDate("07-10-2026"),
                new ClassCode("CS2103T-T10"));
        AttendanceRecord lowerCase = new AttendanceRecord(DATE, new ClassCode("cs2103t-t10"));
        assertEquals(record, record);
        assertEquals(record, copy);
        assertEquals(record.hashCode(), copy.hashCode());
        assertEquals(record, lowerCase);
        assertEquals(record.hashCode(), lowerCase.hashCode());
        assertFalse(record.equals(null));
        assertFalse(record.equals("07-10-2026 CS2103T-T10"));
        assertFalse(record.equals(new AttendanceRecord(new AttendanceDate("06-10-2026"), CLASS_CODE)));
        assertFalse(record.equals(new AttendanceRecord(DATE, new ClassCode("CS2103T-T11"))));
    }

    @Test
    public void toString_returnsDateAndClassCode() {
        assertEquals("07-10-2026 CS2103T-T10", new AttendanceRecord(DATE, CLASS_CODE).toString());
    }
}
