package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;

/**
 * Jackson-friendly version of a participation record.
 */
class JsonAdaptedParticipationRecord {

    public static final String MISSING_FIELD_MESSAGE_FORMAT =
            "Participation record's %s field is missing!";
    public static final String MESSAGE_INVALID_DATE =
            "Participation record date must be a valid ISO date, e.g. 2026-10-28.";

    @JsonProperty("classCode")
    private final JsonAdaptedClassCode classCode;
    @JsonProperty("date")
    private final String date;
    @JsonProperty("points")
    private final String points;

    /**
     * Constructs an adapted participation record from its stored fields.
     * Validation is performed when converting to the model.
     */
    @JsonCreator
    public JsonAdaptedParticipationRecord(
            @JsonProperty("classCode") JsonAdaptedClassCode classCode,
            @JsonProperty("date") String date,
            @JsonProperty("points") String points) {
        this.classCode = classCode;
        this.date = date;
        this.points = points;
    }

    /**
     * Converts a model record into its storage representation.
     */
    public JsonAdaptedParticipationRecord(ParticipationRecord source) {
        requireNonNull(source);
        classCode = new JsonAdaptedClassCode(source.getClassCode());
        date = source.getDate().toString();
        points = source.getPoints().toString();
    }

    /**
     * Converts the stored record into a validated model record.
     *
     * @throws IllegalValueException if a required field is missing or invalid
     */
    public ParticipationRecord toModelType() throws IllegalValueException {
        if (classCode == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "classCode"));
        }
        if (date == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        if (points == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "points"));
        }

        final ClassCode modelClassCode = classCode.toModelType();
        final LocalDate modelDate = parseStoredDate();

        if (!ParticipationPoints.isValidPoints(points)) {
            throw new IllegalValueException(ParticipationPoints.MESSAGE_CONSTRAINTS);
        }
        final ParticipationPoints modelPoints = new ParticipationPoints(points);

        return new ParticipationRecord(modelClassCode, modelDate, modelPoints);
    }

    /**
     * Parses the ISO date used in storage.
     */
    private LocalDate parseStoredDate() throws IllegalValueException {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(
                    MESSAGE_INVALID_DATE + " Found: " + date);
        }
    }
}
