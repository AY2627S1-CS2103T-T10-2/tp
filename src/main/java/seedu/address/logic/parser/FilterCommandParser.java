package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_FILTER_KEYWORD;

import java.util.HashSet;
import java.util.Set;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;
import seedu.address.model.person.PersonInModuleOrClassPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        String[] keywords = trimmedArgs.split("\\s+");
        Set<ClassCode> classCodes = new HashSet<>();
        Set<ModuleCode> moduleCodes = new HashSet<>();

        for (String keyword : keywords) {
            if (ModuleCode.isValidModuleCode(keyword)) {
                moduleCodes.add(new ModuleCode(keyword));
            } else if (ClassCode.isValidClassCode(keyword)) {
                classCodes.add(new ClassCode(keyword));
            } else {
                throw new ParseException(MESSAGE_INVALID_FILTER_KEYWORD);
            }
        }

        return new FilterCommand(new PersonInModuleOrClassPredicate(classCodes, moduleCodes));
    }
}
