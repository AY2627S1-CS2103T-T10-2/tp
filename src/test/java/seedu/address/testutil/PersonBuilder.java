package seedu.address.testutil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_MATRICULATION_NUMBER = "A1234567Z";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";

    private Name name;
    private MatriculationNumber matriculationNumber;
    private Email email;
    private Set<Tag> tags;
    private Attendance attendance;
    private Set<ClassCode> classCodes;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        matriculationNumber = new MatriculationNumber(DEFAULT_MATRICULATION_NUMBER);
        email = new Email(DEFAULT_EMAIL);
        tags = new HashSet<>();
        attendance = Attendance.empty();
        classCodes = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        matriculationNumber = personToCopy.getMatriculationNumber();
        email = personToCopy.getEmail();
        tags = new HashSet<>(personToCopy.getTags());
        attendance = personToCopy.getAttendance();
        classCodes = new HashSet<>(personToCopy.getClassCodes());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code MatriculationNumber} of the {@code Person} that we are building.
     */
    public PersonBuilder withMatriculationNumber(String matriculationNumber) {
        this.matriculationNumber = new MatriculationNumber(matriculationNumber);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the attendance history for the given class using the date strings.
     */
    public PersonBuilder withAttendance(String classCode, String... dates) {
        Attendance updatedAttendance = Attendance.empty();
        for (String date : dates) {
            updatedAttendance = updatedAttendance.withRecord(new AttendanceRecord(new AttendanceDate(date),
                    new ClassCode(classCode)));
        }
        attendance = updatedAttendance;
        return this;
    }

    /**
     * Sets the attendance history using records from any classes.
     */
    public PersonBuilder withAttendanceRecords(AttendanceRecord... records) {
        attendance = new Attendance(List.of(records));
        return this;
    }

    /**
     * Sets the class codes of the {@code Person} that we are building.
     */
    public PersonBuilder withClassCodes(String... classCodes) {
        this.classCodes = SampleDataUtil.getClassCodeSet(classCodes);
        return this;
    }

    /**
     * Builds a person with the configured details.
     */
    public Person build() {
        return new Person(name, matriculationNumber, email, tags, attendance, classCodes);
    }

}
