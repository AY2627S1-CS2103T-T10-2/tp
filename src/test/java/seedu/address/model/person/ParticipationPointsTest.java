package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the validation and value semantics of participation points.
 */
public class ParticipationPointsTest {

    @Test
    public void validPoints_acceptedAndStored() {
        for (String validPoints : List.of(
                "0", "1", "2", "002", "2147483646", "2147483647")) {
            assertTrue(ParticipationPoints.isValidPoints(validPoints), validPoints);
        }

        assertEquals(0, new ParticipationPoints("0").value);
        assertEquals(1, new ParticipationPoints("1").value);
        assertEquals(2, new ParticipationPoints("2").value);
        assertEquals(2, new ParticipationPoints("002").value);
        assertEquals(Integer.MAX_VALUE, new ParticipationPoints("2147483647").value);
    }

    @Test
    public void invalidPoints_rejectedWithoutCreatingValue() {
        List<String> invalidInputs = List.of(
                "", " ", "-1", "-0", "+2", "2.5", "two", "1 2", " 2 ",
                "2147483648", "9999999999999999999999");

        for (String invalidPoints : invalidInputs) {
            assertFalse(ParticipationPoints.isValidPoints(invalidPoints), invalidPoints);

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> new ParticipationPoints(invalidPoints));

            assertEquals(ParticipationPoints.MESSAGE_CONSTRAINTS, exception.getMessage());
        }
    }

    @Test
    public void nullPoints_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ParticipationPoints(null));
        assertThrows(NullPointerException.class, () -> ParticipationPoints.isValidPoints(null));
    }

    @Test
    public void equals() {
        ParticipationPoints points = new ParticipationPoints("2");
        ParticipationPoints samePoints = new ParticipationPoints("002");
        ParticipationPoints differentPoints = new ParticipationPoints("3");

        assertTrue(points.equals(points));
        assertEquals(points, samePoints);
        assertEquals(points.hashCode(), samePoints.hashCode());
        assertFalse(points.equals(differentPoints));
        assertFalse(points.equals(null));
        assertFalse(points.equals("2"));
    }

    @Test
    public void toString_leadingZeros_returnsNormalizedValue() {
        assertEquals("2", new ParticipationPoints("002").toString());
    }
}
