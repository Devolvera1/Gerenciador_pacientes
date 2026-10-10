package com.example.sistema_gestao_pacientes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class PaginaPrincipalController {

    @FXML
    private void onMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #E2EDF8;");
    }

    @FXML
    private void onMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #FFFFFF;");


    }
}
