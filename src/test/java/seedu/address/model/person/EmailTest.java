package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail(".peterjack@example.com")); // local part starts with a period
        assertFalse(Email.isValidEmail("peterjack.@example.com")); // local part ends with a period
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example..com")); // empty domain label
        assertFalse(Email.isValidEmail("john\n@example.com")); // control character
        assertFalse(Email.isValidEmail("john@example.com\n")); // trailing newline
        assertFalse(Email.isValidEmail("j\u00f6hn@example.com")); // non-ASCII local part
        assertFalse(Email.isValidEmail("john@ex\u00e4mple.com")); // non-ASCII domain
        assertFalse(Email.isValidEmail("\"john doe\"@example.com")); // quoted local part is unsupported
        assertFalse(Email.isValidEmail("john@[127.0.0.1]")); // domain literal is unsupported

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("john__doe@example.com")); // consecutive underscores in local part
        assertTrue(Email.isValidEmail("john___doe__123@example.com")); // multiple runs of underscores
        assertTrue(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertTrue(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertTrue(Email.isValidEmail("__peterjack@example.com")); // local part starts with underscores
        assertTrue(Email.isValidEmail("peterjack__@example.com")); // local part ends with underscores
        assertTrue(Email.isValidEmail("john++doe@example.com")); // repeated special characters
        assertTrue(Email.isValidEmail("john_.doe@example.com")); // special character next to a period
        assertTrue(Email.isValidEmail("o'connor@example.com")); // apostrophe
        assertTrue(Email.isValidEmail("!#$%&'*+/=?^_`{|}~-@example.com")); // all allowed special characters
        assertTrue(Email.isValidEmail("peterjack@exam--ple.com")); // consecutive domain hyphens
        assertTrue(Email.isValidEmail("peterjack@example.c")); // single-character final label
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@b")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_lengthBoundaries() {
        assertTrue(Email.isValidEmail("a".repeat(64) + "@example.com"));
        assertFalse(Email.isValidEmail("a".repeat(65) + "@example.com"));

        assertTrue(Email.isValidEmail("a@" + "b".repeat(63) + ".com"));
        assertFalse(Email.isValidEmail("a@" + "b".repeat(64) + ".com"));
        assertFalse(Email.isValidEmail("a@example." + "b".repeat(64)));

        String domainPrefix = "b".repeat(63) + "." + "c".repeat(63) + ".";
        String localPart = "a".repeat(64);
        assertTrue(Email.isValidEmail(localPart + "@" + domainPrefix + "d".repeat(61))); // 254 characters
        assertFalse(Email.isValidEmail(localPart + "@" + domainPrefix + "d".repeat(62))); // 255 characters
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
