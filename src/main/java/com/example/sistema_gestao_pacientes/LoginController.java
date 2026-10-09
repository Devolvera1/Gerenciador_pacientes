package com.example.sistema_gestao_pacientes;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private void handleOk() {
        String usuario = usernameField.getText();
        String senha = passwordField.getText();
        System.out.println("Tentativa de login realizada para o usuário: " + usuario);
    }

    @FXML
    private void handleCancel() {
        System.exit(0);
    }
}
