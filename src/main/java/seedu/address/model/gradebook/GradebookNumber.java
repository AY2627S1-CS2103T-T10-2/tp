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
        BigDecimal normalized = requireNonNull(value).stripTrailingZeros();
        if (normalized.signum() == 0) {
            return true;
        }
        long scale = normalized.scale();
        long integerDigits = Math.max(1L, (long) normalized.precision() - scale);
        long fractionalDigits = Math.max(0L, scale);
        long decimalPoint = fractionalDigits == 0 ? 0 : 1;
        return integerDigits + decimalPoint + fractionalDigits <= MAX_PLAIN_LENGTH;
    }
}
