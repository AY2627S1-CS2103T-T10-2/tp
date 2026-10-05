package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MatriculationNumberTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatriculationNumber(null));
    }

    @Test
    public void constructor_invalidMatriculationNumber_throwsIllegalArgumentException() {
        String invalidMatriculationNumber = "";
        assertThrows(IllegalArgumentException.class, () -> new MatriculationNumber(invalidMatriculationNumber));
    }

    @Test
    public void isValidMatriculationNumber() {
        // null matriculation number
        assertThrows(NullPointerException.class, () -> MatriculationNumber.isValidMatriculationNumber(null));

        // invalid matriculation number
        assertFalse(MatriculationNumber.isValidMatriculationNumber("")); // empty string
        assertFalse(MatriculationNumber.isValidMatriculationNumber("A1234567")); // missing last character
        assertFalse(MatriculationNumber.isValidMatriculationNumber("A12345678A")); // too many digits
        assertFalse(MatriculationNumber.isValidMatriculationNumber("A12345678")); // last character not a letter

        // valid matriculation number
        assertTrue(MatriculationNumber.isValidMatriculationNumber("A1234567A")); // valid format
    }

    @Test
    public void equals() {
        MatriculationNumber matriculationNumber = new MatriculationNumber("A1234567A");

        // same values -> returns true
        assertTrue(matriculationNumber.equals(new MatriculationNumber("A1234567A")));

        // same object -> returns true
        assertTrue(matriculationNumber.equals(matriculationNumber));

        // null -> returns false
        assertFalse(matriculationNumber.equals(null));

        // different types -> returns false
        assertFalse(matriculationNumber.equals(5.0f));

        // different values -> returns false
        assertFalse(matriculationNumber.equals(new MatriculationNumber("A1234567B")));
    }

    @Test
    public void constructor_lowercaseInput_normalisedToUppercase() {
        assertEquals("A1234567A", new MatriculationNumber("a1234567a").value);
    }
}
