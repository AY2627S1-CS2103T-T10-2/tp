package seedu.address.model.classcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ModuleCodeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ModuleCode(null));
    }

    @Test
    public void constructor_invalidModuleCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ModuleCode(""));
        assertThrows(IllegalArgumentException.class, () -> new ModuleCode("CS 2103T"));
    }

    @Test
    public void constructor_lowerCase_storedAsUpperCase() {
        assertEquals("CS2103T", new ModuleCode("cs2103t").value);
    }

    @Test
    public void isValidModuleCode() {
        // null module code
        assertThrows(NullPointerException.class, () -> ModuleCode.isValidModuleCode(null));

        // invalid module codes
        assertFalse(ModuleCode.isValidModuleCode("")); // empty string
        assertFalse(ModuleCode.isValidModuleCode(" ")); // spaces only
        assertFalse(ModuleCode.isValidModuleCode("C2103")); // only 1 letter prefix
        assertFalse(ModuleCode.isValidModuleCode("CSXX2103")); // prefix too long
        assertFalse(ModuleCode.isValidModuleCode("CS210")); // too few digits
        assertFalse(ModuleCode.isValidModuleCode("CS21033")); // too many digits
        assertFalse(ModuleCode.isValidModuleCode("CS2103TXX")); // suffix too long
        assertFalse(ModuleCode.isValidModuleCode("CS 2103T")); // space inside
        assertFalse(ModuleCode.isValidModuleCode("CS2103T!")); // symbol
        assertFalse(ModuleCode.isValidModuleCode("2103CS")); // digits first
        assertFalse(ModuleCode.isValidModuleCode("CS2103-T")); // contains separator

        // valid module codes
        assertTrue(ModuleCode.isValidModuleCode("CS2103T"));
        assertTrue(ModuleCode.isValidModuleCode("cs2103t")); // lower case
        assertTrue(ModuleCode.isValidModuleCode("CS3230")); // no suffix
        assertTrue(ModuleCode.isValidModuleCode("GEA1000")); // 3 letter prefix
        assertTrue(ModuleCode.isValidModuleCode("ACC1701X")); // 3 letter prefix with suffix
    }

    @Test
    public void equals() {
        ModuleCode moduleCode = new ModuleCode("CS2103T");

        // same values -> returns true
        assertTrue(moduleCode.equals(new ModuleCode("CS2103T")));

        // same object -> returns true
        assertTrue(moduleCode.equals(moduleCode));

        // different case -> returns true
        assertTrue(moduleCode.equals(new ModuleCode("cs2103t")));

        // null -> returns false
        assertFalse(moduleCode.equals(null));

        // different types -> returns false
        assertFalse(moduleCode.equals(5.0f));

        // different value -> returns false
        assertFalse(moduleCode.equals(new ModuleCode("CS3230")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new ModuleCode("CS2103T").hashCode(), new ModuleCode("cs2103t").hashCode());
        assertNotEquals(new ModuleCode("CS2103T").hashCode(), new ModuleCode("CS3230").hashCode());
    }
}
