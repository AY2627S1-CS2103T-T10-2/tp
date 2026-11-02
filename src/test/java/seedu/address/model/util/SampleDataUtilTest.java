package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.Person;

public class SampleDataUtilTest {
    @Test
    public void getSampleAddressBook_validAcademicRecords_preservesOriginalIdentities() {
        ReadOnlyAddressBook samples = SampleDataUtil.getSampleAddressBook();
        assertEquals(List.of("Alex Yeoh", "Bernice Yu", "Charlotte Oliveiro", "David Li", "Irfan Ibrahim",
                "Roy Balakrishnan"), samples.getPersonList().stream().map(p -> p.getName().fullName).toList());
        assertEquals(List.of("A1234567X", "A2345678Y", "A3456789Z", "A4567890W", "A5678901V", "A6789012U"),
                samples.getPersonList().stream().map(p -> p.getMatriculationNumber().toString()).toList());
        ReadOnlyGradebook gradebook = samples.getGradebook();
        assertEquals(2, gradebook.getAssignments().size());
        for (Person student : samples.getPersonList()) {
            assertTrue(student.getEmail().value.endsWith("@u.nus.edu"));
            assertFalse(student.getClassCodes().isEmpty());
            assertEquals(2, student.getAttendance().getRecords().size());
            student.getAttendance().getRecords().forEach(record -> {
                assertTrue(student.isEnrolledIn(record.getClassCode()));
                assertFalse(record.getDate().toLocalDate().isAfter(LocalDate.now()));
            });
            assertFalse(student.getParticipationHistory().isEmpty());
            student.getParticipationHistory().getRecords().forEach(record -> {
                assertTrue(student.isEnrolledIn(record.getClassCode()));
                assertFalse(record.getDate().isAfter(LocalDate.now()));
            });
            assertEquals(1, gradebook.getMarksForStudent(student.getMatriculationNumber()).size());
        }
        gradebook.getAssignments().forEach(assignment -> {
            assertEquals(3, gradebook.getStudentMarks(assignment.getId()).size());
            gradebook.getStudentMarks(assignment.getId()).forEach((matric, mark) -> {
                Person student = samples.getPersonList().stream()
                        .filter(person -> person.getMatriculationNumber().equals(matric)).findFirst().orElseThrow();
                assertTrue(student.getClassCodes().stream().anyMatch(classCode ->
                        classCode.getModuleCode().equals(assignment.getId().getModuleCode())));
                mark.ifPresent(score -> {
                    assertTrue(score.getValue().signum() >= 0);
                    assertTrue(score.getValue().compareTo(assignment.getMaxMark()) <= 0);
                });
            });
        });
        assertTrue(gradebook.getMarksForStudent(samples.getPersonList().getLast().getMatriculationNumber())
                .values().stream().anyMatch(mark -> mark.isEmpty()));
    }
}
