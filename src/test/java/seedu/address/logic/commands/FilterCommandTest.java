package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.FilterCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonInModuleOrClassPredicate;
import seedu.address.testutil.PersonBuilder;

class FilterCommandTest {

    @Test
    public void execute_emptyAddressBook_returnsNoMatches() throws ParseException {
        Model model = new ModelManager();
        CommandResult result = new FilterCommandParser().parse("CS3230").execute(model);
        assertEquals(FilterCommand.MESSAGE_NO_SUCH_CLASS, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_zeroOneAndMultipleMatches_replacesPreviousFilter() throws ParseException {
        Person classMatch = new PersonBuilder().withName("Alice").withMatriculationNumber("A1234567A")
                .withClassCodes("CS2103T-T10").build();
        Person moduleMatch = new PersonBuilder().withName("Bob").withMatriculationNumber("A1234568A")
                .withClassCodes("CS3230-T01").build();
        Person bothMatches = new PersonBuilder().withName("Carol").withMatriculationNumber("A1234569A")
                .withClassCodes("CS2103T-T10", "CS3230-T02").build();
        Person noMatch = new PersonBuilder().withName("Dave").withMatriculationNumber("A1234570A")
                .withClassCodes("CS2040-T10").build();
        Model model = new ModelManager();
        List<Person> students = List.of(classMatch, moduleMatch, bothMatches, noMatch);
        students.forEach(model::addPerson);
        FilterCommandParser parser = new FilterCommandParser();

        // Zero results from a populated book.
        CommandResult result = parser.parse("CS9999").execute(model);
        assertEquals(FilterCommand.MESSAGE_NO_SUCH_CLASS, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());

        // The next command searches the whole book even when the displayed list is empty.
        result = parser.parse("CS2040-T10").execute(model);
        assertEquals(String.format(FilterCommand.MESSAGE_SUCCESS, 1), result.getFeedbackToUser());
        assertEquals(List.of(noMatch), model.getFilteredPersonList());

        // OR semantics, overlapping matches and duplicate keywords must not duplicate students.
        result = parser.parse("cs2103t-t10 CS3230 cs3230").execute(model);
        assertEquals(String.format(FilterCommand.MESSAGE_SUCCESS, 3), result.getFeedbackToUser());
        assertEquals(List.of(classMatch, moduleMatch, bothMatches), model.getFilteredPersonList());
        assertEquals(students, model.getAddressBook().getPersonList());
    }

    @Test
    void equals() throws ParseException {
        PersonInModuleOrClassPredicate firstPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)),
                        Set.of(new ModuleCode("CS2103T")));
        PersonInModuleOrClassPredicate secondPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)),
                        Set.of(new ModuleCode("CS2103T")));
        PersonInModuleOrClassPredicate thirdPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)),
                        Set.of(new ModuleCode("CS3230")));

        FilterCommand firstFilterCommand = new FilterCommand(firstPredicate);
        FilterCommand secondFilterCommand = new FilterCommand(secondPredicate);
        FilterCommand thirdFilterCommand = new FilterCommand(thirdPredicate);

        //same object -> returns true
        assertTrue(firstFilterCommand.equals(firstFilterCommand));

        //same values -> return true
        FilterCommand firstFilterCommandCopy = new FilterCommand(firstPredicate);
        assertTrue(firstFilterCommand.equals(firstFilterCommandCopy));

        //different types -> return false
        assertFalse(firstFilterCommand.equals(1));
        assertFalse(firstFilterCommand.equals(null));

        //different set of ClassCode -> return false
        assertFalse(firstFilterCommand.equals(secondFilterCommand));

        //different set of ModuleCode -> return false
        assertFalse(thirdFilterCommand.equals(secondFilterCommand));
    }

    @Test
    public void toStringMethod() throws ParseException {
        PersonInModuleOrClassPredicate predicate = new PersonInModuleOrClassPredicate(Set.of(
                new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), Set.of(new ModuleCode("CS2103T")));
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }
}
