package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;

/**
 * Jackson-friendly version of {@link AttendanceRecord}.
 */
class JsonAdaptedAttendanceRecord {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Attendance record's %s field is missing!";

    private final String date;
    private final String classCode;

    /**
     * Constructs an adapted attendance record with the given date and class code.
     */
    @JsonCreator
    public JsonAdaptedAttendanceRecord(@JsonProperty("date") String date,
            @JsonProperty("classCode") String classCode) {
        this.date = date;
        this.classCode = classCode;
    }

    /**
     * Converts an attendance record into this class for Jackson use.
     */
    public JsonAdaptedAttendanceRecord(AttendanceRecord source) {
        this(source.getDate().toString(), source.getClassCode().toString());
    }

    /**
     * Converts the saved record to a model attendance record, validating the date before the class code.
     *
     * @throws IllegalValueException If a required field is missing or invalid.
     */
    public AttendanceRecord toModelType() throws IllegalValueException {
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        if (!AttendanceDate.isValidAttendanceDate(date)) {
            throw new IllegalValueException(AttendanceDate.MESSAGE_CONSTRAINTS);
        }
        if (classCode == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "classCode"));
        }
        if (!ClassCode.isValidClassCode(classCode)) {
            throw new IllegalValueException(ClassCode.MESSAGE_CONSTRAINTS);
        }
        return new AttendanceRecord(new AttendanceDate(date), new ClassCode(classCode));
    }
}
