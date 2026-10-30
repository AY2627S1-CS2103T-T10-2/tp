package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;

public class JsonAdaptedAttendanceRecordTest {
    private static final String VALID_DATE = "07-10-2026";
    private static final String VALID_CLASS_CODE = "CS2103T-T10";

    @Test
    public void toModelType_futureDate_returnsRecord() throws Exception {
        AttendanceRecord expectedRecord = new AttendanceRecord(new AttendanceDate("28-10-2099"),
                new ClassCode(VALID_CLASS_CODE));
        assertEquals(expectedRecord, new JsonAdaptedAttendanceRecord("28-10-2099", VALID_CLASS_CODE).toModelType());
    }

    @Test
    public void toModelType_validRecord_returnsEqualRecord() throws Exception {
        AttendanceRecord record = new AttendanceRecord(new AttendanceDate(VALID_DATE), new ClassCode(VALID_CLASS_CODE));
        assertEquals(record, new JsonAdaptedAttendanceRecord(record).toModelType());
    }

    @Test
    public void jsonRoundTrip_validRecord_preservesFields() throws Exception {
        AttendanceRecord record = new AttendanceRecord(new AttendanceDate(VALID_DATE), new ClassCode(VALID_CLASS_CODE));
        String json = JsonUtil.toJsonString(new JsonAdaptedAttendanceRecord(record));
        assertEquals(Map.of("date", VALID_DATE, "classCode", VALID_CLASS_CODE),
                JsonUtil.fromJsonString(json, Map.class));
        assertEquals(record, JsonUtil.fromJsonString(json, JsonAdaptedAttendanceRecord.class).toModelType());
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(null, VALID_CLASS_CODE);
        assertThrows(IllegalValueException.class, "Attendance record's date field is missing!", record::toModelType);
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        String[] invalidDates = {"", "bad", "29-02-2026"};
        for (String date : invalidDates) {
            JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(date, VALID_CLASS_CODE);
            assertThrows(IllegalValueException.class, AttendanceDate.MESSAGE_CONSTRAINTS, record::toModelType);
        }
    }

    @Test
    public void toModelType_nullClassCode_throwsIllegalValueException() {
        JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(VALID_DATE, null);
        assertThrows(IllegalValueException.class, "Attendance record's classCode field is missing!",
                record::toModelType);
    }

    @Test
    public void toModelType_invalidClassCode_throwsIllegalValueException() {
        String[] invalidClassCodes = {"", "bad", "CS2103T", "CS2103T-T10-X"};
        for (String classCode : invalidClassCodes) {
            JsonAdaptedAttendanceRecord record = new JsonAdaptedAttendanceRecord(VALID_DATE, classCode);
            assertThrows(IllegalValueException.class, ClassCode.MESSAGE_CONSTRAINTS, record::toModelType);
        }
    }

    @Test
    public void toModelType_invalidDateAndClassCode_reportsDateFirst() {
        JsonAdaptedAttendanceRecord missingDate = new JsonAdaptedAttendanceRecord(null, null);
        assertThrows(IllegalValueException.class, "Attendance record's date field is missing!",
                missingDate::toModelType);
        JsonAdaptedAttendanceRecord invalidDate = new JsonAdaptedAttendanceRecord("bad", null);
        assertThrows(IllegalValueException.class, AttendanceDate.MESSAGE_CONSTRAINTS, invalidDate::toModelType);
    }

    @Test
    public void toModelType_lowerCaseClassCode_normalizesClassCode() throws Exception {
        AttendanceRecord expected = new AttendanceRecord(new AttendanceDate(VALID_DATE),
                new ClassCode(VALID_CLASS_CODE));
        assertEquals(expected, new JsonAdaptedAttendanceRecord(VALID_DATE, "cs2103t-t10").toModelType());
    }
}
