package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;

/**
 * Contains model integration tests and value semantics tests for {@code StudentIdentifier}.
 */
public class StudentIdentifierTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void resolve_firstIndexUnfilteredList_returnsFirstPerson() throws Exception {
        assertEquals(ALICE, new StudentIdentifier(INDEX_FIRST_PERSON).resolve(model));
    }

    @Test
    public void resolve_lastIndexUnfilteredList_returnsLastPerson() throws Exception {
        int size = model.getFilteredPersonList().size();
        Person expectedPerson = model.getFilteredPersonList().getLast();
        assertEquals(expectedPerson, new StudentIdentifier(Index.fromOneBased(size)).resolve(model));
    }

    @Test
    public void resolve_indexBeyondUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertThrows(CommandException.class, "The student index provided is invalid.", ()
                -> new StudentIdentifier(outOfBoundIndex).resolve(model));
    }

    @Test
    public void resolve_indexBeyondFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertThrows(CommandException.class, "The student index provided is invalid.", ()
                -> new StudentIdentifier(INDEX_SECOND_PERSON).resolve(model));
    }

    @Test
    public void resolve_indexEmptyList_throwsCommandException() {
        assertThrows(CommandException.class, "The student index provided is invalid.", ()
                -> new StudentIdentifier(INDEX_FIRST_PERSON).resolve(new ModelManager()));
    }

    @Test
    public void resolve_existingMatriculationNumber_returnsPerson() throws Exception {
        StudentIdentifier identifier = new StudentIdentifier(new MatriculationNumber("A1234567A"));
        assertEquals(ALICE, identifier.resolve(model));
    }

    @Test
    public void resolve_matriculationNumberOutsideFilteredList_returnsPerson() throws Exception {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        StudentIdentifier identifier = new StudentIdentifier(new MatriculationNumber("A1234567B"));
        assertEquals(BENSON, identifier.resolve(model));
    }

    @Test
    public void resolve_nonexistentMatriculationNumber_throwsCommandException() {
        StudentIdentifier identifier = new StudentIdentifier(new MatriculationNumber("A9999999Z"));
        assertThrows(CommandException.class, "No student with matriculation number A9999999Z found.", ()
                -> identifier.resolve(model));
    }

    @Test
    public void resolve_matriculationNumberEmptyAddressBook_throwsCommandException() {
        StudentIdentifier identifier = new StudentIdentifier(new MatriculationNumber("A1234567A"));
        assertThrows(CommandException.class, "No student with matriculation number A1234567A found.", ()
                -> identifier.resolve(new ModelManager()));
    }

    @Test
    public void resolve_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentIdentifier(INDEX_FIRST_PERSON).resolve(null));
        assertThrows(NullPointerException.class, ()
                -> new StudentIdentifier(new MatriculationNumber("A1234567A")).resolve(null));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentIdentifier((Index) null));
        assertThrows(NullPointerException.class, () -> new StudentIdentifier((MatriculationNumber) null));
    }

    @Test
    public void equals() {
        StudentIdentifier firstIndex = new StudentIdentifier(INDEX_FIRST_PERSON);
        StudentIdentifier firstIndexCopy = new StudentIdentifier(INDEX_FIRST_PERSON);
        StudentIdentifier secondIndex = new StudentIdentifier(INDEX_SECOND_PERSON);
        StudentIdentifier firstMatric = new StudentIdentifier(new MatriculationNumber("A1234567A"));
        StudentIdentifier firstMatricCopy = new StudentIdentifier(new MatriculationNumber("A1234567A"));
        StudentIdentifier secondMatric = new StudentIdentifier(new MatriculationNumber("A1234567B"));

        assertTrue(firstIndex.equals(firstIndex));
        assertTrue(firstIndex.equals(firstIndexCopy));
        assertTrue(firstMatric.equals(firstMatricCopy));
        assertFalse(firstIndex.equals(secondIndex));
        assertFalse(firstMatric.equals(secondMatric));
        assertFalse(firstIndex.equals(firstMatric));
        assertFalse(firstMatric.equals(firstIndex));
        assertFalse(firstIndex.equals(null));
        assertFalse(firstIndex.equals(1));
        assertEquals(firstIndex.hashCode(), firstIndexCopy.hashCode());
        assertEquals(firstMatric.hashCode(), firstMatricCopy.hashCode());
    }

    @Test
    public void toStringMethod() {
        StudentIdentifier identifier = new StudentIdentifier(INDEX_FIRST_PERSON);
        String expected = StudentIdentifier.class.getCanonicalName()
                + "{index=" + Optional.of(INDEX_FIRST_PERSON)
                + ", matriculationNumber=" + Optional.empty() + "}";
        assertEquals(expected, identifier.toString());
    }
}
