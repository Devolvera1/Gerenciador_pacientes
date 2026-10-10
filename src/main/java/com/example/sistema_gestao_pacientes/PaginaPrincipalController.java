package com.example.sistema_gestao_pacientes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PaginaPrincipalController {

    @FXML private Text lblData;



    @FXML private AnchorPane conteudoCentral;

    @FXML
    public void initialize() {
        carregarTela("Dashboard.fxml");
        lblData.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, d MMMM yyyy", new Locale("pt", "BR"))));
    }


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

    private void carregarTela(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFileName));
            Parent novaTela = loader.load();

            AnchorPane.setTopAnchor(novaTela, 0.0);
            AnchorPane.setBottomAnchor(novaTela, 0.0);
            AnchorPane.setLeftAnchor(novaTela, 0.0);
            AnchorPane.setRightAnchor(novaTela, 0.0);

            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(novaTela);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível carregar a tela: " + fxmlFileName);
        }
    }

    @FXML
    private void abrirDashboard() {
        carregarTela("Dashboard.fxml");
    }

    @FXML
    private void abrirPacientes() {
        carregarTela("Pacientes.fxml");
    }

    @FXML
    private void abrirMedicos() {
        carregarTela("Medicos.fxml");
    }
}
