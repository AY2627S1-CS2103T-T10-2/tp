package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
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
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private MatriculationNumber matriculationNumber;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Attendance attendance;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        matriculationNumber = new MatriculationNumber(DEFAULT_MATRICULATION_NUMBER);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        attendance = Attendance.empty();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        matriculationNumber = personToCopy.getMatriculationNumber();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
        attendance = personToCopy.getAttendance();
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
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
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
     * Sets the attendance history using the given date strings.
     */
    public PersonBuilder withAttendance(String... dates) {
        Attendance updatedAttendance = Attendance.empty();
        for (String date : dates) {
            updatedAttendance = updatedAttendance.withDate(new AttendanceDate(date));
        }
        attendance = updatedAttendance;
        return this;
    }

    public Person build() {
        return new Person(name, matriculationNumber, email, address, tags, attendance);
    }

}
