package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.classcode.ClassCode;

class PersonInModuleOrClassPredicateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonInModuleOrClassPredicate(null));
    }

    @Test
    public void equals() {

        //same object
        PersonInModuleOrClassPredicate firstPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertTrue(firstPredicate.equals(firstPredicate));

        //different object, same ClassCode
        PersonInModuleOrClassPredicate secondPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertEquals(firstPredicate, secondPredicate);

        //different object, different ClassCode
        PersonInModuleOrClassPredicate thirdPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));
        assertNotEquals(firstPredicate, thirdPredicate);

        //multiple ClassCode, equal Set of ClassCodes
        PersonInModuleOrClassPredicate fourthPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        PersonInModuleOrClassPredicate fifthPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertEquals(fifthPredicate, fourthPredicate);

        //multiple ClassCode, different Set of ClassCodes
        assertNotEquals(fifthPredicate, thirdPredicate);

    }

}
