package seedu.address.storage;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.gradebook.Assignment;
import seedu.address.model.gradebook.AssignmentId;

/**
 * Jackson-friendly version of a shared {@link Assignment} definition.
 */
class JsonAdaptedAssignment {
    private final String id;
    private final String name;
    private final String maxMark;

    @JsonCreator
    JsonAdaptedAssignment(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("maxMark") String maxMark) {
        this.id = id;
        this.name = name;
        this.maxMark = maxMark;
    }

    JsonAdaptedAssignment(Assignment source) {
        this(source.getId().toString(), source.getName(), source.getMaxMark().toPlainString());
    }

    /**
     * Converts the saved definition to an assignment, validating every field.
     */
    Assignment toModelType() throws IllegalValueException {
        if (id == null || !AssignmentId.isValidAssignmentId(id)) {
            throw new IllegalValueException(AssignmentId.MESSAGE_CONSTRAINTS);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalValueException(Assignment.MESSAGE_NAME_CONSTRAINTS);
        }
        try {
            return new Assignment(new AssignmentId(id), name, new BigDecimal(maxMark));
        } catch (NumberFormatException | NullPointerException ex) {
            throw new IllegalValueException("Assignment maximum mark must be a valid number.");
        } catch (IllegalArgumentException ex) {
            throw new IllegalValueException(ex.getMessage());
        }
    }
}
