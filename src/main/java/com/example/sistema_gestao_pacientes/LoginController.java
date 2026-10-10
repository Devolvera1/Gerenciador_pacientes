package com.example.sistema_gestao_pacientes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    private Button okButton;

    @FXML
    public void initialize() {
        loadSavedUser();

        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleOk();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleOk();
            }
        });
    }

    private void loadSavedUser() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        String savedUser = prefs.get("saved_username", "");

        if (!savedUser.isEmpty()) {
            usernameField.setText(savedUser);
            if (rememberMeCheckbox != null) {
                rememberMeCheckbox.setSelected(true);
            }
        }
    }

    @FXML
    private void handleOk() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        DatabaseConnection dbConnection = new DatabaseConnection();

        if (dbConnection.authenticateUser(username, password)) {

            Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
            if (rememberMeCheckbox != null && rememberMeCheckbox.isSelected()) {
                prefs.put("saved_username", username);
            } else {
                prefs.remove("saved_username");
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaginaPrincipal.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Página Principal");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);

                Stage loginStage = (Stage) usernameField.getScene().getWindow();
                loginStage.close();

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Não foi possível abrir a página principal.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Login");
            alert.setHeaderText("Usuário ou senha incorretos.");
            alert.showAndWait();
        }
    }
}