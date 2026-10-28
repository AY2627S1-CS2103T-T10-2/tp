package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains tests for {@code MarkClassAttendanceCommand}.
 */
public class MarkClassAttendanceCommandTest {
    private static final ClassCode CLASS_T10 = new ClassCode("CS2103T-T10");
    private static final ClassCode CLASS_T11 = new ClassCode("CS2103T-T11");
    private static final AttendanceDate DATE = new AttendanceDate("07-10-2026");
    private static final AttendanceDate OTHER_DATE = new AttendanceDate("08-10-2026");

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noEnrolledStudents_throwsCommandException() {
        enroll(BENSON, CLASS_T11);

        assertCommandFailure(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "No students are enrolled in CS2103T-T10.");
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        assertCommandFailure(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), new ModelManager(),
                "No students are enrolled in CS2103T-T10.");
    }

    @Test
    public void execute_oneEnrolledStudent_success() {
        Person alice = enroll(ALICE, CLASS_T10);
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice, new AttendanceRecord(DATE, CLASS_T10));

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 1 student in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_multipleEnrolledStudents_marksOnlyEnrolledStudents() {
        Person alice = enroll(ALICE, CLASS_T10);
        Person benson = enroll(BENSON, CLASS_T10);
        Person carl = enroll(CARL, CLASS_T10);
        enroll(DANIEL, CLASS_T11);
        Model expectedModel = copyModel();
        AttendanceRecord record = new AttendanceRecord(DATE, CLASS_T10);
        setExpectedAttendance(expectedModel, alice, record);
        setExpectedAttendance(expectedModel, benson, record);
        setExpectedAttendance(expectedModel, carl, record);

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 3 students in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_someAlreadyRecorded_skipsRecordedStudents() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(CLASS_T10.toString())
                .withAttendance(CLASS_T10.toString(), DATE.toString()).build();
        model.setPerson(ALICE, alice);
        Person benson = enroll(BENSON, CLASS_T10);
        Person carl = enroll(CARL, CLASS_T10);
        Model expectedModel = copyModel();
        AttendanceRecord record = new AttendanceRecord(DATE, CLASS_T10);
        setExpectedAttendance(expectedModel, benson, record);
        setExpectedAttendance(expectedModel, carl, record);

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 2 students in CS2103T-T10 on 07-10-2026"
                        + "\nAlready recorded, skipped: Alice Pauline", expectedModel);
    }

    @Test
    public void execute_allAlreadyRecorded_successWithoutChanges() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(CLASS_T10.toString())
                .withAttendance(CLASS_T10.toString(), DATE.toString()).build();
        Person benson = new PersonBuilder(BENSON).withClassCodes(CLASS_T10.toString())
                .withAttendance(CLASS_T10.toString(), DATE.toString()).build();
        model.setPerson(ALICE, alice);
        model.setPerson(BENSON, benson);
        Model expectedModel = copyModel();

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 0 students in CS2103T-T10 on 07-10-2026"
                        + "\nAlready recorded, skipped: Alice Pauline, Benson Meier", expectedModel);
    }

    @Test
    public void execute_existingDifferentDate_keepsBothRecords() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(CLASS_T10.toString())
                .withAttendance(CLASS_T10.toString(), OTHER_DATE.toString()).build();
        model.setPerson(ALICE, alice);
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice,
                new AttendanceRecord(OTHER_DATE, CLASS_T10), new AttendanceRecord(DATE, CLASS_T10));

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 1 student in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_otherClassRecordedOnSameDate_keepsBothRecords() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(CLASS_T10.toString(), CLASS_T11.toString())
                .withAttendance(CLASS_T11.toString(), DATE.toString()).build();
        model.setPerson(ALICE, alice);
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice,
                new AttendanceRecord(DATE, CLASS_T11), new AttendanceRecord(DATE, CLASS_T10));

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 1 student in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_unenrolledStudentWithRecord_leavesStudentUnchangedAndUnnamed() {
        Person alice = enroll(ALICE, CLASS_T10);
        Person benson = new PersonBuilder(BENSON)
                .withAttendance(CLASS_T10.toString(), OTHER_DATE.toString()).build();
        model.setPerson(BENSON, benson);
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice, new AttendanceRecord(DATE, CLASS_T10));

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 1 student in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_lowercaseClass_success() {
        Person alice = enroll(ALICE, CLASS_T10);
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice, new AttendanceRecord(DATE, CLASS_T10));

        assertCommandSuccess(new MarkClassAttendanceCommand(new ClassCode("cs2103t-t10"), Optional.of(DATE)), model,
                "Marked attendance for 1 student in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void execute_withoutDate_marksToday() throws CommandException {
        Person alice = enroll(ALICE, CLASS_T10);
        AttendanceDate today = AttendanceDate.today();
        Model expectedModel = copyModel();
        setExpectedAttendance(expectedModel, alice, new AttendanceRecord(today, CLASS_T10));

        CommandResult result = new MarkClassAttendanceCommand(CLASS_T10, Optional.empty()).execute(model);

        assertEquals(expectedModel, model);
        assertEquals("Marked attendance for 1 student in CS2103T-T10 on " + today, result.getFeedbackToUser());
    }

    @Test
    public void execute_filteredList_marksStudentsOutsideDisplayedList() {
        Person alice = enroll(ALICE, CLASS_T10);
        Person benson = enroll(BENSON, CLASS_T10);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Model expectedModel = copyModel();
        AttendanceRecord record = new AttendanceRecord(DATE, CLASS_T10);
        setExpectedAttendance(expectedModel, alice, record);
        setExpectedAttendance(expectedModel, benson, record);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE)), model,
                "Marked attendance for 2 students in CS2103T-T10 on 07-10-2026", expectedModel);
    }

    @Test
    public void equals() {
        MarkClassAttendanceCommand command = new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE))));
        assertFalse(command.equals(new MarkClassAttendanceCommand(CLASS_T11, Optional.of(DATE))));
        assertFalse(command.equals(new MarkClassAttendanceCommand(CLASS_T10, Optional.of(OTHER_DATE))));
        assertFalse(command.equals(new MarkClassAttendanceCommand(CLASS_T10, Optional.empty())));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void toStringMethod() {
        MarkClassAttendanceCommand command = new MarkClassAttendanceCommand(CLASS_T10, Optional.of(DATE));
        String expected = MarkClassAttendanceCommand.class.getCanonicalName()
                + "{classCode=CS2103T-T10, date=" + Optional.of(DATE) + "}";

        assertEquals(expected, command.toString());
    }

    /**
     * Enrolls the given typical student and returns the replacement stored in the model.
     */
    private Person enroll(Person student, ClassCode classCode) {
        Person enrolledStudent = new PersonBuilder(student).withClassCodes(classCode.toString()).build();
        model.setPerson(student, enrolledStudent);
        return enrolledStudent;
    }

    /**
     * Copies the address book and preferences for expected-model assertions.
     */
    private Model copyModel() {
        return new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
    }

    /**
     * Replaces the student's attendance with the explicitly expected records.
     */
    private void setExpectedAttendance(Model expectedModel, Person student, AttendanceRecord... records) {
        expectedModel.setPerson(student, new PersonBuilder(student).withAttendanceRecords(records).build());
    }
}
