package seedu.address.model.gradebook;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

/**
 * Checks whether a gradebook number can be displayed and saved as a reasonably short plain decimal.
 */
final class GradebookNumber {
    static final int MAX_PLAIN_LENGTH = 64;

    private GradebookNumber() {}

    /**
     * Returns whether the normalized number's plain decimal representation fits the length limit.
     * Calculates the length without creating the potentially enormous decimal string.
     */
    static boolean fitsPlainLength(BigDecimal value) {
        requireNonNull(value);
        if (value.signum() == 0) {
            return true;
        }
        // Precision minus scale is unchanged by stripping zeros. Reject oversized integers before
        // normalization can overflow the scale; any remaining normalized scale is at least -63.
        if ((long) value.precision() - value.scale() > MAX_PLAIN_LENGTH) {
            return false;
        }
        BigDecimal normalized = value.stripTrailingZeros();
        long scale = normalized.scale();
        long integerDigits = Math.max(1L, (long) normalized.precision() - scale);
        long fractionalDigits = Math.max(0L, scale);
        long decimalPoint = fractionalDigits == 0 ? 0 : 1;
        return integerDigits + decimalPoint + fractionalDigits <= MAX_PLAIN_LENGTH;
    }
}
