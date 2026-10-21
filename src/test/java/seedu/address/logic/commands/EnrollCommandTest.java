package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
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

public class EnrollCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private final ClassCode classCode = new ClassCode(VALID_CLASS_CODE_CS2103T_T10);

    @Test
    public void execute_validIndex_enrollsStudent() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person enrolled = new PersonBuilder(student).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, enrolled);

        assertCommandSuccess(new EnrollCommand(INDEX_FIRST_PERSON, classCode), model,
                String.format(EnrollCommand.MESSAGE_SUCCESS, student.getName(), classCode), expectedModel);
    }

    @Test
    public void execute_studentAlreadyInOtherClass_keepsExistingClass() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person inCs3230 = new PersonBuilder(student).withClassCodes(VALID_CLASS_CODE_CS3230_T01).build();
        model.setPerson(student, inCs3230);

        Person expected = new PersonBuilder(student)
                .withClassCodes(VALID_CLASS_CODE_CS3230_T01, VALID_CLASS_CODE_CS2103T_T10).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(inCs3230, expected);

        assertCommandSuccess(new EnrollCommand(INDEX_FIRST_PERSON, classCode), model,
                String.format(EnrollCommand.MESSAGE_SUCCESS, student.getName(), classCode), expectedModel);
    }

    @Test
    public void execute_alreadyEnrolled_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student).withClassCodes(VALID_CLASS_CODE_CS2103T_T10).build());

        assertCommandFailure(new EnrollCommand(INDEX_FIRST_PERSON, classCode), model,
                String.format(EnrollCommand.MESSAGE_DUPLICATE_CLASS, classCode));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new EnrollCommand(outOfBounds, classCode), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        EnrollCommand first = new EnrollCommand(INDEX_FIRST_PERSON, classCode);
        assertTrue(first.equals(first));
        assertTrue(first.equals(new EnrollCommand(INDEX_FIRST_PERSON, new ClassCode("cs2103t-t10"))));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
        assertFalse(first.equals(new EnrollCommand(INDEX_SECOND_PERSON, classCode)));
        assertFalse(first.equals(new EnrollCommand(INDEX_FIRST_PERSON, new ClassCode("CS3230-T01"))));
    }
}
