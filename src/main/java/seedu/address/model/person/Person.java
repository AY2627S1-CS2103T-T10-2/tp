package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final MatriculationNumber matriculationNumber;
    private final Email email;

    // Data fields
    private final Attendance attendance;
    private final ParticipationHistory participationHistory;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<ClassCode> classCodes = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, MatriculationNumber matriculationNumber, Email email, Set<Tag> tags) {
        this(name, matriculationNumber, email, tags, Attendance.empty());
    }

    /**
     * Constructs a person with the given attendance history.
     * Every field must be non-null.
     */
    public Person(Name name, MatriculationNumber matriculationNumber, Email email, Set<Tag> tags,
                  Attendance attendance) {
        this(name, matriculationNumber, email, tags, attendance, new HashSet<>());
    }

    /**
     * Constructs a person with attendance and enrolled classes, but no participation records.
     * Every field must be non-null.
     */
    public Person(Name name, MatriculationNumber matriculationNumber, Email email, Set<Tag> tags,
                  Attendance attendance, Set<ClassCode> classCodes) {
        this(name, matriculationNumber, email, tags, attendance, classCodes, ParticipationHistory.empty());
    }

    /**
     * Constructs a person with attendance, enrolled classes and participation history.
     * Every field must be non-null.
     */
    public Person(Name name, MatriculationNumber matriculationNumber, Email email, Set<Tag> tags,
                  Attendance attendance, Set<ClassCode> classCodes, ParticipationHistory participationHistory) {
        requireAllNonNull(name, matriculationNumber, email, tags, attendance, classCodes, participationHistory);
        this.name = name;
        this.matriculationNumber = matriculationNumber;
        this.email = email;
        this.attendance = attendance;
        this.participationHistory = participationHistory;
        this.tags.addAll(tags);
        this.classCodes.addAll(classCodes);
    }

    public Name getName() {
        return name;
    }

    public MatriculationNumber getMatriculationNumber() {
        return matriculationNumber;
    }

    public Email getEmail() {
        return email;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    /**
     * Returns this student's immutable participation history.
     */
    public ParticipationHistory getParticipationHistory() {
        return participationHistory;
    }

    /**
     * Returns a copy of this student with the given participation history.
     * All other student information is preserved.
     */
    public Person withParticipationHistory(ParticipationHistory updatedHistory) {
        requireNonNull(updatedHistory);
        return new Person(name, matriculationNumber, email, tags,
                attendance, classCodes, updatedHistory);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable class code set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<ClassCode> getClassCodes() {
        return Collections.unmodifiableSet(classCodes);
    }

    /**
     * Checks if a {@link Person} is enrolled in the class.
     *
     * @return True if the {@link Person} is enrolled in the class specified by {@link ClassCode}
     */
    public boolean isEnrolledIn(ClassCode classCode) {
        requireNonNull(classCode);
        return classCodes.contains(classCode);
    }

    /**
     * Checks if a {@link Person} is enrolled in any class.
     *
     * @return True if the {@link Person} is enrolled in any class
     */
    public boolean isEnrolled() {
        return !classCodes.isEmpty();
    }

    /**
     * Creates a copy of the Person without the specified classCode.
     * Participation history and all other student information are preserved.
     * Preconditions: the Person is enrolled in the given class.
     *
     * @param classCode The specified class to unenroll from
     */
    public Person withoutClassCode(ClassCode classCode) {
        Set<ClassCode> updatedClassCodes = new HashSet<>(classCodes);

        assert updatedClassCodes.contains(classCode);

        updatedClassCodes.remove(classCode);
        return new Person(name, matriculationNumber, email, tags,
                attendance, updatedClassCodes, participationHistory);
    }

    /**
     * Creates a copy of the Person with the specified classCode.
     * Participation history and all other student information are preserved.
     *
     * @param classCode The specified class to enroll in
     */
    public Person withClassCode(ClassCode classCode) {
        requireNonNull(classCode);
        Set<ClassCode> updatedClassCodes = new HashSet<>(classCodes);
        updatedClassCodes.add(classCode);
        return new Person(name, matriculationNumber, email, tags,
                attendance, updatedClassCodes, participationHistory);
    }

    /**
     * Creates a copy of the Person without any classes.
     * Participation history and all other student information are preserved.
     * Preconditions: the Person is enrolled in at least one class.
     */
    public Person withoutClassCodes() {
        assert !classCodes.isEmpty();

        return new Person(name, matriculationNumber, email, tags,
                attendance, Collections.emptySet(), participationHistory);
    }

    /**
     * Returns true if both persons have the same matriculation number.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getMatriculationNumber().equals(getMatriculationNumber());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && matriculationNumber.equals(otherPerson.matriculationNumber)
                && email.equals(otherPerson.email)
                && tags.equals(otherPerson.tags)
                && attendance.equals(otherPerson.attendance)
                && classCodes.equals(otherPerson.classCodes)
                && participationHistory.equals(otherPerson.participationHistory);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, matriculationNumber, email, tags,
                attendance, classCodes, participationHistory);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("matriculationNumber", matriculationNumber)
                .add("email", email)
                .add("tags", tags)
                .add("attendance", attendance)
                .add("classCodes", classCodes)
                .add("participationHistory", participationHistory)
                .toString();
    }
}
