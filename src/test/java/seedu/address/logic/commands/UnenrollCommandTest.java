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
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.Attendance;
import seedu.address.model.person.Email;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UnenrollCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private final ClassCode classCode = new ClassCode(VALID_CLASS_CODE_CS2103T_T10);

    private final Person studentWithClass = new Person(new Name("Alice"), new MatriculationNumber("A1234567A"),
            new Email("e0123456@u.nus.edu"), new HashSet<>(), new Attendance(new HashSet<>()),
            Collections.singleton(classCode));

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
        Person enrolled = new PersonBuilder(student).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        Model newModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        newModel.setPerson(student, enrolled);

        assertCommandSuccess(new UnenrollCommand(INDEX_FIRST_PERSON, classCode), newModel,
                String.format(UnenrollCommand.MESSAGE_SUCCESS, student.getName(), classCode), model);
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
        UnenrollCommand first = new UnenrollCommand(INDEX_FIRST_PERSON, classCode);
        assertTrue(first.equals(first));
        assertTrue(first.equals(new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode("cs2103t-t10"))));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
        assertFalse(first.equals(new UnenrollCommand(INDEX_SECOND_PERSON, classCode)));
        assertFalse(first.equals(new UnenrollCommand(INDEX_FIRST_PERSON, new ClassCode("CS3230-T01"))));

        UnenrollCommand second = new UnenrollCommand(INDEX_FIRST_PERSON);
        assertTrue(second.equals(second));
        assertFalse(second.equals(null));
        assertFalse(second.equals(1));
        assertFalse(second.equals(new UnenrollCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        UnenrollCommand unenrollCommand = new UnenrollCommand(INDEX_FIRST_PERSON, classCode);
        String expected = UnenrollCommand.class.getCanonicalName()
                + "{index=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, classCode=CS2103T-T10}";
        assertEquals(expected, unenrollCommand.toString());
    }
}
