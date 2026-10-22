package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.Test;

public class MatriculationNumberMatchesPredicateTest {

    @Test
    public void test_matchingMatriculationNumber_returnsTrue() {
        MatriculationNumberMatchesPredicate predicate =
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("a1234567a"));

        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_differentMatriculationNumber_returnsFalse() {
        MatriculationNumberMatchesPredicate predicate =
                new MatriculationNumberMatchesPredicate(ALICE.getMatriculationNumber());

        assertFalse(predicate.test(BENSON));
    }

    @Test
    public void equals() {
        MatriculationNumberMatchesPredicate firstPredicate =
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("A1234567A"));
        MatriculationNumberMatchesPredicate samePredicate =
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("a1234567a"));
        MatriculationNumberMatchesPredicate differentPredicate =
                new MatriculationNumberMatchesPredicate(
                        new MatriculationNumber("A1234567B"));

        assertTrue(firstPredicate.equals(firstPredicate));
        assertEquals(firstPredicate, samePredicate);
        assertEquals(firstPredicate.hashCode(), samePredicate.hashCode());
        assertFalse(firstPredicate.equals(differentPredicate));
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals("A1234567A"));
    }
}
