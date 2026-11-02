package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithNoTags_showsNoTags() {
        Person person = new PersonBuilder().build();
        assertTrue(Messages.format(person).contains("No tags"));
    }
}
