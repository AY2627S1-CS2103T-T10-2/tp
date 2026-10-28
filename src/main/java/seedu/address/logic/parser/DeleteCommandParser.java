package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICULATION_NUMBER;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.StudentIdentifier;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given arguments and returns a DeleteCommand for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_MATRICULATION_NUMBER);

        String[] indexArguments = args.trim().split("\\s+");

        // Preserve shared parsing for single-target and matriculation-number inputs.
        if (argMultimap.getValue(PREFIX_MATRICULATION_NUMBER).isPresent()
                || indexArguments.length == 1) {
            StudentIdentifier targetStudent =
                    ParserUtil.parseStudentIdentifier(argMultimap, DeleteCommand.MESSAGE_USAGE);

            return new DeleteCommand(targetStudent);
        }

        List<StudentIdentifier> targetStudents = new ArrayList<>();
        for (String indexArgument : indexArguments) {
            targetStudents.add(
                    new StudentIdentifier(ParserUtil.parseIndex(indexArgument)));
        }

        return new DeleteCommand(targetStudents);
    }
}
