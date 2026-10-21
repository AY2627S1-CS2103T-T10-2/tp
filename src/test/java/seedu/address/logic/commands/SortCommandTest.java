package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code SortCommand}.
 */
public class SortCommandTest {

    // Deliberately NOT in alphabetical order, and with a lowercase name, so that a missing sort
    // or a case-sensitive sort both make the tests fail.
    private static final Person ZOE = new PersonBuilder().withName("Zoe Tan")
            .withMatriculationNumber("A1000001A").withEmail("zoe@example.com").build();
    private static final Person CHARLIE = new PersonBuilder().withName("Charlie Ho")
            .withMatriculationNumber("A1000002B").withEmail("charlie@example.com").build();
    private static final Person ALICE_LOWERCASE = new PersonBuilder().withName("alice Lim")
            .withMatriculationNumber("A1000003C").withEmail("alice@example.com").build();
    private static final Person BOB = new PersonBuilder().withName("Bob Ng")
            .withMatriculationNumber("A1000004D").withEmail("bob@example.com").build();

    private Model model;

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
        assertCommandSuccess(new SortCommand(), model, SortCommand.MESSAGE_SUCCESS, expectedModel);

        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_sortedTwice_sameResult() throws Exception {
        new SortCommand().execute(model);
        new SortCommand().execute(model);

        assertEquals(List.of(ALICE_LOWERCASE, BOB, CHARLIE, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_unsortedList_doesNotChangeStoredOrder() throws Exception {
        new SortCommand().execute(model);

        // the sort is display-only: the address book (and therefore the saved file) keeps its order
        assertEquals(List.of(ZOE, CHARLIE, ALICE_LOWERCASE, BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filteredList_sortsOnlyShownPersons() throws Exception {
        model.updateFilteredPersonList(person -> !person.equals(CHARLIE));
        new SortCommand().execute(model);

        assertEquals(List.of(ALICE_LOWERCASE, BOB, ZOE), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandSuccess(new SortCommand(), emptyModel, SortCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(), emptyModel.getFilteredPersonList());
    }
}
