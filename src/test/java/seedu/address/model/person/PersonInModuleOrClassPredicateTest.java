package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;
import seedu.address.testutil.PersonBuilder;

class PersonInModuleOrClassPredicateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonInModuleOrClassPredicate(null, null));
        assertThrows(NullPointerException.class, () -> new PersonInModuleOrClassPredicate(null, Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonInModuleOrClassPredicate(Set.of(), null));
    }

    @Test
    public void constructor_bothSetsEmpty_throwsParseException() {
        assertThrows(ParseException.class, () -> new PersonInModuleOrClassPredicate(Set.of(), Set.of()));
    }

    @Test
    public void test_moduleOnly_matchesAnyGroupButRequiresExactModule() throws ParseException {
        PersonInModuleOrClassPredicate predicate = new PersonInModuleOrClassPredicate(Set.of(),
                Set.of(new ModuleCode("cs2103t"), new ModuleCode("CS3230")));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T10").build()));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T11").build()));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS3230-T01").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS2103-T10").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS2103TT-T10").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void test_classOnly_requiresBothModuleAndGroup() throws ParseException {
        PersonInModuleOrClassPredicate predicate = new PersonInModuleOrClassPredicate(
                Set.of(new ClassCode("cs2103t-t10"), new ClassCode("CS3230-T01")), Set.of());
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T10").build()));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS3230-T01").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T11").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS3230-T10").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T100").build()));
        assertFalse(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void test_mixedKeywords_matchesEitherPartition() throws ParseException {
        PersonInModuleOrClassPredicate predicate = new PersonInModuleOrClassPredicate(
                Set.of(new ClassCode("CS2103T-T10")), Set.of(new ModuleCode("CS3230")));
        // Class-only match, module-only match, both, and neither.
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T10").build()));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS3230-T02").build()));
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T10", "CS3230-T02").build()));
        assertFalse(predicate.test(new PersonBuilder().withClassCodes("CS2103T-T11", "CS2040-T10").build()));
        // A single match among several enrolments is sufficient.
        assertTrue(predicate.test(new PersonBuilder().withClassCodes("CS2040-T10", "CS3230-T02").build()));
    }

    @Test
    public void equals_sameClassesDifferentModules_returnsFalse() throws ParseException {
        Set<ClassCode> classes = Set.of(new ClassCode("CS2103T-T10"));
        PersonInModuleOrClassPredicate first = new PersonInModuleOrClassPredicate(classes,
                Set.of(new ModuleCode("CS3230")));
        PersonInModuleOrClassPredicate second = new PersonInModuleOrClassPredicate(classes,
                Set.of(new ModuleCode("CS2040")));
        assertNotEquals(first, second);
        assertNotEquals(first, new PersonInModuleOrClassPredicate(classes, Set.of()));
    }

    @Test
    public void equals() throws ParseException {

        //same object
        PersonInModuleOrClassPredicate firstPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)),
                        Set.of(new ModuleCode("CS3230")));
        assertTrue(firstPredicate.equals(firstPredicate));

        //different object, same ClassCode
        PersonInModuleOrClassPredicate secondPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)),
                        Set.of(new ModuleCode("CS3230")));
        assertEquals(firstPredicate, secondPredicate);

        //different object, different ClassCode
        PersonInModuleOrClassPredicate thirdPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01)),
                        Set.of(new ModuleCode("CS3230")));
        assertNotEquals(firstPredicate, thirdPredicate);

        //multiple ClassCode, equal Set of ClassCodes
        PersonInModuleOrClassPredicate fourthPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), Set.of(new ModuleCode("CS3230")));
        PersonInModuleOrClassPredicate fifthPredicate =
                new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS3230_T01),
                        new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), Set.of(new ModuleCode("CS3230")));
        assertEquals(fifthPredicate, fourthPredicate);

        //multiple ClassCode, different Set of ClassCodes
        assertNotEquals(fifthPredicate, thirdPredicate);

    }

}
