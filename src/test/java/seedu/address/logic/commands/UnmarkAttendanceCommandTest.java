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

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
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
 * Contains tests for {@code UnmarkAttendanceCommand}.
 */
public class UnmarkAttendanceCommandTest {

    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");

    private static final AttendanceDate ATTENDANCE_DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");
    private static final AttendanceDate FUTURE_DATE = new AttendanceDate("31-12-9999");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_indexOnlyRecordedDate_success() {
        Person student = recordAttendance(ALICE, ATTENDANCE_DATE.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, ALICE);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_indexOneOfSeveralDates_keepsOtherDates() {
        Person student = recordAttendance(ALICE, ATTENDANCE_DATE.toString(), OTHER_DATE.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, new PersonBuilder(ALICE)
                .withAttendance("CS2103T-T10", OTHER_DATE.toString()).build());
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_withoutDate_unmarksToday() throws CommandException {
        AttendanceDate today = AttendanceDate.today();
        Person student = recordAttendance(ALICE, today.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, ALICE);

        CommandResult result = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON), Optional.empty(),
                Optional.empty()).execute(model);

        assertEquals(expectedModel, model);
        assertEquals("Unmarked attendance for Alice Pauline in CS2103T-T10 on " + today, result.getFeedbackToUser());
    }

    @Test
    public void execute_lastIndexUnfilteredList_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person originalStudent = model.getFilteredPersonList().get(lastIndex.getZeroBased());
        Person student = recordAttendance(originalStudent, ATTENDANCE_DATE.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, originalStudent);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(lastIndex),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for George Best in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        Person student = recordAttendance(BENSON, ATTENDANCE_DATE.toString());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, BENSON);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Benson Meier in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(outOfBoundIndex),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "The student index provided is invalid.");
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_SECOND_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "The student index provided is invalid.");
    }

    @Test
    public void execute_validMatriculationNumber_success() {
        Person student = recordAttendance(ALICE, ATTENDANCE_DATE.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, ALICE);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), Optional.empty(),
                Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_matriculationNumberOutsideFilteredList_success() {
        Person student = recordAttendance(BENSON, ATTENDANCE_DATE.toString());
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, BENSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567B")), Optional.empty(),
                Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Benson Meier in CS2103T-T10 on 07-10-2026",
                expectedModel);
    }

    @Test
    public void execute_nonexistentMatriculationNumber_throwsCommandException() {
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A9999999Z")), Optional.empty(),
                Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "No student with matriculation number A9999999Z found.");
    }

    @Test
    public void execute_noAttendanceRecorded_throwsCommandException() {
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "No attendance recorded for Alice Pauline on 07-10-2026.");
    }

    @Test
    public void execute_differentDateRecorded_throwsCommandException() {
        recordAttendance(ALICE, OTHER_DATE.toString());
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model, "No attendance recorded for Alice Pauline on 07-10-2026.");
    }

    @Test
    public void execute_futureDateNotRecorded_throwsCommandException() {
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(FUTURE_DATE));

        assertCommandFailure(command, model, "No attendance recorded for Alice Pauline on 31-12-9999.");
    }

    @Test
    public void execute_futureDateRecorded_success() {
        Person student = recordAttendance(ALICE, FUTURE_DATE.toString());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, ALICE);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(FUTURE_DATE));

        assertCommandSuccess(command, model, "Unmarked attendance for Alice Pauline in CS2103T-T10 on 31-12-9999",
                expectedModel);
    }

    @Test
    public void equals() {
        StudentIdentifier firstStudent = new StudentIdentifier(INDEX_FIRST_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(firstStudent, Optional.empty(),
                Optional.of(ATTENDANCE_DATE));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new UnmarkAttendanceCommand(firstStudent, Optional.empty(),
                Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_SECOND_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(new UnmarkAttendanceCommand(firstStudent, Optional.empty(),
                Optional.of(OTHER_DATE))));
        assertFalse(command.equals(new UnmarkAttendanceCommand(firstStudent, Optional.empty(), Optional.empty())));
        assertFalse(command.equals(new MarkAttendanceCommand(firstStudent, CLASS_T10, Optional.of(ATTENDANCE_DATE))));

        StudentIdentifier matricStudent = new StudentIdentifier(new MatriculationNumber("A1234567A"));
        UnmarkAttendanceCommand matricCommand = new UnmarkAttendanceCommand(matricStudent, Optional.empty(),
                Optional.of(ATTENDANCE_DATE));

        assertTrue(matricCommand.equals(new UnmarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567A")), Optional.empty(),
                Optional.of(ATTENDANCE_DATE))));
        assertFalse(matricCommand.equals(new UnmarkAttendanceCommand(
                new StudentIdentifier(new MatriculationNumber("A1234567B")), Optional.empty(),
                Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(matricCommand));
    }

    @Test
    public void toStringMethod() {
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));
        String expected = UnmarkAttendanceCommand.class.getCanonicalName()
                + "{targetStudent=" + new StudentIdentifier(INDEX_FIRST_PERSON)
                + ", classCode=" + Optional.empty() + ", date=" + Optional.of(ATTENDANCE_DATE) + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Records the given dates for the student in the model and returns the updated student.
     */
    private Person recordAttendance(Person student, String... dates) {
        Person markedStudent = new PersonBuilder(student).withAttendance("CS2103T-T10", dates).build();
        model.setPerson(student, markedStudent);
        return markedStudent;
    }

    /**
     * Returns an unfiltered copy of the model for expected results.
     */
    private Model copyModel() {
        return new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
    }

    @Test
    public void execute_explicitClass_keepsOtherClassOnSameDate() {
        AttendanceRecord first = new AttendanceRecord(ATTENDANCE_DATE, CLASS_T10);
        AttendanceRecord second = new AttendanceRecord(ATTENDANCE_DATE, CLASS_T11);
        Person student = new PersonBuilder(ALICE).withAttendanceRecords(first, second).build();
        model.setPerson(ALICE, student);
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, new PersonBuilder(ALICE).withAttendanceRecords(second).build());
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model,
                "Unmarked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_explicitClassNotRecorded_throwsRecordNotFound() {
        Person student = new PersonBuilder(ALICE)
                .withAttendance(CLASS_T11.toString(), ATTENDANCE_DATE.toString()).build();
        model.setPerson(ALICE, student);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model,
                "No attendance recorded for Alice Pauline in CS2103T-T10 on 07-10-2026.");
    }

    @Test
    public void execute_explicitClassWithoutCurrentEnrolment_success() {
        Person student = recordAttendance(ALICE, ATTENDANCE_DATE.toString());
        assertTrue(student.getClassCodes().isEmpty());
        Model expectedModel = copyModel();
        expectedModel.setPerson(student, ALICE);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE));

        assertCommandSuccess(command, model,
                "Unmarked attendance for Alice Pauline in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_noClassWithMultipleRecords_throwsErrorWithSortedClasses() {
        Person student = new PersonBuilder(ALICE).withAttendanceRecords(
                new AttendanceRecord(ATTENDANCE_DATE, CLASS_T11),
                new AttendanceRecord(ATTENDANCE_DATE, CLASS_T10)).build();
        model.setPerson(ALICE, student);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(new StudentIdentifier(INDEX_FIRST_PERSON),
                Optional.empty(), Optional.of(ATTENDANCE_DATE));

        assertCommandFailure(command, model,
                "Alice Pauline has attendance recorded in more than one class on 07-10-2026: "
                        + "CS2103T-T10, CS2103T-T11. Specify the class with c/MODULE-CLASS.");
    }

    @Test
    public void equals_differentClassOrClassPresence_returnsFalse() {
        StudentIdentifier target = new StudentIdentifier(INDEX_FIRST_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(target,
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE));

        assertTrue(command.equals(new UnmarkAttendanceCommand(target,
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(new UnmarkAttendanceCommand(target,
                Optional.of(CLASS_T11), Optional.of(ATTENDANCE_DATE))));
        assertFalse(command.equals(new UnmarkAttendanceCommand(target,
                Optional.empty(), Optional.of(ATTENDANCE_DATE))));
    }

    @Test
    public void toStringMethod_explicitClass_includesClassCode() {
        StudentIdentifier target = new StudentIdentifier(INDEX_FIRST_PERSON);
        UnmarkAttendanceCommand command = new UnmarkAttendanceCommand(target,
                Optional.of(CLASS_T10), Optional.of(ATTENDANCE_DATE));
        String expected = UnmarkAttendanceCommand.class.getCanonicalName()
                + "{targetStudent=" + target + ", classCode=" + Optional.of(CLASS_T10)
                + ", date=" + Optional.of(ATTENDANCE_DATE) + "}";
        assertEquals(expected, command.toString());
    }

}
