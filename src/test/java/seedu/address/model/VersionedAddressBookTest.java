package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;

public class VersionedAddressBookTest {
    @Test
    public void undoRedo_initialState_throwsWithoutChangingData() {
        VersionedAddressBook book = new VersionedAddressBook(new AddressBook());
        assertFalse(book.canUndo());
        assertFalse(book.canRedo());
        assertThrows(IllegalStateException.class, book::undo);
        assertThrows(IllegalStateException.class, book::redo);
        assertEquals(new AddressBook(), book);
    }

    @Test
    public void commit_afterUndo_discardsRedoBranch() {
        VersionedAddressBook book = new VersionedAddressBook(new AddressBook());
        book.addPerson(ALICE);
        book.commit();
        book.undo();
        book.addPerson(BOB);
        book.commit();

        assertFalse(book.canRedo());
        assertThrows(IllegalStateException.class, book::redo);
        book.undo();
        assertEquals(new AddressBook(), book);
        assertFalse(book.canUndo());
        book.redo();
        assertEquals(List.of(BOB), book.getPersonList());
    }

    @Test
    public void commit_unchangedData_preservesRedoWithoutAddingHistory() {
        VersionedAddressBook book = new VersionedAddressBook(new AddressBook());
        book.commit();
        assertFalse(book.canUndo());
        book.addPerson(ALICE);
        book.commit();
        book.commit();
        book.undo();
        assertFalse(book.canUndo());
        book.commit();
        assertTrue(book.canRedo());
        book.redo();
        assertEquals(List.of(ALICE), book.getPersonList());
        assertFalse(book.canRedo());
    }

    @Test
    public void undoRedo_gradebookChanges_restoreIndependentSnapshots() {
        AddressBook initial = new AddressBook();
        initial.addPerson(ALICE);
        VersionedAddressBook book = new VersionedAddressBook(initial);
        AssignmentId id = new AssignmentId("CS2103T:A1");
        book.addAssignmentForStudent(new Assignment(id, "Assignment One", new BigDecimal("20")),
                ALICE.getMatriculationNumber());
        book.commit();
        book.recordMark(ALICE.getMatriculationNumber(), id, new Mark(new BigDecimal("17")));
        book.commit();
        book.removeAssignment(id);
        book.commit();

        book.undo();
        assertEquals(Optional.of(new Mark(new BigDecimal("17"))), book.getGradebook()
                .getMarksForStudent(ALICE.getMatriculationNumber()).get(id));
        book.undo();
        assertEquals(Optional.empty(), book.getGradebook()
                .getMarksForStudent(ALICE.getMatriculationNumber()).get(id));
        book.undo();
        assertEquals(initial, book);
        book.redo();
        book.redo();
        assertEquals(Optional.of(new Mark(new BigDecimal("17"))), book.getGradebook()
                .getMarksForStudent(ALICE.getMatriculationNumber()).get(id));
        book.redo();
        assertTrue(book.getGradebook().getAssignments().isEmpty());
    }
}
