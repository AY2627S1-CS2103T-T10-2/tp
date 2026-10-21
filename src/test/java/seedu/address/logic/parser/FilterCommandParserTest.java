package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_FILTER_KEYWORD;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS2103T_T10;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLASS_CODE_CS3230_T01;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.classcode.ClassCode;
import seedu.address.model.classcode.ModuleCode;
import seedu.address.model.person.PersonInModuleOrClassPredicate;

public class FilterCommandParserTest {

    private FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "\n\t", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_moduleOnly_returnsFilterCommand() throws ParseException {
        FilterCommand expected = new FilterCommand(new PersonInModuleOrClassPredicate(Set.of(),
                Set.of(new ModuleCode("CS2103T"), new ModuleCode("CS3230"))));
        assertParseSuccess(parser, "CS2103T CS3230", expected);
    }

    @Test
    public void parse_mixedKeywords_normalizesCaseWhitespaceAndDuplicates() throws ParseException {
        FilterCommand expected = new FilterCommand(new PersonInModuleOrClassPredicate(
                Set.of(new ClassCode("CS2103T-T10")), Set.of(new ModuleCode("CS3230"))));
        assertParseSuccess(parser, " \tcs2103t-t10\nCs3230 CS2103T-T10 cs3230  ", expected);
    }

    @Test
    public void parse_validModuleBoundaries_returnsFilterCommand() throws ParseException {
        // Prefix: 2 and 3 letters; digits: exactly 4; suffix: 0, 1 and 2 letters.
        for (String keyword : new String[]{"CS0000", "ABC9999", "CS2103T", "ABC1234XY"}) {
            FilterCommand expected = new FilterCommand(new PersonInModuleOrClassPredicate(Set.of(),
                    Set.of(new ModuleCode(keyword))));
            assertParseSuccess(parser, keyword, expected);
        }
    }

    @Test
    public void parse_validClassGroupBoundaries_returnsFilterCommand() throws ParseException {
        // Group length: minimum 1 and maximum 5; letters and digits are both allowed.
        for (String keyword : new String[]{"CS2103T-1", "CS2103T-A", "CS2103T-A1234"}) {
            FilterCommand expected = new FilterCommand(new PersonInModuleOrClassPredicate(
                    Set.of(new ClassCode(keyword)), Set.of()));
            assertParseSuccess(parser, keyword, expected);
        }
    }

    @Test
    public void parse_invalidModuleBoundaries_throwsParseException() {
        // One step outside each prefix, digit-count and suffix boundary.
        for (String keyword : new String[]{"C2103", "ABCD2103", "CS210", "CS21030", "CS2103ABC"}) {
            assertParseFailure(parser, keyword, MESSAGE_INVALID_FILTER_KEYWORD);
        }
    }

    @Test
    public void parse_invalidClassCodes_throwsParseException() {
        // Group lengths 0 and 6; missing module, standalone group, symbols and extra separators.
        for (String keyword : new String[]{"CS2103T-", "CS2103T-A12345", "-T10", "T10",
            "CS2103T-T!", "CS2103T--T10", "CS2103T-T10-X", "C2103-T10"}) {
            assertParseFailure(parser, keyword, MESSAGE_INVALID_FILTER_KEYWORD);
        }
    }

    @Test
    public void parse_invalidKeywordAmongValidKeywords_rejectsWholeCommand() {
        assertParseFailure(parser, "T10 CS3230", MESSAGE_INVALID_FILTER_KEYWORD);
        assertParseFailure(parser, "CS3230 T10 CS2103T-T10", MESSAGE_INVALID_FILTER_KEYWORD);
        assertParseFailure(parser, "CS2103T-T10 T10", MESSAGE_INVALID_FILTER_KEYWORD);
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() throws ParseException {
        // no leading and trailing whitespaces
        FilterCommand expectedFilterCommand =
                new FilterCommand(new PersonInModuleOrClassPredicate(Set.of(new ClassCode(VALID_CLASS_CODE_CS2103T_T10),
                        new ClassCode(VALID_CLASS_CODE_CS3230_T01)), Set.of()));
        assertParseSuccess(parser, VALID_CLASS_CODE_CS2103T_T10 + " " + VALID_CLASS_CODE_CS3230_T01,
                expectedFilterCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n " + VALID_CLASS_CODE_CS2103T_T10 + "\n \t " + VALID_CLASS_CODE_CS3230_T01
                + "  \t", expectedFilterCommand);
    }

}
