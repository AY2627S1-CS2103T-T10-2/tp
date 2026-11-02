package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    private static final String SPECIAL_CHARACTERS = "!#$%&'*+/=?^_`{|}~-";
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final int MAX_EMAIL_LENGTH = 254;
    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format local-part@domain "
            + "and adhere to the following constraints:\n"
            + "1. The local-part must contain 1 to 64 ASCII characters: letters, digits, periods, or these special "
            + "characters, excluding the parentheses, (" + SPECIAL_CHARACTERS + "). Periods must not be consecutive "
            + "or appear at the start or end. Other allowed special characters may appear anywhere.\n"
            + "2. The local-part is followed by an '@' and then a domain name. The domain name is made up of domain "
            + "labels separated by periods.\n"
            + "The domain name must:\n"
            + "    - have each domain label contain 1 to 63 ASCII characters\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters or hyphens.\n"
            + "3. The entire email must not exceed 254 characters. Quoted local-parts, domain literals, "
            + "and non-ASCII addresses are not supported.";
    // RFC 5322 dot-atom local-part: periods separate non-empty groups of allowed characters.
    private static final String LOCAL_ATOM_REGEX = "[A-Za-z0-9" + SPECIAL_CHARACTERS + "]+";
    private static final String LOCAL_PART_REGEX = LOCAL_ATOM_REGEX + "(?:\\." + LOCAL_ATOM_REGEX + ")*";
    private static final String DOMAIN_PART_REGEX = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";
    private static final String DOMAIN_LAST_PART_REGEX = "[A-Za-z0-9][A-Za-z0-9-]{0,61}[A-Za-z0-9]";
    private static final String DOMAIN_REGEX = "(?:" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns true for an ASCII dot-atom email with a hostname domain and SMTP length limits.
     * This supports unquoted local-parts only; it does not verify mailbox or domain existence.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);
        int atIndex = test.indexOf('@');
        return test.length() <= MAX_EMAIL_LENGTH
                && atIndex > 0 && atIndex <= MAX_LOCAL_PART_LENGTH
                && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
