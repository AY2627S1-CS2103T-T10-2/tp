package seedu.address.model.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    /**
     * Returns the six sample students with class enrollment and session histories.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            createSamplePerson(new Name("Alex Yeoh"), new MatriculationNumber("A1234567X"),
                    new Email("alexyeoh@u.nus.edu"), getTagSet("friends"), "CS2100-T01"),
            createSamplePerson(new Name("Bernice Yu"), new MatriculationNumber("A2345678Y"),
                    new Email("berniceyu@u.nus.edu"), getTagSet("colleagues", "friends"), "CS2100-T01"),
            createSamplePerson(new Name("Charlotte Oliveiro"), new MatriculationNumber("A3456789Z"),
                    new Email("charlotte@u.nus.edu"), getTagSet("neighbours"), "CS2100-T01"),
            createSamplePerson(new Name("David Li"), new MatriculationNumber("A4567890W"),
                    new Email("lidavid@u.nus.edu"), getTagSet("family"), "CS2040S-T02"),
            createSamplePerson(new Name("Irfan Ibrahim"), new MatriculationNumber("A5678901V"),
                    new Email("irfan@u.nus.edu"), getTagSet("classmates"), "CS2040S-T02"),
            createSamplePerson(new Name("Roy Balakrishnan"), new MatriculationNumber("A6789012U"),
                    new Email("royb@u.nus.edu"), getTagSet("colleagues"), "CS2040S-T02")
        };
    }

    private static Person createSamplePerson(Name name, MatriculationNumber matriculationNumber, Email email,
            Set<Tag> tags, String classCode) {
        ClassCode enrolledClass = new ClassCode(classCode);
        Attendance attendance = new Attendance(List.of(
                new AttendanceRecord(new AttendanceDate("01-09-2026"), enrolledClass),
                new AttendanceRecord(new AttendanceDate("08-09-2026"), enrolledClass)));
        ParticipationHistory participation = new ParticipationHistory(List.of(
                new ParticipationRecord(enrolledClass, LocalDate.of(2026, 9, 8), new ParticipationPoints("2"))));
        return new Person(name, matriculationNumber, email, tags, attendance, Set.of(enrolledClass), participation);
    }

    /**
     * Returns sample students and their shared assignments, including marked and unmarked work.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
            ClassCode enrolledClass = samplePerson.getClassCodes().iterator().next();
            AssignmentId assignmentId = new AssignmentId(enrolledClass.getModuleCode() + ":QUIZ1");
            if (sampleAb.getGradebook().findAssignment(assignmentId).isEmpty()) {
                sampleAb.addAssignment(new Assignment(assignmentId, "Week 4 Quiz", new BigDecimal("20")));
            }
            sampleAb.assignStudent(samplePerson.getMatriculationNumber(), assignmentId);
            if (!samplePerson.getMatriculationNumber().equals(new MatriculationNumber("A6789012U"))) {
                sampleAb.recordMark(samplePerson.getMatriculationNumber(), assignmentId,
                        new Mark(new BigDecimal("17.5")));
            }
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a class code set containing the list of strings given.
     */
    public static Set<ClassCode> getClassCodeSet(String... strings) {
        return Arrays.stream(strings)
                .map(ClassCode::new)
                .collect(Collectors.toSet());
    }

}
