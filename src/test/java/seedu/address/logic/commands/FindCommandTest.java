package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.MatriculationNumberMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_activeSort_resetsToNameOrder() throws Exception {
        model.sortPersons(Model.COMPARATOR_NAME_ASC.reversed());
        new FindCommand(preparePredicate("Kurz Elle Kunz")).execute(model);

        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        // "urz" -> back of "Kurz" (CARL), "Ell" -> front of "Elle" (ELLE), "ion" -> middle of "Fiona" (FIONA)
        NameContainsKeywordsPredicate predicate = preparePredicate("urz Ell ion");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_matchingMatriculationNumber_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        MatriculationNumberMatchesPredicate predicate =
                new MatriculationNumberMatchesPredicate(ALICE.getMatriculationNumber());
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_nonexistentMatriculationNumber_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        MatriculationNumberMatchesPredicate predicate =
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("A9999999Z"));
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_matriculationNumberAfterNameFilter_findsHiddenPerson() {
        model.updateFilterPredicate(new NameContainsKeywordsPredicate(List.of("Alice")));
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        MatriculationNumberMatchesPredicate predicate =
                new MatriculationNumberMatchesPredicate(BENSON.getMatriculationNumber());
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterFilter_findNarrowsFilteredList() throws Exception {
        model.updateFilterPredicate(preparePredicate("Kurz Elle Kunz")); // CARL, ELLE, FIONA

        NameContainsKeywordsPredicate predicate = preparePredicate("Elle");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(ELLE), model.getFilteredPersonList());
    }

    @Test
    public void execute_findMatchesOnlyOutsideFilter_noPersonFound() throws Exception {
        model.updateFilterPredicate(preparePredicate("Kurz Elle Kunz"));

        new FindCommand(preparePredicate("Alice")).execute(model); // ALICE is outside the filter

        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_secondFindUnderFilter_replacesFirstFind() throws Exception {
        model.updateFilterPredicate(preparePredicate("Kurz Elle Kunz"));

        new FindCommand(preparePredicate("Elle")).execute(model);
        new FindCommand(preparePredicate("Carl")).execute(model);

        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
