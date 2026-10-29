package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Gradebook;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AssignmentCommandTest {
    private final AssignmentId id = new AssignmentId("CS2103T:A1");
    private final Assignment assignment = new Assignment(id, "Assignment One", new BigDecimal("20"));
    private final MatriculationNumber student = ALICE.getMatriculationNumber();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    public void setUp() {
        model.setPerson(ALICE, ALICE.withClassCode(new ClassCode("CS2103T-T10")));
        model.setPerson(BENSON, BENSON.withClassCode(new ClassCode("CS2103T-T11")));
    }

    @Test
    public void execute_newAssignment_addsDefinitionAndUnmarkedStudentEntry() {
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.addAssignmentForStudent(assignment, student);
        assertCommandSuccess(new AssignmentCommand(student, assignment), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, student), expected);
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
    }

    @Test
    public void execute_existingAssignment_reusesDefinitionForAnotherStudent() {
        model.addAssignmentForStudent(assignment, student);
        MatriculationNumber otherStudent = BENSON.getMatriculationNumber();
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.assignStudent(otherStudent, id);
        assertCommandSuccess(new AssignmentCommand(otherStudent, id), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, otherStudent), expected);
        assertEquals(1, model.getAddressBook().getGradebook().getAssignments().size());
    }

    @Test
    public void execute_duplicateNameOrId_rejectsWithoutChangingModel() {
        model.addAssignmentForStudent(assignment, student);
        MatriculationNumber otherStudent = BENSON.getMatriculationNumber();
        Assignment sameName = new Assignment(new AssignmentId("CS2103T:A2"),
                " assignment   one ", new BigDecimal("25"));
        assertCommandFailure(new AssignmentCommand(otherStudent, sameName), model,
                Gradebook.MESSAGE_DUPLICATE_NAME);
        Assignment sameId = new Assignment(id, "Another Name", new BigDecimal("25"));
        assertCommandFailure(new AssignmentCommand(otherStudent, sameId), model,
                Gradebook.MESSAGE_DUPLICATE_ID);
    }

    @Test
    public void execute_alreadyAssigned_rejectsWithoutChangingModel() {
        model.addAssignmentForStudent(assignment, student);
        assertCommandFailure(new AssignmentCommand(student, id), model,
                Gradebook.MESSAGE_DUPLICATE_STUDENT_ASSIGNMENT);
    }

    @Test
    public void execute_unknownStudentOrAssignment_rejectsWithoutChangingModel() {
        MatriculationNumber unknownStudent = new MatriculationNumber("A9999999Z");
        assertCommandFailure(new AssignmentCommand(unknownStudent, assignment), model,
                AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        assertCommandFailure(new AssignmentCommand(unknownStudent, id), model,
                AddressBook.MESSAGE_STUDENT_NOT_FOUND);
        assertCommandFailure(new AssignmentCommand(student, id), model,
                Gradebook.MESSAGE_UNKNOWN_ASSIGNMENT);
    }

    @Test
    public void execute_noEnrollment_rejectsBothFormsWithoutChangingModel() {
        assertBothFormsRejectUnenrolledStudent(ALICE);
    }

    @Test
    public void execute_onlyOtherModuleEnrollment_rejectsBothFormsWithoutChangingModel() {
        assertBothFormsRejectUnenrolledStudent(ALICE.withClassCode(new ClassCode("CS3230-T01")));
    }

    @Test
    public void execute_onlyModulePrefixMatches_rejectsBothFormsWithoutChangingModel() {
        assertBothFormsRejectUnenrolledStudent(ALICE.withClassCode(new ClassCode("CS2103-T10")));
    }

    @Test
    public void execute_multipleClassesAndMixedCaseModule_acceptsBothForms() throws Exception {
        Person enrolled = new PersonBuilder(ALICE).withClassCodes("CS3230-T01", "cs2103t-l02").build();
        model.setPerson(model.getAddressBook().getPersonList().get(0), enrolled);
        Assignment mixedCaseAssignment = new Assignment(new AssignmentId("cS2103t:a2"),
                "Assignment Two", BigDecimal.TEN);
        new AssignmentCommand(student, mixedCaseAssignment).execute(model);
        model.addAssignment(assignment);
        new AssignmentCommand(student, new AssignmentId("cs2103t:a1")).execute(model);

        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student)
                .get(mixedCaseAssignment.getId()));
    }

    @Test
    public void execute_studentOutsideFilter_canBeAssigned() {
        model.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        assertFalse(model.getFilteredPersonList().stream()
                .anyMatch(person -> person.getMatriculationNumber().equals(student)));
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(student));
        expected.addAssignmentForStudent(assignment, student);
        assertCommandSuccess(new AssignmentCommand(student, assignment), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, student), expected);
        MatriculationNumber otherStudent = BENSON.getMatriculationNumber();
        model.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(otherStudent));
        expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> !person.getMatriculationNumber().equals(otherStudent));
        expected.assignStudent(otherStudent, id);
        assertCommandSuccess(new AssignmentCommand(otherStudent, id), model,
                String.format(AssignmentCommand.MESSAGE_SUCCESS, id, otherStudent), expected);
    }

    @Test
    public void execute_unenrollLastClass_preservesWorkAndAllowsMarking() throws Exception {
        assertUnenrollmentPreservesWork(false);
    }

    @Test
    public void execute_unenrollAllClasses_preservesWorkAndAllowsMarking() throws Exception {
        assertUnenrollmentPreservesWork(true);
    }

    @Test
    public void equals() {
        AssignmentCommand newAssignment = new AssignmentCommand(student, assignment);
        assertTrue(newAssignment.equals(new AssignmentCommand(student, assignment)));
        assertFalse(newAssignment.equals(new AssignmentCommand(student, id)));
        assertFalse(newAssignment.equals(new AssignmentCommand(BENSON.getMatriculationNumber(), assignment)));
        assertFalse(newAssignment.equals(null));
    }

    private void assertBothFormsRejectUnenrolledStudent(Person targetStudent) {
        model.setPerson(model.getAddressBook().getPersonList().get(0), targetStudent);
        model.addAssignmentForStudent(assignment, BENSON.getMatriculationNumber());
        model.recordMark(BENSON.getMatriculationNumber(), id, new Mark(BigDecimal.TEN));
        Assignment newWork = new Assignment(new AssignmentId("CS2103T:A2"), "Assignment Two", BigDecimal.TEN);
        assertCommandFailure(new AssignmentCommand(student, newWork), model,
                String.format(AssignmentCommand.MESSAGE_STUDENT_NOT_ENROLLED,
                        newWork.getId(), student, newWork.getId().getModuleCode()));
        assertCommandFailure(new AssignmentCommand(student, id), model,
                String.format(AssignmentCommand.MESSAGE_STUDENT_NOT_ENROLLED, id, student, id.getModuleCode()));
    }

    private void assertUnenrollmentPreservesWork(boolean allClasses) throws Exception {
        model.addAssignmentForStudent(assignment, student);
        model.recordMark(student, id, new Mark(BigDecimal.ZERO));
        Assignment unmarkedWork = new Assignment(new AssignmentId("CS2103T:A2"),
                "Assignment Two", BigDecimal.TEN);
        model.addAssignmentForStudent(unmarkedWork, student);
        if (allClasses) {
            new UnenrollCommand(Index.fromOneBased(1)).execute(model);
        } else {
            new UnenrollCommand(Index.fromOneBased(1), new ClassCode("CS2103T-T10")).execute(model);
        }
        assertEquals(Optional.of(new Mark(BigDecimal.ZERO)),
                model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student)
                .get(unmarkedWork.getId()));
        new MarkCommand(student, unmarkedWork.getId(), new Mark(BigDecimal.TEN)).execute(model);
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)), model.getAddressBook().getGradebook()
                .getMarksForStudent(student).get(unmarkedWork.getId()));
        new MarkCommand(student, id, new Mark(BigDecimal.TEN)).execute(model);
        assertEquals(Optional.of(new Mark(BigDecimal.TEN)),
                model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
        model.unassignStudent(student, id);
        assertCommandFailure(new AssignmentCommand(student, id), model,
                String.format(AssignmentCommand.MESSAGE_STUDENT_NOT_ENROLLED, id, student, id.getModuleCode()));
        new EnrollCommand(Index.fromOneBased(1), new ClassCode("CS2103T-T11")).execute(model);
        new AssignmentCommand(student, id).execute(model);
        assertEquals(Optional.empty(), model.getAddressBook().getGradebook().getMarksForStudent(student).get(id));
    }
}
