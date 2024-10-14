package org.example.ceasarciper;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.File;

public class CeasarCiperController {

    @FXML
    private RadioButton encryptWithKeyRadio;

    @FXML
    private RadioButton decryptWithKeyRadio;

    @FXML
    private RadioButton bruteForceRadio;

    @FXML
    private RadioButton statisticalAnalysisRadio;

    @FXML
    private TextField filePathField;

    @FXML
    private TextField keyField;

    @FXML
    private Label keyLabel;

    @FXML
    private Button executeButton;

    private final CeasarProcessor processor = new CeasarProcessor();

    @FXML
    public void initialize() {

        ToggleGroup group = new ToggleGroup();

        encryptWithKeyRadio.setToggleGroup(group);
        decryptWithKeyRadio.setToggleGroup(group);
        bruteForceRadio.setToggleGroup(group);
        statisticalAnalysisRadio.setToggleGroup(group);


        encryptWithKeyRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateKeyFieldVisibility();
            updateButtonState();
        });
        decryptWithKeyRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateKeyFieldVisibility();
            updateButtonState();
        });
        bruteForceRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateKeyFieldVisibility();
            updateButtonState();
        });
        statisticalAnalysisRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateKeyFieldVisibility();
            updateButtonState();
        });


        updateKeyFieldVisibility();
        updateButtonState();
    }

    @FXML
    public void executeAction() {
        String filePath = filePathField.getText();
        String keyText = keyField.getText();

        if (filePath.isEmpty()) {
            showErrorAlert("Ошибка", "Путь к файлу не указан.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            showErrorAlert("Ошибка", "Файл не найден.");
            return;
        }

        int key = 0;
        if (!keyText.isEmpty()) {
            try {
                key = Integer.parseInt(keyText);
            } catch (NumberFormatException e) {
                showErrorAlert("Ошибка", "Ключ должен быть числом.");
                return;
            }
        }

        if (encryptWithKeyRadio.isSelected()) {
            processor.encryptFile(filePath, filePath + ".encrypted", key);
            showSuccessAlert("Успех", "Файл успешно зашифрован.", filePath + ".encrypted");
        } else if (decryptWithKeyRadio.isSelected()) {
            processor.decryptFile(filePath, filePath + ".decrypted", key);
            showSuccessAlert("Успех", "Файл успешно расшифрован.", filePath + ".decrypted");
        } else if (bruteForceRadio.isSelected()) {
            processor.bruteForceDecrypt(filePath, filePath + ".brute_force_decrypted");
            showSuccessAlert("Успех", "Файл успешно расшифрован методом brute force.", filePath + ".brute_force_decrypted");
        } else if (statisticalAnalysisRadio.isSelected()) {
            processor.statisticalAnalysisDecrypt(filePath, filePath + ".statistical_analysis_decrypted");
            showSuccessAlert("Успех", "Файл успешно расшифрован методом статистического анализа.", filePath + ".statistical_analysis_decrypted");
        }
    }

    private void updateKeyFieldVisibility() {
        if (encryptWithKeyRadio.isSelected() || decryptWithKeyRadio.isSelected()) {
            keyField.setVisible(true);
            keyField.setManaged(true);
            keyLabel.setVisible(true);
            keyLabel.setManaged(true);
        } else {
            keyField.setVisible(false);
            keyField.setManaged(false);
            keyLabel.setVisible(false);
            keyLabel.setManaged(false);
        }
    }

    private void updateButtonState() {
        boolean isAnyRadioSelected = encryptWithKeyRadio.isSelected() || decryptWithKeyRadio.isSelected() || bruteForceRadio.isSelected() || statisticalAnalysisRadio.isSelected();
        executeButton.setDisable(!isAnyRadioSelected);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message, String filePath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message + "\nПуть к файлу: " + filePath);
        alert.showAndWait();
    }
}