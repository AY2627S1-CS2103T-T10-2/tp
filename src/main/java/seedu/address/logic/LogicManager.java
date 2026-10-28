package seedu.address.logic;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.SortDisplay;
import seedu.address.model.gradebook.ReadOnlyGradebook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT =
            "The command executed in memory, but the data could not be saved: %s";
    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "The command executed in memory, but the data could not be saved to %s "
                    + "due to insufficient permissions to write to the file or folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private final Optional<String> startupErrorMessage;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, Optional.empty());
    }

    /**
     * Constructs a {@code LogicManager} that blocks commands if a startup error is present.
     *
     * @param model The application model.
     * @param storage The application storage.
     * @param startupErrorMessage The restore failure and recovery instructions, or empty if restoration succeeded.
     */
    public LogicManager(Model model, Storage storage, Optional<String> startupErrorMessage) {
        this.model = model;
        this.storage = storage;
        this.startupErrorMessage = requireNonNull(startupErrorMessage);
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        if (startupErrorMessage.isPresent()) {
            throw new CommandException(startupErrorMessage.get());
        }

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        commandResult = command.execute(model);
        // Unchanged data, including restored undo/redo states, does not add a history entry.
        model.commitAddressBook();

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public Optional<String> getStartupErrorMessage() {
        return startupErrorMessage;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ReadOnlyGradebook getGradebook() {
        return model.getAddressBook().getGradebook();
    }

    @Override
    public ObservableValue<SortDisplay> sortDisplayProperty() {
        return model.sortDisplayProperty();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public ObservableValue<Number> addressBookRestoreCountProperty() {
        return model.addressBookRestoreCountProperty();
    }
}
