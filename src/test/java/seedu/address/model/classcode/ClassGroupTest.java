package seedu.address.model.classcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ClassGroupTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassGroup(null));
    }

    @Test
    public void constructor_invalidClassGroup_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ClassGroup(""));
        assertThrows(IllegalArgumentException.class, () -> new ClassGroup("T 10"));
    }

    @Test
    public void constructor_lowerCase_storedAsUpperCase() {
        assertEquals("T10", new ClassGroup("t10").value);
    }

    @Test
    public void isValidClassGroup() {
        // null class group
        assertThrows(NullPointerException.class, () -> ClassGroup.isValidClassGroup(null));

        // invalid class groups
        assertFalse(ClassGroup.isValidClassGroup("")); // empty string
        assertFalse(ClassGroup.isValidClassGroup(" ")); // spaces only
        assertFalse(ClassGroup.isValidClassGroup("T 10")); // space inside
        assertFalse(ClassGroup.isValidClassGroup("T-10")); // contains separator
        assertFalse(ClassGroup.isValidClassGroup("T10!")); // symbol
        assertFalse(ClassGroup.isValidClassGroup("T10T10")); // too long (6 characters)

        // valid class groups
        assertTrue(ClassGroup.isValidClassGroup("T10"));
        assertTrue(ClassGroup.isValidClassGroup("t10")); // lower case
        assertTrue(ClassGroup.isValidClassGroup("1")); // single character
        assertTrue(ClassGroup.isValidClassGroup("04")); // digits only
        assertTrue(ClassGroup.isValidClassGroup("ABCDE")); // maximum length
    }

    @Test
    public void equals() {
        ClassGroup classGroup = new ClassGroup("T10");

        // same values -> returns true
        assertTrue(classGroup.equals(new ClassGroup("T10")));

        // same object -> returns true
        assertTrue(classGroup.equals(classGroup));

        // different case -> returns true
        assertTrue(classGroup.equals(new ClassGroup("t10")));

        // null -> returns false
        assertFalse(classGroup.equals(null));

        // different types -> returns false
        assertFalse(classGroup.equals(5.0f));

        // different value -> returns false
        assertFalse(classGroup.equals(new ClassGroup("T11")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new ClassGroup("T10").hashCode(), new ClassGroup("t10").hashCode());
    }
}
