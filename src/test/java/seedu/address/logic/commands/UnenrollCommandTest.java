package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.UnenrollCommand.MESSAGE_MISSING_CLASS;
import static seedu.address.logic.commands.UnenrollCommand.MESSAGE_MISSING_CLASS_ALL;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UnenrollCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private final ClassCode classCode = new ClassCode(VALID_CLASS_CODE_CS2103T_T10);

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnenrollCommand(null));
    }

    @Test
    public void constructor_nullClassCode_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new UnenrollCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void execute_validIndexWithClass_unenrollsStudent() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person enrolledTwice = new PersonBuilder(student)
                .withClassCodes(VALID_CLASS_CODE_CS2103T_T10, VALID_CLASS_CODE_CS3230_T01).build();
        Person enrolled = new PersonBuilder(student).withClassCodes(VALID_CLASS_CODE_CS3230_T01).build();
        Model newModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        newModel.setPerson(student, enrolledTwice);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, enrolled);

        assertCommandSuccess(new UnenrollCommand(INDEX_FIRST_PERSON, classCode), newModel,
                String.format(UnenrollCommand.MESSAGE_SUCCESS, student.getName(), classCode), expectedModel);
    }

    @Test
    public void execute_validIndexWithoutClass_unenrollsStudent() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person enrolledTwice = new PersonBuilder(student)
                .withClassCodes(VALID_CLASS_CODE_CS2103T_T10, VALID_CLASS_CODE_CS3230_T01).build();
        Model newModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        newModel.setPerson(student, enrolledTwice);

        assertCommandSuccess(new UnenrollCommand(INDEX_FIRST_PERSON), newModel,
                String.format(UnenrollCommand.MESSAGE_SUCCESS_ALL, student.getName()), model);
    }

    @Test
    public void execute_notEnrolledWithClass_throwsCommandException() {
        assertCommandFailure(new UnenrollCommand(INDEX_FIRST_PERSON, classCode), model,
                String.format(MESSAGE_MISSING_CLASS, classCode));
    }

    @Test
    public void execute_notEnrolledWithoutClass_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertCommandFailure(new UnenrollCommand(INDEX_FIRST_PERSON), model,
                String.format(MESSAGE_MISSING_CLASS_ALL, student.getName()));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new UnenrollCommand(outOfBounds, classCode), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        // Target-object partitions: unenroll from one class / unenroll from all classes.
        UnenrollCommand withClass = new UnenrollCommand(INDEX_SECOND_PERSON, classCode);
        UnenrollCommand allClasses = new UnenrollCommand(INDEX_SECOND_PERSON);

        // EP: same object -> true.
        assertTrue(withClass.equals(withClass));
        assertTrue(allClasses.equals(allClasses));

        // EP: distinct objects with equal values -> true (class codes are case-insensitive).
        assertTrue(withClass.equals(new UnenrollCommand(Index.fromOneBased(2), new ClassCode("cs2103t-t10"))));
        assertTrue(allClasses.equals(new UnenrollCommand(Index.fromOneBased(2))));

        // EP: null -> false.
        assertFalse(withClass.equals(null));
        assertFalse(allClasses.equals(null));

        // EP: different type -> false.
        assertFalse(withClass.equals(1));
        assertFalse(allClasses.equals(1));

        // EP: different index -> false.
        assertFalse(withClass.equals(new UnenrollCommand(INDEX_FIRST_PERSON, classCode)));
        assertFalse(withClass.equals(new UnenrollCommand(INDEX_THIRD_PERSON, classCode)));
        assertFalse(allClasses.equals(new UnenrollCommand(INDEX_FIRST_PERSON)));
        assertFalse(allClasses.equals(new UnenrollCommand(INDEX_THIRD_PERSON)));

        // EP: same index and mode, different class code -> false.
        assertFalse(withClass.equals(new UnenrollCommand(INDEX_SECOND_PERSON,
                new ClassCode(VALID_CLASS_CODE_CS3230_T01))));

        // EP: same index, different modes -> false in both directions.
        assertFalse(withClass.equals(allClasses));
        assertFalse(allClasses.equals(withClass));
    }

    @Test
    public void toStringMethod() {
        UnenrollCommand unenrollCommand = new UnenrollCommand(INDEX_FIRST_PERSON, classCode);
        String expected = UnenrollCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", classCode=" + classCode + "}";
        assertEquals(expected, unenrollCommand.toString());
    }

    @Test
    public void toStringMethod_withoutClassCode() {
        UnenrollCommand unenrollCommand = new UnenrollCommand(INDEX_FIRST_PERSON);
        String expected = UnenrollCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", classCode=null}";
        assertEquals(expected, unenrollCommand.toString());
    }
}
