package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void setUpJavaFx() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void fillInnerParts_restoreFailed_disablesCommandBoxAndDisplaysRecoveryInstructions() throws Exception {
        String startupError = "Cannot restore records. Close TAssist, correct the file, and reopen TAssist.";
        assertStartupState(Optional.of(startupError));
    }

    @Test
    public void fillInnerParts_restoreSucceeded_enablesCommandBox() throws Exception {
        assertStartupState(Optional.empty());
    }

    private void assertStartupState(Optional<String> startupError) throws Exception {
        FutureTask<Void> checkWindow = new FutureTask<>(() -> {
            Path dataFilePath = temporaryFolder.resolve("addressbook.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataFilePath),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
            Logic logic = new LogicManager(new ModelManager(), storage, startupError);
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, dataFilePath);
                window.fillInnerParts();
                TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
                TextArea resultDisplay = (TextArea) stage.getScene().lookup("#resultDisplay");

                if (startupError.isPresent()) {
                    assertTrue(commandTextField.isDisabled());
                    assertEquals(startupError.get(), resultDisplay.getText());
                    assertTrue(resultDisplay.isWrapText());
                } else {
                    assertFalse(commandTextField.isDisabled());
                    assertTrue(resultDisplay.getText().isEmpty());
                }
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(checkWindow);
        checkWindow.get(10, TimeUnit.SECONDS);
    }
}
