package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;

/**
 * Tests participation updates and preservation of other student information.
 */
public class ParticipationCommandTest {

    private static final ClassCode CLASS_CODE = new ClassCode("CS2103T-T10");
    private static final AttendanceDate SESSION_DATE = new AttendanceDate("28-10-2026");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private Person student;

    @BeforeEach
    public void setUp() {
        Person originalStudent = model.getFilteredPersonList().get(0);
        Attendance attendance = new Attendance(List.of(
                new AttendanceRecord(new AttendanceDate("27-10-2026"), CLASS_CODE)));

        student = new Person(
                originalStudent.getName(),
                originalStudent.getMatriculationNumber(),
                originalStudent.getEmail(),
                originalStudent.getTags(),
                attendance,
                Set.of(CLASS_CODE),
                ParticipationHistory.empty());

        model.setPerson(originalStudent, student);
    }

    @Test
    public void execute_sameSession_replacesPointsAndPreservesOtherDetails() throws Exception {
        setPoints("2");

        ParticipationRecord firstRecord = getCurrentStudent().getParticipationHistory()
                .findRecord(CLASS_CODE, SESSION_DATE.toLocalDate()).orElseThrow();
        assertEquals(new ParticipationPoints("2"), firstRecord.getPoints());

        setPoints("3");

        Person updatedStudent = getCurrentStudent();
        ParticipationHistory history = updatedStudent.getParticipationHistory();
        ParticipationRecord updatedRecord =
                history.findRecord(CLASS_CODE, SESSION_DATE.toLocalDate()).orElseThrow();

        assertEquals(1, history.getRecords().size());
        assertEquals(new ParticipationPoints("3"), updatedRecord.getPoints());

        assertEquals(student.getName(), updatedStudent.getName());
        assertEquals(student.getMatriculationNumber(), updatedStudent.getMatriculationNumber());
        assertEquals(student.getEmail(), updatedStudent.getEmail());
        assertEquals(student.getTags(), updatedStudent.getTags());
        assertEquals(student.getAttendance(), updatedStudent.getAttendance());
        assertEquals(student.getClassCodes(), updatedStudent.getClassCodes());

        assertTrue(student.getParticipationHistory().isEmpty());
    }

    @Test
    public void execute_notEnrolled_throwsExceptionWithoutChangingStudents() {
        List<Person> studentsBefore = List.copyOf(model.getAddressBook().getPersonList());
        ClassCode otherClass = new ClassCode("CS2103T-T11");

        ParticipationCommand command = new ParticipationCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                otherClass, SESSION_DATE, new ParticipationPoints("2"));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(
                        ParticipationCommand.MESSAGE_NOT_ENROLLED, student.getName(), otherClass),
                exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_attendanceAfterParticipation_preservesParticipation() throws Exception {
        setPoints("2");
        ParticipationHistory historyBefore = getCurrentStudent().getParticipationHistory();
        AttendanceDate attendanceDate = new AttendanceDate("29-10-2026");

        MarkAttendanceCommand command = new MarkAttendanceCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                CLASS_CODE, Optional.of(attendanceDate));
        command.execute(model);

        Person updatedStudent = getCurrentStudent();
        Attendance expectedAttendance = student.getAttendance()
                .withRecord(new AttendanceRecord(attendanceDate, CLASS_CODE));

        assertEquals(expectedAttendance, updatedStudent.getAttendance());
        assertEquals(historyBefore, updatedStudent.getParticipationHistory());
    }

    @Test
    public void execute_futureDate_throwsExceptionWithoutChangingStudents() {
        AttendanceDate futureDate = new AttendanceDate(
                LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        List<Person> studentsBefore = List.copyOf(model.getAddressBook().getPersonList());
        ParticipationHistory historyBefore = getCurrentStudent().getParticipationHistory();

        ParticipationCommand command = new ParticipationCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                CLASS_CODE, futureDate, new ParticipationPoints("2"));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(ParticipationCommand.MESSAGE_FUTURE_DATE, futureDate),
                exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
        assertEquals(historyBefore, getCurrentStudent().getParticipationHistory());
    }

    @Test
    public void execute_todayDate_recordsParticipation() throws Exception {
        AttendanceDate today = AttendanceDate.today();

        ParticipationCommand command = new ParticipationCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                CLASS_CODE, today, new ParticipationPoints("2"));

        command.execute(model);

        ParticipationRecord record = getCurrentStudent().getParticipationHistory()
                .findRecord(CLASS_CODE, today.toLocalDate()).orElseThrow();

        assertEquals(new ParticipationPoints("2"), record.getPoints());
    }

    @Test
    public void execute_pastDate_recordsParticipation() throws Exception {
        AttendanceDate pastDate = new AttendanceDate(
                LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        ParticipationCommand command = new ParticipationCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                CLASS_CODE, pastDate, new ParticipationPoints("2"));

        command.execute(model);

        ParticipationRecord record = getCurrentStudent().getParticipationHistory()
                .findRecord(CLASS_CODE, pastDate.toLocalDate()).orElseThrow();

        assertEquals(new ParticipationPoints("2"), record.getPoints());
    }

    private void setPoints(String points) throws CommandException {
        ParticipationCommand command = new ParticipationCommand(
                new StudentIdentifier(student.getMatriculationNumber()),
                CLASS_CODE, SESSION_DATE, new ParticipationPoints(points));
        command.execute(model);
    }

    private Person getCurrentStudent() {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.isSamePerson(student))
                .findFirst()
                .orElseThrow();
    }
}
