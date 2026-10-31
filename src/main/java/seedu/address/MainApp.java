package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seedu.address.commons.core.AppInstanceLock;
import seedu.address.commons.core.HomeFolder;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V0.5.1";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final String ALREADY_RUNNING_ERROR_FORMAT =
            "TAssist is already running from this folder:\n%s\n\n"
            + "Use the existing TAssist window, or close it before reopening TAssist.";
    private static final String INSTANCE_LOCK_ERROR_FORMAT =
            "TAssist could not lock this folder for exclusive access:\n%s\n\n"
            + "Reason: %s\n\n"
            + "Make sure you have permission to create and update files in this folder, then reopen TAssist.";
    private static final String DATA_LOADING_ERROR_FORMAT =
            "TAssist could not restore the saved student and attendance records from:\n%s\n\n"
            + "Reason: %s\n\n"
            + "Commands are disabled. The original data file has not been changed.\n"
            + "Close TAssist using the window's close button. Correct the file, replace it with a valid backup, "
            + "or delete it to start with sample student records. Then reopen TAssist.";

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private final Path homeFolder;
    private AppInstanceLock instanceLock;
    private String launchBlockedMessage;

    /**
     * Creates an application that stores its files in the home folder.
     */
    public MainApp() {
        this(HomeFolder.getHomeFolder());
    }

    MainApp(Path homeFolder) {
        this.homeFolder = homeFolder;
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        try {
            instanceLock = AppInstanceLock.tryAcquire(homeFolder).orElse(null);
        } catch (IOException e) {
            launchBlockedMessage = String.format(INSTANCE_LOCK_ERROR_FORMAT, homeFolder, e.getMessage());
            logger.severe(launchBlockedMessage);
            return;
        }
        if (instanceLock == null) {
            launchBlockedMessage = String.format(ALREADY_RUNNING_ERROR_FORMAT, homeFolder);
            logger.warning(launchBlockedMessage);
            return;
        }

        try {
            JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(homeFolder.resolve("preferences.json"));
            UserPrefs userPrefs = initPrefs(userPrefsStorage);
            JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(homeFolder.resolve("data")
                    .resolve("addressbook.json"));
            storage = new StorageManager(addressBookStorage, userPrefsStorage);

            initModelAndLogic(storage, userPrefs);

            ui = new UiManager(logic, storage.getAddressBookFilePath());
        } catch (Exception | Error e) {
            releaseInstanceLock();
            throw e;
        }
    }

    /**
     * Initializes the model and logic using saved records, or sample records if the data file is missing.
     * If restoration fails, initializes an empty model and blocks commands for the rest of this session.
     */
    protected void initModelAndLogic(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        Optional<String> startupErrorMessage = Optional.empty();
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            String errorMessage = String.format(DATA_LOADING_ERROR_FORMAT,
                    storage.getAddressBookFilePath().toAbsolutePath(), e.getCause().getMessage());
            logger.warning(errorMessage);
            startupErrorMessage = Optional.of(errorMessage);
            initialData = new AddressBook();
        }

        model = new ModelManager(initialData, userPrefs);
        logic = new LogicManager(model, storage, startupErrorMessage);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        if (launchBlockedMessage != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("TAssist - cannot start");
            alert.setHeaderText("Unable to start TAssist");
            alert.setContentText(launchBlockedMessage);
            alert.showAndWait();
            Platform.exit();
            return;
        }
        logger.info("Starting AddressBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        try {
            if (storage != null && model != null) {
                storage.saveUserPrefs(model.getUserPrefs());
            }
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        } finally {
            releaseInstanceLock();
        }
    }

    private void releaseInstanceLock() {
        if (instanceLock == null) {
            return;
        }
        try {
            instanceLock.close();
        } catch (IOException e) {
            logger.severe("Failed to release application lock " + StringUtil.getDetails(e));
        }
    }
}
