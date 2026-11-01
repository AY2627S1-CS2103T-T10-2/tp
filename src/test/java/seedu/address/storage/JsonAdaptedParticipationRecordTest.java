package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.person.ParticipationPoints;
import seedu.address.model.person.ParticipationRecord;

public class JsonAdaptedParticipationRecordTest {

    private static final ClassCode CLASS_CODE = new ClassCode("CS2103T-T10");
    private static final JsonAdaptedClassCode JSON_CLASS_CODE =
            new JsonAdaptedClassCode(CLASS_CODE);

    @Test
    public void toModelType_validDate_returnsRecord() throws Exception {
        JsonAdaptedParticipationRecord adapted =
                new JsonAdaptedParticipationRecord(JSON_CLASS_CODE, "28-10-2026", "2");

        ParticipationRecord expected = new ParticipationRecord(
                CLASS_CODE, LocalDate.of(2026, 10, 28), new ParticipationPoints("2"));

        assertEquals(expected, adapted.toModelType());
    }

    @Test
    public void toModelType_isoDate_throwsException() {
        JsonAdaptedParticipationRecord adapted =
                new JsonAdaptedParticipationRecord(JSON_CLASS_CODE, "2026-10-28", "2");

        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_invalidCalendarDate_throwsException() {
        JsonAdaptedParticipationRecord adapted =
                new JsonAdaptedParticipationRecord(JSON_CLASS_CODE, "31-02-2026", "2");

        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_year0001_returnsRecord() throws Exception {
        JsonAdaptedParticipationRecord adapted =
                new JsonAdaptedParticipationRecord(JSON_CLASS_CODE, "28-10-0001", "2");

        assertEquals(LocalDate.of(1, 10, 28), adapted.toModelType().getDate());
    }
}
