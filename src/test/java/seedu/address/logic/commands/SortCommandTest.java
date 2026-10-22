package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.Person;
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
    public void execute_attendanceSort_ordersByRecordedDateCount() throws Exception {
        Person attendedAlice = new PersonBuilder(ALICE_LOWERCASE).withAttendance("01-10-2026").build();
        Person attendedCharlie = new PersonBuilder(CHARLIE).withAttendance("01-10-2026", "02-10-2026").build();
        model.setPerson(ALICE_LOWERCASE, attendedAlice);
        model.setPerson(CHARLIE, attendedCharlie);

        new SortCommand(ATTENDANCE, ASC).execute(model);
        assertEquals(List.of(BOB, ZOE, attendedAlice, attendedCharlie), model.getFilteredPersonList());

        new SortCommand(ATTENDANCE, DESC).execute(model);
        assertEquals(List.of(attendedCharlie, attendedAlice, BOB, ZOE), model.getFilteredPersonList());
        assertEquals("Attendance dates: 0", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
        assertEquals("Attendance dates: 2",
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
        Person attendedBob = new PersonBuilder(BOB).withAttendance("01-10-2026").build();

        model.setPerson(BOB, attendedBob);

        assertEquals(attendedBob, model.getFilteredPersonList().getFirst());
        assertEquals("Attendance dates: 1",
                model.sortDisplayProperty().getValue().getDetailFor(attendedBob).orElseThrow());
    }

    @Test
    public void execute_markSort_placesMissingMarksLastInBothDirections() throws Exception {
        model.addAssignmentForStudent(new Assignment(assignmentId, "Assignment One", new BigDecimal("20")),
                BOB.getMatriculationNumber());
        model.assignStudent(ZOE.getMatriculationNumber(), assignmentId);
        model.assignStudent(CHARLIE.getMatriculationNumber(), assignmentId);
        model.recordMark(ZOE.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("0")));
        model.recordMark(CHARLIE.getMatriculationNumber(), assignmentId, new Mark(new BigDecimal("12.5")));

        new SortCommand(assignmentId, ASC).execute(model);
        assertEquals(List.of(ZOE, CHARLIE, ALICE_LOWERCASE, BOB), model.getFilteredPersonList());
        assertEquals("CS2103T:A1: 0/20", model.sortDisplayProperty().getValue().getDetailFor(ZOE).orElseThrow());
        assertEquals("CS2103T:A1: Unmarked", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
        assertEquals("CS2103T:A1: Not assigned",
                model.sortDisplayProperty().getValue().getDetailFor(ALICE_LOWERCASE).orElseThrow());

        new SortCommand(assignmentId, DESC).execute(model);
        assertEquals(List.of(CHARLIE, ZOE, ALICE_LOWERCASE, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_unknownAssignment_keepsPreviousSortAndDetail() throws Exception {
        new SortCommand(ATTENDANCE, DESC).execute(model);
        assertCommandFailure(new SortCommand(assignmentId, ASC), model, Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
        assertEquals("Attendance dates: 0", model.sortDisplayProperty().getValue().getDetailFor(BOB).orElseThrow());
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
