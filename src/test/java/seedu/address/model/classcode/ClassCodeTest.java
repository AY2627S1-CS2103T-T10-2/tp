package seedu.address.model.classcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ClassCodeTest {

    @Test
    public void constructor_nullString_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassCode((String) null));
    }

    @Test
    public void constructor_nullParts_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassCode(null, new ClassGroup("T10")));
        assertThrows(NullPointerException.class, () -> new ClassCode(new ModuleCode("CS2103T"), null));
    }

    @Test
    public void constructor_invalidClassCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ClassCode(""));
        assertThrows(IllegalArgumentException.class, () -> new ClassCode("CS2103T"));
    }

    @Test
    public void constructor_validString_splitsIntoModuleAndGroup() {
        ClassCode classCode = new ClassCode("cs2103t-t10");
        assertEquals(new ModuleCode("CS2103T"), classCode.getModuleCode());
        assertEquals(new ClassGroup("T10"), classCode.getClassGroup());
    }

    @Test
    public void constructor_fromParts_equalsConstructorFromString() {
        ClassCode fromParts = new ClassCode(new ModuleCode("CS2103T"), new ClassGroup("T10"));
        assertEquals(new ClassCode("CS2103T-T10"), fromParts);
    }

    @Test
    public void isValidClassCode() {
        // null class code
        assertThrows(NullPointerException.class, () -> ClassCode.isValidClassCode(null));

        // invalid class codes
        assertFalse(ClassCode.isValidClassCode("")); // empty string
        assertFalse(ClassCode.isValidClassCode(" ")); // spaces only
        assertFalse(ClassCode.isValidClassCode("CS2103T")); // missing group
        assertFalse(ClassCode.isValidClassCode("CS2103T-")); // empty group
        assertFalse(ClassCode.isValidClassCode("-T10")); // empty module
        assertFalse(ClassCode.isValidClassCode("CS2103T-T10-X")); // too many parts
        assertFalse(ClassCode.isValidClassCode("CS2103T T10")); // wrong separator
        assertFalse(ClassCode.isValidClassCode("CS2103T_T10")); // wrong separator
        assertFalse(ClassCode.isValidClassCode("CS21-T10")); // invalid module
        assertFalse(ClassCode.isValidClassCode("CS2103T-T 10")); // invalid group
        assertFalse(ClassCode.isValidClassCode(" CS2103T-T10")); // leading space

        // valid class codes
        assertTrue(ClassCode.isValidClassCode("CS2103T-T10"));
        assertTrue(ClassCode.isValidClassCode("cs2103t-t10")); // lower case
        assertTrue(ClassCode.isValidClassCode("CS3230-1")); // numeric group
        assertTrue(ClassCode.isValidClassCode("GEA1000-04")); // 3 letter module, numeric group
    }

    @Test
    public void toString_lowerCaseInput_returnsNormalisedUpperCase() {
        assertEquals("CS2103T-T10", new ClassCode("cs2103t-t10").toString());
    }

    @Test
    public void equals() {
        ClassCode classCode = new ClassCode("CS2103T-T10");

        // same values -> returns true
        assertTrue(classCode.equals(new ClassCode("CS2103T-T10")));

        // same object -> returns true
        assertTrue(classCode.equals(classCode));

        // different case -> returns true
        assertTrue(classCode.equals(new ClassCode("cs2103t-t10")));

        // null -> returns false
        assertFalse(classCode.equals(null));

        // different types -> returns false
        assertFalse(classCode.equals(5.0f));

        // different group -> returns false
        assertFalse(classCode.equals(new ClassCode("CS2103T-T11")));

        // different module, same group -> returns false
        assertFalse(classCode.equals(new ClassCode("CS3230-T10")));
    }

    @Test
    public void hashCode_equalClassCodes_sameHashCode() {
        assertEquals(new ClassCode("CS2103T-T10").hashCode(), new ClassCode("cs2103t-t10").hashCode());
    }
}
