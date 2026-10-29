package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.FIONA;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void constructor_andAddPerson_displayStudentsByName() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());
        assertEquals(List.of(BENSON, ALICE), modelManager.getAddressBook().getPersonList());
    }

    @Test
    public void addPerson_afterSort_restoresNameOrder() {
        modelManager.addPerson(BENSON);
        modelManager.sortPersons(Model.COMPARATOR_NAME_ASC.reversed());

        modelManager.addPerson(ALICE);

        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    private ModelManager modelWithAliceBensonCarl() {
        return new ModelManager(new AddressBookBuilder()
                .withPerson(ALICE).withPerson(BENSON).withPerson(CARL).build(), new UserPrefs());
    }

    @Test
    public void updateFindPredicate_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateFindPredicate(null));
    }

    @Test
    public void updateFilterPredicate_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateFilterPredicate(null));
    }

    @Test
    public void updateFindPredicate_withActiveFilter_showsIntersection() {
        modelManager = modelWithAliceBensonCarl();
        modelManager.updateFilterPredicate(p -> p.equals(ALICE) || p.equals(BENSON));
        modelManager.updateFindPredicate(p -> p.equals(BENSON) || p.equals(CARL));
        assertEquals(List.of(BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilterPredicate_withActiveFind_keepsFind() {
        modelManager = modelWithAliceBensonCarl();
        modelManager.updateFindPredicate(p -> p.equals(BENSON) || p.equals(CARL));
        modelManager.updateFilterPredicate(p -> p.equals(ALICE) || p.equals(BENSON));
        assertEquals(List.of(BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFindPredicate_calledTwice_secondReplacesFirst() {
        modelManager = modelWithAliceBensonCarl();
        modelManager.updateFindPredicate(p -> p.equals(ALICE));
        modelManager.updateFindPredicate(p -> p.equals(CARL));
        assertEquals(List.of(CARL), modelManager.getFilteredPersonList());
    }

    @Test
    public void updateFilteredPersonList_clearsBothFilterAndFind() {
        modelManager = modelWithAliceBensonCarl();
        modelManager.updateFilterPredicate(p -> p.equals(ALICE));
        modelManager.updateFindPredicate(p -> p.equals(BENSON));
        assertEquals(List.of(), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(ALICE, BENSON, CARL), modelManager.getFilteredPersonList());
    }

    @Test
    public void addPerson_withActiveFilterAndFind_resetsBoth() {
        modelManager = modelWithAliceBensonCarl();
        modelManager.updateFilterPredicate(p -> p.equals(ALICE));
        modelManager.updateFindPredicate(p -> p.equals(ALICE));

        modelManager.addPerson(FIONA);

        assertEquals(4, modelManager.getFilteredPersonList().size());
    }
}
