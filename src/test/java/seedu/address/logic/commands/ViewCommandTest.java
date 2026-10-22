package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ViewCommand}.
 */
public class ViewCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        CommandResult expectedResult = new CommandResult("Viewing student: Alice Pauline", person);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_studentWithClassesAndAttendance_returnsStudentDetails() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithAttendance = new PersonBuilder(originalPerson)
                .withAttendance("07-10-2026", "14-10-2026")
                .withClassCodes("CS2103T-T10", "CS2100-T01")
                .build();
        model.setPerson(originalPerson, personWithAttendance);
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        CommandResult expectedResult = new CommandResult("Viewing student: Alice Pauline", personWithAttendance);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        CommandResult expectedResult = new CommandResult("Viewing student: Alice Pauline", person);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(viewCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        ViewCommand viewCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());
        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertTrue(viewFirstCommand.equals(viewFirstCommand));
        assertTrue(viewFirstCommand.equals(new ViewCommand(INDEX_FIRST_PERSON)));
        assertFalse(viewFirstCommand.equals(1));
        assertFalse(viewFirstCommand.equals(null));
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";

        assertEquals(expected, viewCommand.toString());
    }
}
