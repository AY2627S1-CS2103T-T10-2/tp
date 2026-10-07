package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AttendanceTest {
    private final AttendanceDate earlier = new AttendanceDate("31-12-2025");
    private final AttendanceDate later = new AttendanceDate("01-01-2026");

    @Test
    public void constructor_mutableInput_copiesAndSortsDates() {
        List<AttendanceDate> dates = new ArrayList<>(List.of(earlier, later));
        Attendance attendance = new Attendance(dates);
        dates.clear();
        assertEquals(List.of(later, earlier), attendance.getDates());
        assertThrows(UnsupportedOperationException.class, () -> attendance.getDates().clear());
    }

    @Test
    public void constructor_duplicateDates_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Attendance.MESSAGE_DUPLICATE_DATE, () ->
                new Attendance(List.of(earlier, new AttendanceDate("31-12-2025"))));
    }

    @Test
    public void nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Attendance(null));
        assertThrows(NullPointerException.class, () -> new Attendance(Arrays.asList(earlier, null)));
        assertThrows(NullPointerException.class, () -> Attendance.empty().withDate(null));
        assertThrows(NullPointerException.class, () -> Attendance.empty().contains(null));
    }

    @Test
    public void withDate_newDate_returnsUpdatedHistoryWithoutChangingOriginal() {
        Attendance original = Attendance.empty();
        Attendance updated = original.withDate(earlier);
        assertTrue(original.getDates().isEmpty());
        assertFalse(original.contains(earlier));
        assertTrue(updated.contains(earlier));
        assertEquals(List.of(later, earlier), updated.withDate(later).getDates());
    }

    @Test
    public void withDate_duplicateDate_throwsIllegalArgumentException() {
        Attendance attendance = Attendance.empty().withDate(earlier);
        assertThrows(IllegalArgumentException.class, Attendance.MESSAGE_DUPLICATE_DATE, () ->
                attendance.withDate(new AttendanceDate("31-12-2025")));
        assertEquals(List.of(earlier), attendance.getDates());
    }

    @Test
    public void equalsAndHashCode_ignoreInputOrder() {
        Attendance attendance = new Attendance(List.of(earlier, later));
        Attendance reordered = new Attendance(List.of(later, earlier));
        assertEquals(attendance, reordered);
        assertEquals(attendance.hashCode(), reordered.hashCode());
        assertFalse(attendance.equals(Attendance.empty()));
        assertFalse(attendance.equals(null));
        assertFalse(attendance.equals(List.of(earlier, later)));
    }
}
