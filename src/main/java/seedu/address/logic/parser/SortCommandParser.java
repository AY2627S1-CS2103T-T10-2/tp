package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortDirection;
import seedu.address.logic.commands.SortCommand.SortField;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.gradebook.AssignmentId;

/**
 * Parses a field and direction for the sort command.
 */
public class SortCommandParser implements Parser<SortCommand> {

    @Override
    public SortCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new SortCommand();
        }

        String[] tokens = trimmedArgs.split("\\s+");
        String fieldName = tokens[0].toLowerCase(Locale.ROOT);
        if (fieldName.equals("mark")) {
            if (tokens.length < 2 || tokens.length > 3 || !tokens[1].startsWith("id/")) {
                throw createInvalidFormatException();
            }
            String idValue = tokens[1].substring("id/".length());
            if (!AssignmentId.isValidAssignmentId(idValue)) {
                throw new ParseException(AssignmentId.MESSAGE_CONSTRAINTS);
            }
            return new SortCommand(new AssignmentId(idValue), parseDirection(tokens, 2));
        }
        if (tokens.length > 2) {
            throw createInvalidFormatException();
        }

        SortField field = switch (fieldName) {
            case "name" -> SortField.NAME;
            case "matric" -> SortField.MATRIC;
            case "email" -> SortField.EMAIL;
            case "attendance" -> SortField.ATTENDANCE;
            default -> throw createInvalidFormatException();
        };
        return new SortCommand(field, parseDirection(tokens, 1));
    }

    private SortDirection parseDirection(String[] tokens, int index) throws ParseException {
        if (tokens.length > index) {
            return switch (tokens[index].toLowerCase(Locale.ROOT)) {
                case "asc" -> SortDirection.ASC;
                case "desc" -> SortDirection.DESC;
                default -> throw createInvalidFormatException();
            };
        }
        return SortDirection.ASC;
    }

    private ParseException createInvalidFormatException() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
