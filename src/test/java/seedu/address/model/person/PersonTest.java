package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRICULATION_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.classcode.ClassCode;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same matriculation number, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withEmail(VALID_EMAIL_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different matriculation number, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withMatriculationNumber(VALID_MATRICULATION_NUMBER_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // matriculation number differs in case only, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB)
                .withMatriculationNumber(VALID_MATRICULATION_NUMBER_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different matriculation number -> returns false
        editedAlice = new PersonBuilder(ALICE).withMatriculationNumber(VALID_MATRICULATION_NUMBER_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void attendance_changesDataButNotIdentity() {
        Person recordedAlice = new PersonBuilder(ALICE).withAttendance("07-10-2026").build();
        assertEquals(Attendance.empty(), ALICE.getAttendance());
        assertTrue(ALICE.isSamePerson(recordedAlice));
        assertFalse(ALICE.equals(recordedAlice));
        Person copy = new PersonBuilder(recordedAlice).build();
        assertEquals(recordedAlice, copy);
        assertEquals(recordedAlice.hashCode(), copy.hashCode());
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getName(), ALICE.getMatriculationNumber(),
                ALICE.getEmail(), ALICE.getTags(), null));
    }

    @Test
    public void constructor_withoutClassCodes_defaultsToEmpty() {
        Person person = new Person(ALICE.getName(), ALICE.getMatriculationNumber(), ALICE.getEmail(),
                ALICE.getTags());
        assertTrue(person.getClassCodes().isEmpty());

        Person withAttendance = new Person(ALICE.getName(), ALICE.getMatriculationNumber(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getAttendance());
        assertTrue(withAttendance.getClassCodes().isEmpty());
    }

    @Test
    public void constructor_classCodesSet_isCopiedDefensively() {
        Set<ClassCode> classCodes = new HashSet<>();
        classCodes.add(new ClassCode(VALID_CLASS_CODE_CS2103T_T10));
        Person person = new Person(ALICE.getName(), ALICE.getMatriculationNumber(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getAttendance(), classCodes);

        classCodes.add(new ClassCode(VALID_CLASS_CODE_CS3230_T01));

        assertEquals(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)), person.getClassCodes());
    }

    @Test
    public void constructor_nullClassCodes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Person(ALICE.getName(), ALICE.getMatriculationNumber(),
                ALICE.getEmail(), ALICE.getTags(), ALICE.getAttendance(), null));
    }

    @Test
    public void getClassCodes_modifyingReturnedSet_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertThrows(UnsupportedOperationException.class, () -> person.getClassCodes()
                .add(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));
    }

    @Test
    public void isEnrolledIn_containsClassCode_returnsTrue() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertTrue(alice.isEnrolledIn(new ClassCode(VALID_CLASS_CODE_CS2103T_T10)));
    }

    @Test
    public void isEnrolledIn_absentClassCode_returnsFalse() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertFalse(alice.isEnrolledIn(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));
    }

    @Test
    public void isEnrolledIn_nullClassCode_throwsNullPointerException() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertThrows(NullPointerException.class, () -> alice.isEnrolledIn(null));
    }

    @Test
    public void isEnrolled_personHasClasses_returnsTrue() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertTrue(alice.isEnrolled());
    }

    @Test
    public void isEnrolled_personHasNoClasses_returnsFalse() {
        Person bob = new PersonBuilder(BOB).build();
        assertFalse(bob.isEnrolled());
    }

    @Test
    public void withoutClassCode_personNotInGivenClass_throwsAssertionError() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertThrows(AssertionError.class, () -> alice.withoutClassCode(new ClassCode(VALID_CLASS_CODE_CS3230_T01)));
    }

    @Test
    public void withoutClassCode_personInGivenClass_returnsModifiedPerson() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        Person modifiedAlice = alice.withoutClassCode(new ClassCode(VALID_CLASS_CODE_CS2103T_T10));
        Person aliceWithout2103T = new PersonBuilder(ALICE).build();
        assertEquals(aliceWithout2103T, modifiedAlice);
    }

    @Test
    public void withoutClassCodes_personHasClasses_returnsModifiedPerson() {
        Person alice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        Person aliceWithout2103T = new PersonBuilder(ALICE).build();
        Person modifiedAlice = alice.withoutClassCodes();
        assertEquals(aliceWithout2103T, modifiedAlice);
    }

    @Test
    public void withoutClassCodes_personWithoutClasses_throwsAssertionError() {
        Person bob = new PersonBuilder(BOB).build();
        assertThrows(AssertionError.class, bob::withoutClassCodes);
    }

    @Test
    public void equals_differentClassCodes_returnsFalse() {
        Person withClass = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        Person withOtherClass = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS3230_T01).build();

        // class codes differ only -> returns false
        assertFalse(ALICE.equals(withClass));
        assertFalse(withClass.equals(withOtherClass));

        // same class codes, regardless of case -> returns true
        assertTrue(withClass.equals(new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build()));
    }

    @Test
    public void classCodes_changeDataButNotIdentity() {
        Person enrolledAlice = new PersonBuilder(ALICE).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        assertTrue(ALICE.isSamePerson(enrolledAlice));
        assertFalse(ALICE.equals(enrolledAlice));
    }

    @Test
    public void builder_copyConstructor_keepsClassCodes() {
        Person enrolledAlice = new PersonBuilder(ALICE)
                .withClassCodes(VALID_CLASS_CODE_CS2103T_T10, VALID_CLASS_CODE_CS3230_T01).build();
        assertEquals(enrolledAlice, new PersonBuilder(enrolledAlice).build());
    }

    @Test
    public void hashCode_sameClassCodes_sameHashCode() {
        Person first = new PersonBuilder(ALICE)
                .withClassCodes(VALID_CLASS_CODE_CS2103T_T10, VALID_CLASS_CODE_CS3230_T01).build();
        Person second = new PersonBuilder(ALICE)
                .withClassCodes(VALID_CLASS_CODE_CS3230_T01, VALID_CLASS_CODE_CS2103T_T10).build();
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", matriculationNumber="
                + ALICE.getMatriculationNumber()
                + ", email=" + ALICE.getEmail() + ", tags=" + ALICE.getTags()
                + ", attendance=" + ALICE.getAttendance()
                + ", classCodes=" + ALICE.getClassCodes() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
