package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.PersonInModuleOrClassPredicate;

class FilterCommandTest {

    @Test
    void equals() {
        PersonInModuleOrClassPredicate firstPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        PersonInModuleOrClassPredicate secondPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));

        FilterCommand firstFilterCommand = new FilterCommand(firstPredicate);
        FilterCommand secondFilterCommand = new FilterCommand(secondPredicate);

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
    }

    @Test
    public void toStringMethod() {
        PersonInModuleOrClassPredicate predicate = new PersonInModuleOrClassPredicate(Set.of(
                new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }
}
