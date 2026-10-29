package seedu.address.testutil;

import java.util.List;

/**
 * Provides shared numeric inputs for gradebook validation tests.
 */
public final class GradebookNumberTestData {
    // These inputs parse as BigDecimal values but exceed the plain decimal length limit.
    public static final List<String> OVERSIZED_INPUTS = List.of(
            "100e2147483647", "100e2147483646", "10e2147483647", "1e2147483648",
            "1e-2147483647", "1e1000000", "1e64", "1e-63");

    // These exponents exceed the range supported by BigDecimal string parsing.
    public static final List<String> INVALID_EXPONENT_INPUTS = List.of("1e-2147483648");

    private GradebookNumberTestData() {}
}
