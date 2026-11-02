
package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains tests for {@code ViewAttendanceStatsCommand}.
 */
public class ViewAttendanceStatsCommandTest {

    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");
    private static final AttendanceDate DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noEnrolledStudents_showsZeroTotal() {
        enroll(DANIEL, CLASS_T11);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(0, 0),
                expectedModel);
    }

    @Test
    public void execute_emptyAddressBook_showsZeroTotal() {
        Model emptyModel = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                emptyModel,
                expectedMessage(0, 0),
                expectedModel);
    }

    @Test
    public void execute_enrolledStudentsWithoutRecords_showsZeroAttended() {
        enroll(ALICE, CLASS_T10);
        enroll(BENSON, CLASS_T10);
        enroll(CARL, CLASS_T10);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(0, 3),
                expectedModel);
    }

    @Test
    public void execute_someStudentsAttended_showsCorrectCounts() {
        enrollWithAttendance(ALICE, CLASS_T10, DATE);
        enrollWithAttendance(BENSON, CLASS_T10, DATE);
        enroll(CARL, CLASS_T10);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(2, 3),
                expectedModel);
    }

    @Test
    public void execute_allStudentsAttended_showsCorrectCounts() {
        enrollWithAttendance(ALICE, CLASS_T10, DATE);
        enrollWithAttendance(BENSON, CLASS_T10, DATE);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(2, 2),
                expectedModel);
    }

    @Test
    public void execute_differentDate_doesNotCountRecord() {
        enrollWithAttendance(ALICE, CLASS_T10, OTHER_DATE);
        enroll(BENSON, CLASS_T10);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(0, 2),
                expectedModel);
    }

    @Test
    public void execute_differentClass_doesNotCountRecord() {
        Person alice = new PersonBuilder(ALICE)
                .withClassCodes(CLASS_T10.toString(), CLASS_T11.toString())
                .withAttendance(CLASS_T11.toString(), DATE.toString())
                .build();
        model.setPerson(ALICE, alice);
        enroll(BENSON, CLASS_T10);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(0, 2),
                expectedModel);
    }

    @Test
    public void execute_unenrolledStudentWithRecord_notCounted() {
        Person alice = new PersonBuilder(ALICE)
                .withAttendance(CLASS_T10.toString(), DATE.toString())
                .build();
        model.setPerson(ALICE, alice);
        enrollWithAttendance(BENSON, CLASS_T10, DATE);
        Model expectedModel = copyModel();

        assertCommandSuccess(
                new ViewAttendanceStatsCommand(CLASS_T10, DATE),
                model,
                expectedMessage(1, 1),
                expectedModel);
    }

    @Test
    public void execute_filteredList_countsAllEnrolledStudents() {
        enrollWithAttendance(ALICE, CLASS_T10, DATE);
        enrollWithAttendance(BENSON, CLASS_T10, DATE);
        enroll(CARL, CLASS_T10);

        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());
        int originalDisplayedCount = model.getFilteredPersonList().size();

        try {
            CommandResult result =
                    new ViewAttendanceStatsCommand(CLASS_T10, DATE).execute(model);

            assertEquals(expectedMessage(2, 3), result.getFeedbackToUser());
            assertEquals(originalAddressBook, model.getAddressBook());
            assertEquals(originalDisplayedCount, model.getFilteredPersonList().size());
        } catch (seedu.address.logic.commands.exceptions.CommandException exception) {
            throw new AssertionError("Statistics command should not fail", exception);
        }
    }

    @Test
    public void equals() {
        ViewAttendanceStatsCommand command =
                new ViewAttendanceStatsCommand(CLASS_T10, DATE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewAttendanceStatsCommand(CLASS_T10, DATE)));
        assertFalse(command.equals(new ViewAttendanceStatsCommand(CLASS_T11, DATE)));
        assertFalse(command.equals(new ViewAttendanceStatsCommand(CLASS_T10, OTHER_DATE)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void toStringMethod() {
        ViewAttendanceStatsCommand command =
                new ViewAttendanceStatsCommand(CLASS_T10, DATE);

        String expected = ViewAttendanceStatsCommand.class.getCanonicalName()
                + "{classCode=CS2103T-T10, date=07-10-2026}";

        assertEquals(expected, command.toString());
    }

    /**
     * Enrolls a student in the given class.
     */
    private void enroll(Person student, ClassCode classCode) {
        Person updatedStudent = new PersonBuilder(student)
                .withClassCodes(classCode.toString())
                .build();

        model.setPerson(student, updatedStudent);
    }

    /**
     * Enrolls a student and adds an attendance record for the given date.
     */
    private void enrollWithAttendance(Person student, ClassCode classCode,
                                      AttendanceDate date) {
        Person updatedStudent = new PersonBuilder(student)
                .withClassCodes(classCode.toString())
                .withAttendance(classCode.toString(), date.toString())
                .build();

        model.setPerson(student, updatedStudent);
    }

    /**
     * Creates a separate model for checking that the command does not change data.
     */
    private Model copyModel() {
        return new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
    }

    /**
     * Returns the expected command result for the given attendance counts.
     */
    private String expectedMessage(int attended, int total) {
        return String.format(ViewAttendanceStatsCommand.MESSAGE_SUCCESS,
                CLASS_T10, DATE, attended, total);
    }
}
