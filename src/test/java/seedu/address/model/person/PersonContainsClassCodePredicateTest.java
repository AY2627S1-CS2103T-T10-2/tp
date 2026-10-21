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

class PersonContainsClassCodePredicateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonContainsClassCodePredicate(null));
    }

    @Test
    public void equals() {

        //same object
        PersonContainsClassCodePredicate firstPredicate =
                new PersonContainsClassCodePredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertTrue(firstPredicate.equals(firstPredicate));

        //different object, same ClassCode
        PersonContainsClassCodePredicate secondPredicate =
                new PersonContainsClassCodePredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertEquals(firstPredicate, secondPredicate);

        //different object, different ClassCode
        PersonContainsClassCodePredicate thirdPredicate =
                new PersonContainsClassCodePredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));
        assertNotEquals(firstPredicate, thirdPredicate);

        //multiple ClassCode, equal Set of ClassCodes
        PersonContainsClassCodePredicate fourthPredicate =
                new PersonContainsClassCodePredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        PersonContainsClassCodePredicate fifthPredicate =
                new PersonContainsClassCodePredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
        assertEquals(fifthPredicate, fourthPredicate);

        //multiple ClassCode, different Set of ClassCodes
        assertNotEquals(fifthPredicate, thirdPredicate);

    }

}
