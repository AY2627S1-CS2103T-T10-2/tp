package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.classcode.ClassCode;

public class AttendanceTest {
    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");
    private static final AttendanceDate DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private final AttendanceRecord earlier =
            new AttendanceRecord(new AttendanceDate("31-12-2025"), CLASS_T10);
    private final AttendanceRecord later =
            new AttendanceRecord(new AttendanceDate("01-01-2026"), CLASS_T10);

    @Test
    public void constructor_mutableInput_copiesAndSortsDates() {
        List<AttendanceRecord> dates = new ArrayList<>(List.of(earlier, later));
        Attendance attendance = new Attendance(dates);
        dates.clear();
        assertEquals(List.of(later, earlier), attendance.getRecords());
        assertThrows(UnsupportedOperationException.class, () -> attendance.getRecords().clear());
    }

    @Test
    public void constructor_duplicateDates_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Attendance.MESSAGE_DUPLICATE_RECORD, () ->
                new Attendance(List.of(earlier, new AttendanceRecord(new AttendanceDate("31-12-2025"), CLASS_T10))));
    }

    @Test
    public void nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Attendance(null));
        assertThrows(NullPointerException.class, () -> new Attendance(Arrays.asList(earlier, null)));
        assertThrows(NullPointerException.class, () -> Attendance.empty().withRecord(null));
        assertThrows(NullPointerException.class, () -> Attendance.empty().withoutRecord(null));
        assertThrows(NullPointerException.class, () -> Attendance.empty().contains(null));
    }

    @Test
    public void withRecord_newDate_returnsUpdatedHistoryWithoutChangingOriginal() {
        Attendance original = Attendance.empty();
        Attendance updated = original.withRecord(earlier);
        assertTrue(original.getRecords().isEmpty());
        assertFalse(original.contains(earlier));
        assertTrue(updated.contains(earlier));
        assertEquals(List.of(later, earlier), updated.withRecord(later).getRecords());
    }

    @Test
    public void withRecord_duplicateDate_throwsIllegalArgumentException() {
        Attendance attendance = Attendance.empty().withRecord(earlier);
        assertThrows(IllegalArgumentException.class, Attendance.MESSAGE_DUPLICATE_RECORD, () ->
                attendance.withRecord(new AttendanceRecord(new AttendanceDate("31-12-2025"), CLASS_T10)));
        assertEquals(List.of(earlier), attendance.getRecords());
    }

    @Test
    public void withoutRecord_recordedDate_returnsUpdatedHistoryWithoutChangingOriginal() {
        Attendance original = new Attendance(List.of(later, earlier));
        Attendance updated = original.withoutRecord(earlier);
        assertEquals(List.of(later), updated.getRecords());
        assertEquals(List.of(later, earlier), original.getRecords());
    }

    @Test
    public void withoutRecord_onlyDate_returnsEmptyHistory() {
        Attendance attendance = new Attendance(List.of(earlier));
        Attendance updated = attendance.withoutRecord(earlier);
        assertTrue(updated.getRecords().isEmpty());
        assertEquals(Attendance.empty(), updated);
    }

    @Test
    public void withoutRecord_unrecordedDate_throwsIllegalArgumentException() {
        Attendance attendance = new Attendance(List.of(earlier));
        assertThrows(IllegalArgumentException.class,
                "Attendance for this class on this date has not been recorded.", () ->
                        attendance.withoutRecord(later));
        assertEquals(List.of(earlier), attendance.getRecords());
    }

    @Test
    public void withoutRecord_emptyHistory_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                "Attendance for this class on this date has not been recorded.", () ->
                        Attendance.empty().withoutRecord(earlier));
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

    @Test
    public void emptyHistory_hasNoRecords() {
        Attendance attendance = Attendance.empty();
        assertTrue(attendance.getRecords().isEmpty());
        assertTrue(attendance.getRecordsOn(DATE).isEmpty());
    }

    @Test
    public void withRecord_sameDateOtherClass_keepsBothWithoutChangingOriginal() {
        AttendanceRecord first = new AttendanceRecord(DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(DATE, CLASS_T11);
        Attendance original = new Attendance(List.of(first));
        Attendance updated = original.withRecord(second);

        assertEquals(List.of(first), original.getRecords());
        assertFalse(original.contains(second));
        assertTrue(updated.contains(second));
        assertEquals(List.of(first, second), updated.getRecords());
    }

    @Test
    public void withoutRecord_sameDateOtherClass_removesOnlyMatchingRecord() {
        AttendanceRecord first = new AttendanceRecord(DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(DATE, CLASS_T11);
        Attendance original = new Attendance(List.of(second, first));

        assertEquals(List.of(second), original.withoutRecord(first).getRecords());
        assertEquals(List.of(first, second), original.getRecords());
    }

    @Test
    public void withoutRecord_sameDateOtherClassMissing_throwsIllegalArgumentException() {
        AttendanceRecord first = new AttendanceRecord(DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(DATE, CLASS_T11);
        Attendance attendance = new Attendance(List.of(first));

        assertThrows(IllegalArgumentException.class, Attendance.MESSAGE_RECORD_NOT_FOUND, () ->
                attendance.withoutRecord(second));
        assertEquals(List.of(first), attendance.getRecords());
    }

    @Test
    public void getRecords_multipleClasses_ordersNewestDateThenClass() {
        AttendanceRecord first = new AttendanceRecord(DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(DATE, CLASS_T11);
        AttendanceRecord newest = new AttendanceRecord(OTHER_DATE, CLASS_T10);
        Attendance attendance = new Attendance(List.of(second, first, newest));

        assertEquals(List.of(newest, first, second), attendance.getRecords());
        assertThrows(UnsupportedOperationException.class, () -> attendance.getRecords().clear());
        assertEquals(List.of(first, second), attendance.getRecordsOn(DATE));
        assertEquals(List.of(newest), attendance.getRecordsOn(OTHER_DATE));
        assertTrue(attendance.getRecordsOn(new AttendanceDate("09-10-2026")).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> attendance.getRecordsOn(DATE).clear());
        assertThrows(NullPointerException.class, () -> attendance.getRecordsOn(null));
    }

    @Test
    public void equals_sameDateDifferentClass_returnsFalse() {
        Attendance first = new Attendance(List.of(new AttendanceRecord(DATE, CLASS_T10)));
        Attendance second = new Attendance(List.of(new AttendanceRecord(DATE, CLASS_T11)));
        assertFalse(first.equals(second));
    }

}
