package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classcode.ClassCode;

/**
 * Jackson-friendly version of {@link ClassCode}.
 */
class JsonAdaptedClassCode {

    private final String classCode;

    /**
     * Constructs a {@code JsonAdaptedClassCode} with the given {@code classCode}.
     */
    @JsonCreator
    public JsonAdaptedClassCode(String classCode) {
        this.classCode = classCode;
    }

    /**
     * Converts a given {@code ClassCode} into this class for Jackson use.
     */
    public JsonAdaptedClassCode(ClassCode source) {
        classCode = source.toString();
    }

    @JsonValue
    public String getClassCode() {
        return classCode;
    }

    /**
     * Converts this Jackson-friendly adapted class code object into the model's {@code ClassCode} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted class code.
     */
    public ClassCode toModelType() throws IllegalValueException {
        if (!ClassCode.isValidClassCode(classCode)) {
            throw new IllegalValueException(ClassCode.MESSAGE_CONSTRAINTS);
        }
        return new ClassCode(classCode);
    }

}
