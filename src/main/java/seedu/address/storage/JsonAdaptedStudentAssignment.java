package seedu.address.storage;

import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.gradebook.AssignmentId;
import seedu.address.model.gradebook.Mark;
import seedu.address.model.person.MatriculationNumber;

/**
 * Jackson-friendly version of one student's assignment and optional mark.
 */
class JsonAdaptedStudentAssignment {
    private final String matriculationNumber;
    private final String assignmentId;
    private final String mark;

    @JsonCreator
    JsonAdaptedStudentAssignment(@JsonProperty("matriculationNumber") String matriculationNumber,
            @JsonProperty("assignmentId") String assignmentId, @JsonProperty("mark") String mark) {
        this.matriculationNumber = matriculationNumber;
        this.assignmentId = assignmentId;
        this.mark = mark;
    }

    JsonAdaptedStudentAssignment(MatriculationNumber student, AssignmentId id, Optional<Mark> mark) {
        this(student.toString(), id.toString(), mark.map(Mark::toString).orElse(null));
    }

    /**
     * Adds a validated student assignment to an address book containing its student and definition.
     */
    void addTo(AddressBook addressBook) throws IllegalValueException {
        if (matriculationNumber == null
                || !MatriculationNumber.isValidMatriculationNumber(matriculationNumber)) {
            throw new IllegalValueException(MatriculationNumber.MESSAGE_CONSTRAINTS);
        }
        if (assignmentId == null || !AssignmentId.isValidAssignmentId(assignmentId)) {
            throw new IllegalValueException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        MatriculationNumber student = new MatriculationNumber(matriculationNumber);
        AssignmentId id = new AssignmentId(assignmentId);
        Mark modelMark = null;
        if (mark != null) {
            try {
                modelMark = new Mark(new BigDecimal(mark));
            } catch (NumberFormatException ex) {
                throw new IllegalValueException("Assignment mark must be a valid number.");
            } catch (IllegalArgumentException ex) {
                throw new IllegalValueException(ex.getMessage());
            }
        }
        try {
            addressBook.assignStudent(student, id);
            if (modelMark != null) {
                addressBook.recordMark(student, id, modelMark);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalValueException(ex.getMessage());
        }
    }
}
