package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.classcode.ClassCode;

/**
 * Stores one student's participation records, with at most one record per class and date.
 * Updates return a new history and leave the original history unchanged.
 */
public final class ParticipationHistory {

    public static final String MESSAGE_DUPLICATE_SESSION =
            "Participation history contains more than one record for the same class and date.";
    public static final String MESSAGE_RECORD_NOT_FOUND =
            "No participation record exists for the specified class and date.";

    private final List<ParticipationRecord> records;

    /**
     * Creates a history from records in their existing order.
     *
     * @throws NullPointerException if the list or any record is null
     * @throws IllegalArgumentException if multiple records refer to the same session
     */
    public ParticipationHistory(List<ParticipationRecord> records) {
        List<ParticipationRecord> copiedRecords = List.copyOf(requireNonNull(records));
        requireUniqueSessions(copiedRecords);
        this.records = copiedRecords;
    }

    private static void requireUniqueSessions(List<ParticipationRecord> records) {
        Map<ClassCode, Set<LocalDate>> datesByClass = new HashMap<>();
        for (ParticipationRecord record : records) {
            Set<LocalDate> dates = datesByClass.computeIfAbsent(
                    record.getClassCode(), classCode -> new HashSet<>());
            if (!dates.add(record.getDate())) {
                throw new IllegalArgumentException(MESSAGE_DUPLICATE_SESSION);
            }
        }
    }

    /**
     * Returns an empty participation history.
     */
    public static ParticipationHistory empty() {
        return new ParticipationHistory(List.of());
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    /**
     * Returns the records as an unmodifiable list, without sorting them.
     */
    public List<ParticipationRecord> getRecords() {
        return records;
    }

    /**
     * Returns the record for the given class and date, if one exists.
     */
    public Optional<ParticipationRecord> findRecord(ClassCode classCode, LocalDate date) {
        requireNonNull(classCode);
        requireNonNull(date);
        return records.stream()
                .filter(record -> record.getClassCode().equals(classCode)
                        && record.getDate().equals(date))
                .findFirst();
    }

    /**
     * Returns a history containing the given record.
     * Replaces an existing record for the same session; otherwise appends a new record.
     * The new score replaces the old score rather than being added to it.
     */
    public ParticipationHistory withRecord(ParticipationRecord record) {
        requireNonNull(record);
        List<ParticipationRecord> updatedRecords = new ArrayList<>(records);
        for (int index = 0; index < updatedRecords.size(); index++) {
            if (updatedRecords.get(index).isSameSession(record)) {
                updatedRecords.set(index, record);
                return new ParticipationHistory(updatedRecords);
            }
        }
        updatedRecords.add(record);
        return new ParticipationHistory(updatedRecords);
    }

    /**
     * Returns a history without the record for the given class and date.
     *
     * @throws IllegalArgumentException if no record exists for that session
     */
    public ParticipationHistory withoutRecord(ClassCode classCode, LocalDate date) {
        ParticipationRecord record = findRecord(classCode, date)
                .orElseThrow(() -> new IllegalArgumentException(MESSAGE_RECORD_NOT_FOUND));
        List<ParticipationRecord> updatedRecords = new ArrayList<>(records);
        updatedRecords.remove(record);
        return new ParticipationHistory(updatedRecords);
    }

    /**
     * Returns the sum of recorded points for one class, or zero if none are recorded.
     * This is a raw total, not a percentage or a final grade.
     */
    public long getTotalPoints(ClassCode classCode) {
        requireNonNull(classCode);
        return records.stream()
                .filter(record -> record.getClassCode().equals(classCode))
                .mapToLong(record -> record.getPoints().value)
                .sum();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ParticipationHistory otherHistory)) {
            return false;
        }
        // Record order does not change the participation information stored.
        return new HashSet<>(records).equals(new HashSet<>(otherHistory.records));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(records).hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("records", records)
                .toString();
    }
}
