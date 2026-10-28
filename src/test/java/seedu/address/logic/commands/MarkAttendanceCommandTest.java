package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains tests for {@code MarkAttendanceCommand}.
 */
public class MarkAttendanceCommandTest {
    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");

    private static final AttendanceDate ATTENDANCE_DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Person alice = new PersonBuilder(ALICE).withClassCodes("CS2103T-T10").build();
    private Person benson = new PersonBuilder(BENSON).withClassCodes("CS2103T-T10").build();

    @BeforeEach
    public void setUp() {
        model.setPerson(ALICE, alice);
        model.setPerson(BENSON, benson);
    }

    @Test
    public void execute_validIndexWithDate_success() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person markedStudent = new PersonBuilder(student)
                .withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        String expectedMessage = "Marked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026";
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexWithoutDate_marksToday() throws CommandException {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        AttendanceDate before = AttendanceDate.today();
        CommandResult result = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.empty()).execute(model);
        AttendanceDate after = AttendanceDate.today();

        Person markedStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        AttendanceDate recorded = markedStudent.getAttendance().contains(new AttendanceRecord(before, CLASS_T10))
                ? before : after;
        assertTrue(markedStudent.getAttendance().contains(new AttendanceRecord(recorded, CLASS_T10)),
                "Expected attendance on " + before + " or " + after);
        expectedModel.setPerson(student, new PersonBuilder(student)
                .withAttendance("CS2103T-T10", recorded.toString()).build());
        assertEquals(expectedModel, model);
        assertEquals(String.format(MarkAttendanceCommand.MESSAGE_SUCCESS, student.getName(), CLASS_T10, recorded),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_existingDifferentDate_keepsBothDates() {
        Person originalStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person student = new PersonBuilder(originalStudent)
                .withAttendance("CS2103T-T10", OTHER_DATE.toString()).build();
        model.setPerson(originalStudent, student);
        Person markedStudent = new PersonBuilder(student)
                .withAttendance("CS2103T-T10", OTHER_DATE.toString(), ATTENDANCE_DATE.toString()).build();
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        String expectedMessage = "Marked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026";
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person markedStudent = new PersonBuilder(student)
                .withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        String expectedMessage = "Marked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026";
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, markedStudent);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MarkAttendanceCommand command = new MarkAttendanceCommand(outOfBoundIndex, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_SECOND_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_dateAlreadyRecorded_throwsCommandException() {
        Person originalStudent = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person student = new PersonBuilder(originalStudent)
                .withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        model.setPerson(originalStudent, student);
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));
        String expectedMessage =
                "Attendance for Alice Pauline in CS2103T-T10 on 07-10-2026 has already been recorded.";

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_validMatriculationNumber_success() {
        Person markedStudent = new PersonBuilder(alice)
                .withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        MarkAttendanceCommand command = new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), CLASS_T10,
                Optional.of(ATTENDANCE_DATE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(alice, markedStudent);
        assertCommandSuccess(command, model,
                "Marked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_matriculationNumberOutsideFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person markedStudent = new PersonBuilder(benson)
                .withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        MarkAttendanceCommand command = new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567B")), CLASS_T10,
                Optional.of(ATTENDANCE_DATE));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(benson, markedStudent);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        assertCommandSuccess(command, model,
                "Marked attendance for Benson Meier in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_nonexistentMatriculationNumber_throwsCommandException() {
        MarkAttendanceCommand command = new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A9999999Z")), CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "No student with matriculation number A9999999Z found.");
    }

    @Test
    public void execute_matriculationNumberDateAlreadyRecorded_throwsCommandException() {
        Person student = new PersonBuilder(alice).withAttendance("CS2103T-T10", ATTENDANCE_DATE.toString()).build();
        model.setPerson(alice, student);
        MarkAttendanceCommand command = new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model,
                "Attendance for Alice Pauline in CS2103T-T10 on 07-10-2026 has already been recorded.");
    }

    @Test
    public void equals() {
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new MarkAttendanceCommand(INDEX_SECOND_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.of(OTHER_DATE))));
        assertFalse(command.equals(new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10, Optional.empty())));

        MarkAttendanceCommand matricCommand = new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), CLASS_T10, Optional.of(ATTENDANCE_DATE));

        assertTrue(matricCommand.equals(new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), CLASS_T10, Optional.of(ATTENDANCE_DATE))));
        assertFalse(matricCommand.equals(new MarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567B")), CLASS_T10, Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(matricCommand));
    }

    @Test
    public void toStringMethod() {
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON, CLASS_T10,
                Optional.of(ATTENDANCE_DATE));
        String expected = MarkAttendanceCommand.class.getCanonicalName()
                + "{targetStudent=" + new StudentIdentifier(INDEX_FIRST_PERSON)
                + ", classCode=CS2103T-T10, date=" + Optional.of(ATTENDANCE_DATE) + "}";
        assertEquals(expected, command.toString());
    }

    @Test
    public void execute_lowercaseClass_success() {
        Person markedStudent = new PersonBuilder(alice)
                .withAttendance(CLASS_T10.toString(), ATTENDANCE_DATE.toString()).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(alice, markedStudent);
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                new ClassCode("cs2103t-t10"), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model,
                "Marked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_otherClassRecordedOnSameDate_keepsBothRecords() {
        AttendanceRecord first = new AttendanceRecord(ATTENDANCE_DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(ATTENDANCE_DATE, CLASS_T11);
        Person student = new PersonBuilder(ALICE).withClassCodes(CLASS_T10.toString(), CLASS_T11.toString())
                .withAttendanceRecords(first).build();
        model.setPerson(alice, student);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, new PersonBuilder(student).withAttendanceRecords(first, second).build());
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                CLASS_T11, Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model,
                "Marked attendance for Alice Pauline in CS2103T-T11 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_noClasses_throwsCommandException() {
        model.setPerson(alice, ALICE);
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                CLASS_T10, Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model,
                "Alice Pauline is not enrolled in any class. Enroll them in a class first.");
    }

    @Test
    public void execute_notEnrolledInGivenClass_throwsCommandException() {
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                CLASS_T11, Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "Alice Pauline is not enrolled in CS2103T-T11.");
    }

    @Test
    public void execute_invalidIndexWithoutEnrolments_throwsIndexErrorFirst() {
        model.setPerson(alice, ALICE);
        model.setPerson(benson, BENSON);
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MarkAttendanceCommand command = new MarkAttendanceCommand(invalidIndex,
                CLASS_T10, Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "The student index provided is invalid.");
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        MarkAttendanceCommand command = new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                CLASS_T10, Optional.of(ATTENDANCE_DATE));

        assertFalse(command.equals(new MarkAttendanceCommand(INDEX_FIRST_PERSON,
                CLASS_T11, Optional.of(ATTENDANCE_DATE))));
    }
}
