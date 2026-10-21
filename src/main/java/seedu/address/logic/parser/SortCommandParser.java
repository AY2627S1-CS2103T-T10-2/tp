package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.SortDirection;
import seedu.address.logic.commands.SortCommand.SortField;
import seedu.address.logic.parser.exceptions.ParseException;

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
        if (tokens.length > 2) {
            throw invalidFormat();
        }

        SortField field = switch (tokens[0]) {
            case "name" -> SortField.NAME;
            case "matric" -> SortField.MATRIC;
            case "email" -> SortField.EMAIL;
            default -> throw invalidFormat();
        };
        SortDirection direction = SortDirection.ASC;
        if (tokens.length == 2) {
            direction = switch (tokens[1]) {
                case "asc" -> SortDirection.ASC;
                case "desc" -> SortDirection.DESC;
                default -> throw invalidFormat();
            };
        }
        return new SortCommand(field, direction);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
