package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_MATRICULATION_NUMBER = "invalid";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_CLASS_CODE = "CS2103T-";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_MATRICULATION_NUMBER = BENSON.getMatriculationNumber().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_attendanceJsonRoundTrip_preservesHistory() throws Exception {
        Person original = new PersonBuilder(BENSON).withAttendance("CS2103T-T10", "16-09-2026", "07-10-2026").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
        JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertEquals(original, restored.toModelType());
        assertEquals(List.of(new AttendanceRecord(new AttendanceDate("07-10-2026"), new ClassCode("CS2103T-T10")),
                new AttendanceRecord(new AttendanceDate("16-09-2026"), new ClassCode("CS2103T-T10"))),
                restored.toModelType().getAttendance().getRecords());
    }

    @Test
    public void toModelType_legacyJsonWithoutAttendance_defaultsToEmpty() throws Exception {
        String json = """
                {
                  "name": "Benson",
                  "matriculationNumber": "A1234567A",
                  "email": "benson@example.com",
                  "tags": []
                }
                """;
        JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertEquals(Attendance.empty(), restored.toModelType().getAttendance());
    }

    @Test
    public void toModelType_emptyAttendance_returnsEmptyHistory() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL,
                VALID_TAGS, List.of());
        assertEquals(Attendance.empty(), person.toModelType().getAttendance());
    }

    @Test
    public void toModelType_invalidAttendance_throwsIllegalValueException() {
        List<JsonAdaptedAttendanceRecord> invalidRecords = List.of(
                new JsonAdaptedAttendanceRecord("31-02-2026", "CS2103T-T10"),
                new JsonAdaptedAttendanceRecord("2026-09-16", "CS2103T-T10"));
        for (JsonAdaptedAttendanceRecord record : invalidRecords) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL,
                    VALID_TAGS, List.of(record));
            assertThrows(IllegalValueException.class, AttendanceDate.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_nullAttendanceRecord_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL,
                VALID_TAGS, Arrays.asList((JsonAdaptedAttendanceRecord) null));
        assertThrows(IllegalValueException.class, "Student attendance records must not be null.", person::toModelType);
    }

    @Test
    public void toModelType_duplicateAttendance_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL,
                VALID_TAGS, List.of(new JsonAdaptedAttendanceRecord("16-09-2026", "CS2103T-T10"),
                        new JsonAdaptedAttendanceRecord("16-09-2026", "cs2103t-t10")));
        assertThrows(IllegalValueException.class, Attendance.MESSAGE_DUPLICATE_RECORD, person::toModelType);
    }

    @Test
    public void toModelType_sameDateDifferentClassesWithoutEnrolment_loadsRecords() throws Exception {
        AttendanceRecord first = new AttendanceRecord(new AttendanceDate("07-10-2026"), new ClassCode("CS2103T-T10"));
        AttendanceRecord second = new AttendanceRecord(new AttendanceDate("07-10-2026"), new ClassCode("CS2103T-T11"));
        Person original = new PersonBuilder(BENSON).withAttendanceRecords(first, second).build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
        assertTrue(json.contains("\"date\" : \"07-10-2026\""));
        assertTrue(json.contains("\"classCode\" : \"CS2103T-T10\""));
        assertEquals(original, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(null, VALID_MATRICULATION_NUMBER, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidMatriculationNumber_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_MATRICULATION_NUMBER, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = MatriculationNumber.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullMatriculationNumber_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MatriculationNumber.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, INVALID_EMAIL, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_legacyJsonWithoutClassCodes_defaultsToEmpty() throws Exception {
        String json = """
                {
                "name": "Benson",
                "matriculationNumber": "A1234567A",
                "email": "benson@example.com",
                "tags": []
                }
                """;
        JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertTrue(restored.toModelType().getClassCodes().isEmpty());
    }

    @Test
    public void toModelType_invalidClassCode_throwsIllegalValueException() {
        List<JsonAdaptedClassCode> invalidClassCodes = new ArrayList<>();
        invalidClassCodes.add(new JsonAdaptedClassCode(INVALID_CLASS_CODE));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_MATRICULATION_NUMBER, VALID_EMAIL,
                VALID_TAGS, List.of(), invalidClassCodes);
        assertThrows(IllegalValueException.class, ClassCode.MESSAGE_CONSTRAINTS, person::toModelType);
    }

}
