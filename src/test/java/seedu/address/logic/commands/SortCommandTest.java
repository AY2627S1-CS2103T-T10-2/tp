package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.SortCommand.SortDirection.ASC;
import static seedu.address.logic.commands.SortCommand.SortDirection.DESC;
import static seedu.address.logic.commands.SortCommand.SortField.ATTENDANCE;
import static seedu.address.logic.commands.SortCommand.SortField.EMAIL;
import static seedu.address.logic.commands.SortCommand.SortField.MATRIC;
import static seedu.address.logic.commands.SortCommand.SortField.NAME;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortDisplay;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.AttendanceDate;
import seedu.address.model.person.AttendanceRecord;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.ParticipationHistory;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code SortCommand}.
 */
public class SortCommandTest {

    // Deliberately NOT in alphabetical order, and with a lowercase name, so that a missing sort
    // or a case-sensitive sort both make the tests fail.
    private static final Person ZOE = new PersonBuilder().withName("Zoe Tan")
            .withMatriculationNumber("A1000001A").withEmail("beta@example.com").build();
    private static final Person CHARLIE = new PersonBuilder().withName("Charlie Ho")
            .withMatriculationNumber("A1000002B").withEmail("delta@example.com").build();
    private static final Person ALICE_LOWERCASE = new PersonBuilder().withName("alice Lim")
            .withMatriculationNumber("A1000003C").withEmail("zeta@example.com").build();
    private static final Person BOB = new PersonBuilder().withName("Bob Ng")
            .withMatriculationNumber("A1000004D").withEmail("alpha@example.com").build();

    private Model model;
    private final AssignmentId assignmentId = new AssignmentId("CS2103T:A1");

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ZOE);
        addressBook.addPerson(CHARLIE);
        addressBook.addPerson(ALICE_LOWERCASE);
        addressBook.addPerson(BOB);
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void execute_reverseNameOrder_sortsByNameIgnoringCase() {
        // ModelManager#equals does not look at the order of the displayed list,
        // so the order is checked explicitly instead of through assertCommandSuccess's model comparison.
        model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandSuccess(new SortCommand(), model,
                String.format(SortCommand.MESSAGE_SUCCESS, "name", "ascending"), expectedModel);

        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_sortedTwice_sameResult() throws Exception {
        new SortCommand().execute(model);
        new SortCommand().execute(model);

        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameDescending_reversesPrimaryOrder() throws Exception {
        new SortCommand(NAME, DESC).execute(model);

        assertEquals(List.of(ZOE, CHARLIE, BOB, ALICE_LOWERCASE), model.getFilteredPersonList());
    }

    @Test
    public void execute_matricAscendingAndDescending_ordersByMatriculationNumber() throws Exception {
        new SortCommand(MATRIC, ASC).execute(model);
        assertEquals(List.of(ZOE, CHARLIE, ALICE_LOWERCASE, BOB), model.getFilteredPersonList());

        new SortCommand(MATRIC, DESC).execute(model);
        assertEquals(List.of(BOB, ALICE_LOWERCASE, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_emailAscendingAndDescending_ordersByEmail() throws Exception {
        new SortCommand(EMAIL, ASC).execute(model);
        assertEquals(List.of(BOB, ZOE, CHARLIE, ALICE_LOWERCASE), model.getFilteredPersonList());

        new SortCommand(EMAIL, DESC).execute(model);
        assertEquals(List.of(ALICE_LOWERCASE, CHARLIE, ZOE, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_equalEmail_ordersByName() throws Exception {
        Person aaron = new PersonBuilder().withName("Aaron Tan")
                .withMatriculationNumber("A1000005E").withEmail("beta@example.com").build();
        model.addPerson(aaron);

        new SortCommand(EMAIL, DESC).execute(model);

        assertEquals(List.of(ALICE_LOWERCASE, CHARLIE, aaron, ZOE, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_equalName_ordersByMatriculationNumber() throws Exception {
        Person otherBob = new PersonBuilder().withName("Bob Ng")
                .withMatriculationNumber("A1000000Z").withEmail("other@example.com").build();
        model.addPerson(otherBob);

        new SortCommand(NAME, DESC).execute(model);

        assertEquals(List.of(ZOE, CHARLIE, otherBob, BOB, ALICE_LOWERCASE), model.getFilteredPersonList());
    }

    @Test
    public void execute_attendanceSort_ordersByRecordCount() throws Exception {
        Person attendedAlice = new PersonBuilder(ALICE_LOWERCASE).withAttendance("CS2103T-T10", "01-10-2026").build();
        Person attendedCharlie = new PersonBuilder(CHARLIE)
                .withAttendance("CS2103T-T10", "01-10-2026", "02-10-2026").build();
        model.setPerson(ALICE_LOWERCASE, attendedAlice);
        model.setPerson(CHARLIE, attendedCharlie);

        new SortCommand(ATTENDANCE, ASC).execute(model);
        assertEquals(List.of(BOB, ZOE, attendedAlice, attendedCharlie), model.getFilteredPersonList());

        new SortCommand(ATTENDANCE, DESC).execute(model);
        assertEquals(List.of(attendedCharlie, attendedAlice, BOB, ZOE), model.getFilteredPersonList());
        assertEquals("Attendance records: 0", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
        assertEquals("Attendance records: 2",
                model.sortDisplayProperty().getValue().getDetailFor(attendedCharlie).orElseThrow());
    }

    @Test
    public void execute_attendanceSortThenList_clearsExtraDetail() throws Exception {
        new SortCommand(ATTENDANCE, DESC).execute(model);
        assertTrue(model.sortDisplayProperty().getValue().getDetailFor(BOB).isPresent());

        new ListCommand().execute(model);

        assertTrue(model.sortDisplayProperty().getValue().getDetailFor(BOB).isEmpty());
    }

    @Test
    public void execute_attendanceSortThenEmailSort_clearsExtraDetail() throws Exception {
        new SortCommand(ATTENDANCE, DESC).execute(model);

        new SortCommand(EMAIL, ASC).execute(model);

        assertTrue(model.sortDisplayProperty().getValue().getDetailFor(BOB).isEmpty());
    }

    @Test
    public void execute_attendanceUpdatedAfterSort_updatesOrder() throws Exception {
        new SortCommand(ATTENDANCE, DESC).execute(model);
        Person attendedBob = new PersonBuilder(BOB).withAttendance("CS2103T-T10", "01-10-2026").build();

        model.setPerson(BOB, attendedBob);

        assertEquals(attendedBob, model.getFilteredPersonList().getFirst());
        assertEquals("Attendance records: 1",
                model.sortDisplayProperty().getValue().getDetailFor(attendedBob).orElseThrow());
    }

    @Test
    public void execute_markSort_ordersMarkedUnmarkedThenNotAssignedInBothDirections() throws Exception {
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                BOB.getMatriculationNumber());
        model.assignStudent(ZOE.getMatriculationNumber(), assignmentId);
        model.assignStudent(CHARLIE.getMatriculationNumber(), assignmentId);
        model.recordMark(ZOE.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("0")));
        model.recordMark(CHARLIE.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("12.5")));

        new SortCommand(assignmentId, ASC).execute(model);
        assertEquals(List.of(ZOE, CHARLIE, BOB, ALICE_LOWERCASE), model.getFilteredPersonList());
        assertEquals("CS2103T:A1: 0/20", model.sortDisplayProperty().getValue().getDetailFor(ZOE).orElseThrow());
        assertEquals("CS2103T:A1: Unmarked", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
        assertEquals("CS2103T:A1: Not assigned",
                model.sortDisplayProperty().getValue().getDetailFor(ALICE_LOWERCASE).orElseThrow());

        new SortCommand(assignmentId, DESC).execute(model);
        assertEquals(List.of(CHARLIE, ZOE, BOB, ALICE_LOWERCASE), model.getFilteredPersonList());
    }

    @Test
    public void execute_markSortWithMissingMarks_ordersEachStatusByNameThenMatricInBothDirections() throws Exception {
        Person otherBob = new PersonBuilder(BOB).withName("bob Ng")
                .withMatriculationNumber("A1000000Z").build();
        Person otherAlice = new PersonBuilder(ALICE_LOWERCASE).withName("Alice Lim")
                .withMatriculationNumber("A1000005E").build();
        model.addPerson(otherBob);
        model.addPerson(otherAlice);
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                ZOE.getMatriculationNumber());
        model.assignStudent(BOB.getMatriculationNumber(), assignmentId);
        model.assignStudent(otherBob.getMatriculationNumber(), assignmentId);

        for (SortCommand.SortDirection direction : List.of(ASC, DESC)) {
            new SortCommand(assignmentId, direction).execute(model);

            assertEquals(List.of(otherBob, BOB, ZOE, ALICE_LOWERCASE, otherAlice, CHARLIE),
                    model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_markSortWithEqualMarks_ordersByNameThenMatricInBothDirections() throws Exception {
        Person otherBob = new PersonBuilder(BOB).withName("bob Ng")
                .withMatriculationNumber("A1000000Z").build();
        model.addPerson(otherBob);
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                BOB.getMatriculationNumber());
        for (Person student : List.of(ALICE_LOWERCASE, otherBob, CHARLIE, ZOE)) {
            model.assignStudent(student.getMatriculationNumber(), assignmentId);
        }
        for (Person student : List.of(BOB, ALICE_LOWERCASE, otherBob, CHARLIE, ZOE)) {
            model.recordMark(student.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("10")));
        }

        for (SortCommand.SortDirection direction : List.of(ASC, DESC)) {
            new SortCommand(assignmentId, direction).execute(model);

            assertEquals(List.of(ALICE_LOWERCASE, otherBob, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_unknownAssignment_keepsPreviousSortAndDetail() throws Exception {
        new SortCommand(ATTENDANCE, DESC).execute(model);
        assertCommandFailure(new SortCommand(assignmentId, ASC), model, Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
        assertEquals("Attendance records: 0", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
    }

    @Test
    public void execute_markSortOnFilteredList_keepsOtherStudentsHidden() throws Exception {
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                BOB.getMatriculationNumber());
        model.recordMark(BOB.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("10")));
        model.updateFilteredPersonList(person -> !person.equals(BOB));

        new SortCommand(assignmentId, DESC).execute(model);

        assertEquals(List.of(ALICE_LOWERCASE, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_markUpdateAfterSort_resetsToNameOrder() throws Exception {
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                BOB.getMatriculationNumber());
        new SortCommand(assignmentId, DESC).execute(model);

        model.recordMark(BOB.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("15")));

        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
        assertTrue(model.sortDisplayProperty().getValue().getDetailFor(BOB).isEmpty());
    }

    @Test
    public void execute_markSortThenTwoAttendanceCommands_updatesBerniceBothTimes() throws Exception {
        setUpSampleMarkSort(DESC);
        SortDisplay display = model.sortDisplayProperty().getValue();
        Attendance berniceAttendance = model.getFilteredPersonList().getFirst().getAttendance();
        Attendance alexAttendance = model.getFilteredPersonList().get(1).getAttendance();

        executeCommand("attendance 1 c/CS2103T-T10 d/10-10-2026");
        assertSampleMarkSort(display, List.of("Bernice Yu", "Alex Yeoh", "Charlotte Oliveiro",
                "David Li", "Irfan Ibrahim", "Roy Balakrishnan"));
        assertEquals(berniceAttendance.withRecord(createAttendanceRecord("10-10-2026")),
                model.getFilteredPersonList().getFirst().getAttendance());
        executeCommand("attendance 1 c/CS2103T-T10 d/11-10-2026");

        assertSampleMarkSort(display, List.of("Bernice Yu", "Alex Yeoh", "Charlotte Oliveiro",
                "David Li", "Irfan Ibrahim", "Roy Balakrishnan"));
        assertEquals(berniceAttendance.withRecord(createAttendanceRecord("10-10-2026"))
                .withRecord(createAttendanceRecord("11-10-2026")),
                model.getFilteredPersonList().getFirst().getAttendance());
        assertEquals(alexAttendance, model.getFilteredPersonList().get(1).getAttendance());
    }

    @Test
    public void execute_classAndParticipationUpdates_preserveMarkSortAndDetails() throws Exception {
        for (SortCommand.SortDirection direction : List.of(ASC, DESC)) {
            setUpSampleMarkSort(direction);
            SortDisplay display = model.sortDisplayProperty().getValue();
            ParticipationHistory berniceParticipation = model.getFilteredPersonList().getFirst()
                    .getParticipationHistory();
            ParticipationHistory alexParticipation = model.getFilteredPersonList().get(1).getParticipationHistory();
            ParticipationRecord record = new ParticipationRecord(new ClassCode("CS2103T-T10"),
                    new AttendanceDate("10-10-2026").toLocalDate(), new ParticipationPoints("2"));
            List<String> expectedNames = List.of("Bernice Yu", "Alex Yeoh", "Charlotte Oliveiro",
                    "David Li", "Irfan Ibrahim", "Roy Balakrishnan");

            executeCommand("enroll 1 c/CS2103T-T11");
            assertSampleMarkSort(display, expectedNames);
            assertTrue(model.getFilteredPersonList().getFirst().isEnrolledIn(new ClassCode("CS2103T-T11")));

            executeCommand("unenroll 1 c/CS2103T-T11");
            assertSampleMarkSort(display, expectedNames);
            assertFalse(model.getFilteredPersonList().getFirst().isEnrolledIn(new ClassCode("CS2103T-T11")));

            executeCommand("participation 1 c/CS2103T-T10 d/10-10-2026 p/2");
            assertSampleMarkSort(display, expectedNames);
            assertEquals(berniceParticipation.withRecord(record),
                    model.getFilteredPersonList().getFirst().getParticipationHistory());
            assertEquals(alexParticipation, model.getFilteredPersonList().get(1).getParticipationHistory());

            executeCommand("participation remove 1 c/CS2103T-T10 d/10-10-2026");
            assertSampleMarkSort(display, expectedNames);
            assertEquals(berniceParticipation, model.getFilteredPersonList().getFirst().getParticipationHistory());

            executeCommand("unenroll 1 all/");
            assertSampleMarkSort(display, expectedNames);
            assertFalse(model.getFilteredPersonList().getFirst().isEnrolled());
        }
    }

    @Test
    public void execute_wholeClassAttendanceAndUnmark_preserveFilteredMarkSort() throws Exception {
        setUpSampleMarkSort(ASC);
        executeCommand("find Alex Bernice Roy");
        executeCommand("sort mark id/CS2103T:A1 asc");
        SortDisplay display = model.sortDisplayProperty().getValue();
        List<String> expectedNames = List.of("Bernice Yu", "Alex Yeoh", "Roy Balakrishnan");
        Attendance berniceAttendance = model.getFilteredPersonList().getFirst().getAttendance();
        Attendance alexAttendance = model.getFilteredPersonList().get(1).getAttendance();

        executeCommand("attendance c/CS2103T-T10 d/10-10-2026");
        assertSampleMarkSort(display, expectedNames);
        assertEquals(berniceAttendance.withRecord(createAttendanceRecord("10-10-2026")),
                model.getFilteredPersonList().getFirst().getAttendance());
        assertEquals(alexAttendance.withRecord(createAttendanceRecord("10-10-2026")),
                model.getFilteredPersonList().get(1).getAttendance());

        executeCommand("attendance unmark 1 c/CS2103T-T10 d/10-10-2026");
        assertSampleMarkSort(display, expectedNames);
        assertEquals(berniceAttendance, model.getFilteredPersonList().getFirst().getAttendance());
        assertEquals(alexAttendance.withRecord(createAttendanceRecord("10-10-2026")),
                model.getFilteredPersonList().get(1).getAttendance());

        for (String command : List.of("enroll 1 c/CS2103T-T11", "unenroll 1 c/CS2103T-T11",
                "participation 1 c/CS2103T-T10 d/10-10-2026 p/2",
                "participation remove 1 c/CS2103T-T10 d/10-10-2026")) {
            executeCommand(command);
            assertSampleMarkSort(display, expectedNames);
        }
    }

    @Test
    public void execute_rejectedUpdates_preserveMarkSortAndDetails() throws Exception {
        setUpSampleMarkSort(DESC);
        executeCommand("attendance 1 c/CS2103T-T10 d/10-10-2026");
        SortDisplay display = model.sortDisplayProperty().getValue();
        List<Person> students = List.copyOf(model.getFilteredPersonList());

        for (String command : List.of("attendance 1 c/CS2103T-T10 d/10-10-2026",
                "attendance unmark 1 c/CS2103T-T10 d/11-10-2026", "enroll 1 c/CS2103T-T10",
                "unenroll 1 c/CS2103T-T11", "participation 1 c/CS2103T-T11 d/10-10-2026 p/2",
                "participation remove 1 c/CS2103T-T10 d/10-10-2026", "edit 1 m/A1234567X")) {
            Command parsedCommand = new AddressBookParser().parseCommand(command);
            assertThrows(CommandException.class, () -> parsedCommand.execute(model), command);
            assertEquals(students, model.getFilteredPersonList(), command);
            assertSampleMarkSort(display, List.of("Bernice Yu", "Alex Yeoh", "Charlotte Oliveiro",
                    "David Li", "Irfan Ibrahim", "Roy Balakrishnan"));
        }
    }

    @Test
    public void execute_studentParticularEdits_resetMarkSortAndDetails() throws Exception {
        for (String edit : List.of("n/Bernice Tan", "m/A9999999Z", "e/new@example.com", "t/newtag")) {
            setUpSampleMarkSort(DESC);

            executeCommand("edit 1 " + edit);

            assertNameOrderWithoutDetails();
            if (edit.startsWith("m/")) {
                assertTrue(model.getAddressBook().getGradebook()
                        .getMarksForStudent(new MatriculationNumber("A2345678Y")).isEmpty());
                assertEquals(new Mark(new BigDecimal("5")), model.getAddressBook().getGradebook()
                        .getMarksForStudent(new MatriculationNumber("A9999999Z")).get(assignmentId).orElseThrow());
            }
        }
    }

    @Test
    public void execute_assignmentAndMarkChanges_resetMarkSortAndDetails() throws Exception {
        for (String command : List.of("mark m/A2345678Y id/CS2103T:A1 mark/6",
                "assignment m/A1234567X id/CS2103T:A1",
                "assignment m/A2345678Y id/CS2103T:A2 n/Second max/20",
                "removeassignment m/A2345678Y id/CS2103T:A1", "removeassignment id/CS2103T:A1")) {
            setUpSampleMarkSort(DESC);

            executeCommand(command);

            assertNameOrderWithoutDetails();
        }
    }

    private void setUpSampleMarkSort(SortCommand.SortDirection direction) throws Exception {
        model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
        executeCommand("enroll 1 c/CS2103T-T10");
        executeCommand("enroll 2 c/CS2103T-T10");
        executeCommand("assignment m/A2345678Y id/CS2103T:A1 n/Quiz max/10");
        executeCommand("mark m/A2345678Y id/CS2103T:A1 mark/5");
        // Two recorded scores ensure the ascending comparator is exercised as well.
        if (direction == ASC) {
            executeCommand("assignment m/A1234567X id/CS2103T:A1");
            executeCommand("mark m/A1234567X id/CS2103T:A1 mark/9");
            executeCommand("enroll 3 c/CS2103T-T10");
            executeCommand("assignment m/A3456789Z id/CS2103T:A1");
        }
        new SortCommand(assignmentId, direction).execute(model);
    }

    private void executeCommand(String command) throws Exception {
        new AddressBookParser().parseCommand(command).execute(model);
    }

    private void assertSampleMarkSort(SortDisplay display, List<String> expectedNames) {
        assertEquals(expectedNames, model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
        assertSame(display, model.sortDisplayProperty().getValue());
        for (Person student : model.getFilteredPersonList()) {
            String result = "Not assigned";
            if (student.getMatriculationNumber().equals(new MatriculationNumber("A2345678Y"))) {
                result = "5/10";
            } else if (model.getAddressBook().getGradebook()
                    .getMarksForStudent(student.getMatriculationNumber()).containsKey(assignmentId)) {
                result = student.getMatriculationNumber().equals(new MatriculationNumber("A1234567X"))
                        ? "9/10" : "Unmarked";
            }
            assertEquals("CS2103T:A1: " + result, display.getDetailFor(student).orElseThrow());
        }
    }

    private void assertNameOrderWithoutDetails() {
        assertEquals(model.getAddressBook().getPersonList().stream().sorted(Model.COMPARATOR_NAME_ASC).toList(),
                model.getFilteredPersonList());
        assertSame(SortDisplay.NONE, model.sortDisplayProperty().getValue());
        for (Person student : model.getFilteredPersonList()) {
            assertTrue(model.sortDisplayProperty().getValue().getDetailFor(student).isEmpty());
        }
    }

    private AttendanceRecord createAttendanceRecord(String date) {
        return new AttendanceRecord(new AttendanceDate(date), new ClassCode("CS2103T-T10"));
    }

    @Test
    public void execute_thenDeleteByIndex_usesSortedOrder() throws Exception {
        new SortCommand(EMAIL, ASC).execute(model);

        new DeleteCommand(Index.fromOneBased(1)).execute(model);

        assertFalse(model.hasPerson(BOB));
    }

    @Test
    public void execute_unsortedList_doesNotChangeStoredOrder() throws Exception {
        new SortCommand(MATRIC, DESC).execute(model);

        // the sort is display-only: the address book (and therefore the saved file) keeps its order
        assertEquals(List.of(ZOE, CHARLIE, ALICE_LOWERCASE, BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filteredList_sortsOnlyShownPersons() throws Exception {
        model.updateFilteredPersonList(person -> !person.equals(CHARLIE));
        new SortCommand(MATRIC, DESC).execute(model);

        assertEquals(List.of(BOB, ALICE_LOWERCASE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandSuccess(new SortCommand(), emptyModel,
                String.format(SortCommand.MESSAGE_SUCCESS, "name", "ascending"), expectedModel);
        assertEquals(List.of(), emptyModel.getFilteredPersonList());
    }
}
