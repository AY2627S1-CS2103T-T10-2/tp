package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class StudentDetailsSelectionTest {
    private Model model;
    private StudentDetailsSelection selection;

    @BeforeEach
    void setUp() {
        model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        selection = new StudentDetailsSelection(model.getAddressBook().getPersonList());
        selection.select(ALICE);
    }

    @Test
    void editSelectedStudent_tracksReplacementAndSubsequentDeletion() {
        Person edited = new PersonBuilder(ALICE).withName("Updated Alice")
                .withMatriculationNumber("A7654321Z").withEmail("updated@example.com")
                .withTags("updated").build();
        model.setPerson(ALICE, edited);
        assertSame(edited, selection.personProperty().get());

        model.deletePerson(edited);
        assertNull(selection.personProperty().get());
    }

    @Test
    void changeAttendanceAndClasses_tracksLatestDetails() {
        Person updated = new PersonBuilder(ALICE).withAttendance("28-10-2026")
                .withClassCodes("CS2103T-T01").build();
        model.setPerson(ALICE, updated);
        assertSame(updated, selection.personProperty().get());
    }

    @Test
    void deleteSelectedStudent_clearsSelectionWithoutSelectingNextStudent() {
        model.deletePerson(ALICE);
        assertNull(selection.personProperty().get());
        model.addPerson(ALICE);
        assertNull(selection.personProperty().get());
    }

    @Test
    void clearAddressBook_clearsSelection() {
        model.setAddressBook(new AddressBook());
        assertNull(selection.personProperty().get());
    }

    @Test
    void modifyAnotherStudent_keepsSelection() {
        Person edited = new PersonBuilder(BENSON).withName("Updated Benson").build();
        model.setPerson(BENSON, edited);
        model.deletePerson(edited);
        assertSame(ALICE, selection.personProperty().get());
    }

    @Test
    void filterAndSort_keepsSelectionAndContinuesTrackingHiddenStudent() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed());
        assertSame(ALICE, selection.personProperty().get());

        Person edited = new PersonBuilder(ALICE).withName("Hidden Alice").build();
        model.setPerson(ALICE, edited);
        assertSame(edited, selection.personProperty().get());
        model.deletePerson(edited);
        assertNull(selection.personProperty().get());
    }

    @Test
    void selectAnotherStudent_tracksNewSelection() {
        selection.select(BENSON);
        model.deletePerson(ALICE);
        assertSame(BENSON, selection.personProperty().get());
        model.deletePerson(BENSON);
        assertNull(selection.personProperty().get());
    }

    @Test
    void clearSelection_doesNotRestoreSelectionOnEdit() {
        selection.clear();
        model.setPerson(ALICE, new PersonBuilder(ALICE).withName("Updated Alice").build());
        assertNull(selection.personProperty().get());
    }
}
