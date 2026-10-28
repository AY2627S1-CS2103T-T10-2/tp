package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String matriculationNumber;
    private final String email;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedAttendanceRecord> attendance = new ArrayList<>();
    private final List<JsonAdaptedClassCode> classCodes = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, List<JsonAdaptedTag> tags) {
        this(name, phone, email, tags, null);
    }

    /**
     * Constructs an adapted person with attendance records and no class codes.
     */
    public JsonAdaptedPerson(String name, String matriculationNumber, String email,
            List<JsonAdaptedTag> tags, List<JsonAdaptedAttendanceRecord> attendance) {
        this(name, matriculationNumber, email, tags, attendance, null);
    }

    /**
     * Constructs an adapted person, treating missing attendance or class codes as empty for older files.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("matriculationNumber") String matriculationNumber,
            @JsonProperty("email") String email,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("attendance") List<JsonAdaptedAttendanceRecord> attendance,
            @JsonProperty("classCodes") List<JsonAdaptedClassCode> classCodes) {
        this.name = name;
        this.matriculationNumber = matriculationNumber;
        this.email = email;
        if (attendance != null) {
            this.attendance.addAll(attendance);
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (classCodes != null) {
            this.classCodes.addAll(classCodes);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        matriculationNumber = source.getMatriculationNumber().value;
        email = source.getEmail().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        attendance.addAll(source.getAttendance().getRecords().stream()
                .map(JsonAdaptedAttendanceRecord::new)
                .collect(Collectors.toList()));
        classCodes.addAll(source.getClassCodes().stream()
                .map(JsonAdaptedClassCode::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        final List<ClassCode> personClassCodes = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException("Student tags must not be null.");
            }
            personTags.add(tag.toModelType());
        }
        for (JsonAdaptedClassCode classCode : classCodes) {
            if (classCode == null) {
                throw new IllegalValueException("Student class codes must not be null.");
            }
            personClassCodes.add(classCode.toModelType());
        }
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (matriculationNumber == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, MatriculationNumber.class.getSimpleName()));
        }
        if (!MatriculationNumber.isValidMatriculationNumber(matriculationNumber)) {
            throw new IllegalValueException(MatriculationNumber.MESSAGE_CONSTRAINTS);
        }
        final MatriculationNumber modelMatriculationNumber = new MatriculationNumber(matriculationNumber);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        final Set<ClassCode> modelClassCodes = new HashSet<>(personClassCodes);
        final List<AttendanceRecord> modelRecords = new ArrayList<>();
        final Set<AttendanceRecord> uniqueRecords = new HashSet<>();
        for (JsonAdaptedAttendanceRecord record : attendance) {
            if (record == null) {
                throw new IllegalValueException("Student attendance records must not be null.");
            }
            AttendanceRecord modelRecord = record.toModelType();
            if (!uniqueRecords.add(modelRecord)) {
                throw new IllegalValueException(Attendance.MESSAGE_DUPLICATE_RECORD);
            }
            modelRecords.add(modelRecord);
        }
        final Attendance modelAttendance = new Attendance(modelRecords);
        return new Person(modelName, modelMatriculationNumber, modelEmail, modelTags, modelAttendance, modelClassCodes);
    }

}
