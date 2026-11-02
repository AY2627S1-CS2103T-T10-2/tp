package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;

/**
 * Tests participation persistence and preservation during other student updates.
 */
public class ParticipationIntegrationTest {

    private static final ClassCode CLASS_CODE = new ClassCode("CS2103T-T10");
    private static final AttendanceDate FIRST_DATE = new AttendanceDate("28-10-2026");
    private static final AttendanceDate SECOND_DATE = new AttendanceDate("29-10-2026");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final ObjectMapper mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    private Person student;

    @BeforeEach
    public void setUp() {
        Person originalStudent = model.getFilteredPersonList().get(0);

        Attendance attendance = new Attendance(List.of(
                new AttendanceRecord(FIRST_DATE, CLASS_CODE)));

        ParticipationHistory history = new ParticipationHistory(List.of(
                new ParticipationRecord(CLASS_CODE, FIRST_DATE.toLocalDate(),
                        new ParticipationPoints("2")),
                new ParticipationRecord(CLASS_CODE, SECOND_DATE.toLocalDate(),
                        new ParticipationPoints("0"))));

        student = new Person(
                originalStudent.getName(),
                originalStudent.getMatriculationNumber(),
                originalStudent.getEmail(),
                originalStudent.getTags(),
                attendance,
                Set.of(CLASS_CODE),
                history);

        model.setPerson(originalStudent, student);
    }

    @Test
    public void writeRead_nonEmptyHistory_preservesStudent(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("student.json");

        mapper.writeValue(file.toFile(), new JsonAdaptedPerson(student));
        JsonAdaptedPerson adaptedPerson =
                mapper.readValue(file.toFile(), JsonAdaptedPerson.class);
        Person restoredStudent = adaptedPerson.toModelType();

        assertEquals(student, restoredStudent);

        ParticipationHistory restoredHistory = restoredStudent.getParticipationHistory();
        assertEquals(2, restoredHistory.getRecords().size());

        ParticipationRecord firstRecord =
                restoredHistory.findRecord(CLASS_CODE, FIRST_DATE.toLocalDate()).orElseThrow();
        ParticipationRecord secondRecord =
                restoredHistory.findRecord(CLASS_CODE, SECOND_DATE.toLocalDate()).orElseThrow();

        assertEquals(new ParticipationPoints("2"), firstRecord.getPoints());
        assertEquals(new ParticipationPoints("0"), secondRecord.getPoints());

        assertEquals(student.getAttendance(), restoredStudent.getAttendance());
        assertEquals(student.getClassCodes(), restoredStudent.getClassCodes());
    }

    @Test
    public void execute_editName_preservesParticipationAndOtherDetails() throws Exception {
        Command command = new AddressBookParser().parseCommand("edit 1 n/Updated Student");
        command.execute(model);

        Person editedStudent = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.isSamePerson(student))
                .findFirst()
                .orElseThrow();

        assertEquals(new Name("Updated Student"), editedStudent.getName());
        assertEquals(student.getMatriculationNumber(), editedStudent.getMatriculationNumber());
        assertEquals(student.getEmail(), editedStudent.getEmail());
        assertEquals(student.getTags(), editedStudent.getTags());
        assertEquals(student.getAttendance(), editedStudent.getAttendance());
        assertEquals(student.getClassCodes(), editedStudent.getClassCodes());
        assertEquals(student.getParticipationHistory(), editedStudent.getParticipationHistory());
    }

    @Test
    public void changeEnrollment_preservesParticipationHistory() {
        ClassCode otherClass = new ClassCode("CS2103T-T11");

        Person enrolledStudent = student.withClassCode(otherClass);
        assertEquals(Set.of(CLASS_CODE, otherClass), enrolledStudent.getClassCodes());
        assertEquals(student.getParticipationHistory(), enrolledStudent.getParticipationHistory());

        Person partlyUnenrolledStudent = enrolledStudent.withoutClassCode(CLASS_CODE);
        assertEquals(Set.of(otherClass), partlyUnenrolledStudent.getClassCodes());
        assertEquals(student.getParticipationHistory(), partlyUnenrolledStudent.getParticipationHistory());

        Person fullyUnenrolledStudent = enrolledStudent.withoutClassCodes();
        assertEquals(Set.of(), fullyUnenrolledStudent.getClassCodes());
        assertEquals(student.getParticipationHistory(), fullyUnenrolledStudent.getParticipationHistory());

        assertEquals(Set.of(CLASS_CODE), student.getClassCodes());
    }
}
