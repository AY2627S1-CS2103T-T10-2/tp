package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.DeleteCommandParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Tests batch deletion through the parser, command and model.
 */
public class DeleteCommandBatchTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void execute_multipleIndices_deletesOriginalTargets() throws Exception {
        Person firstStudent = model.getFilteredPersonList().get(0);
        Person thirdStudent = model.getFilteredPersonList().get(2);

        List<Person> expectedStudents =
                new ArrayList<>(model.getAddressBook().getPersonList());
        expectedStudents.remove(firstStudent);
        expectedStudents.remove(thirdStudent);

        DeleteCommand command = parser.parse(" 1 3");
        command.execute(model);

        assertEquals(expectedStudents, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidIndexInBatch_doesNotDeleteAnyStudent() throws Exception {
        List<Person> studentsBefore =
                List.copyOf(model.getAddressBook().getPersonList());
        int invalidIndex = model.getFilteredPersonList().size() + 1;

        DeleteCommand command = parser.parse(" 1 " + invalidIndex);

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_duplicateIndices_doesNotDeleteAnyStudent() throws Exception {
        List<Person> studentsBefore =
                List.copyOf(model.getAddressBook().getPersonList());

        DeleteCommand command = parser.parse(" 1 1");

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(DeleteCommand.MESSAGE_DUPLICATE_STUDENT, exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_multipleIndicesInFilteredList_deletesDisplayedStudents() throws Exception {
        Person secondStudent = model.getFilteredPersonList().get(1);
        Person thirdStudent = model.getFilteredPersonList().get(2);

        List<Person> expectedStudents =
                new ArrayList<>(model.getAddressBook().getPersonList());
        expectedStudents.remove(secondStudent);
        expectedStudents.remove(thirdStudent);

        model.updateFilteredPersonList(
                person -> person.equals(secondStudent) || person.equals(thirdStudent));
        assertEquals(List.of(secondStudent, thirdStudent), model.getFilteredPersonList());

        DeleteCommand command = parser.parse(" 1 2");
        command.execute(model);

        assertEquals(expectedStudents, model.getAddressBook().getPersonList());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidIndexInFilteredBatch_doesNotDeleteAnyStudent() throws Exception {
        List<Person> studentsBefore =
                List.copyOf(model.getAddressBook().getPersonList());
        Person secondStudent = studentsBefore.get(1);
        Person thirdStudent = studentsBefore.get(2);

        model.updateFilteredPersonList(
                person -> person.equals(secondStudent) || person.equals(thirdStudent));
        assertEquals(List.of(secondStudent, thirdStudent), model.getFilteredPersonList());

        DeleteCommand command = parser.parse(" 1 3");

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
        assertEquals(List.of(secondStudent, thirdStudent), model.getFilteredPersonList());
    }

    @Test
    public void execute_unorderedIndices_deletesOriginalTargets() throws Exception {
        Person firstStudent = model.getFilteredPersonList().get(0);
        Person secondStudent = model.getFilteredPersonList().get(1);
        Person thirdStudent = model.getFilteredPersonList().get(2);

        List<Person> expectedStudents =
                new ArrayList<>(model.getAddressBook().getPersonList());
        expectedStudents.remove(firstStudent);
        expectedStudents.remove(secondStudent);
        expectedStudents.remove(thirdStudent);

        DeleteCommand command = parser.parse(" 3 1 2");
        command.execute(model);

        assertEquals(expectedStudents, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_nonAdjacentDuplicateIndices_doesNotDeleteAnyStudent() throws Exception {
        List<Person> studentsBefore =
                List.copyOf(model.getAddressBook().getPersonList());

        DeleteCommand command = parser.parse(" 1 3 1");

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(DeleteCommand.MESSAGE_DUPLICATE_STUDENT, exception.getMessage());
        assertEquals(studentsBefore, model.getAddressBook().getPersonList());
    }
}
