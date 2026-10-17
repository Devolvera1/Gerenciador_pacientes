package com.example.sistema_gestao_pacientes.Prontuario;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Util.ConsultaSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProntuarioController {

    private int pacienteId;

    @FXML
    private Button BtnPacientes;

    @FXML
    private Text txtAlergia;

    @FXML
    private TableColumn<ConsultaSession, String> colEstado;

    @FXML
    public void initialize() {
        BtnPacientes.setOnAction(event -> abrirDashboard());
    }

    public void setPacienteId(int id) {
        this.pacienteId = id;
        carregarDadosDoPaciente();
    }

    private void carregarDadosDoPaciente() {
        String sql = "SELECT * FROM pacientes WHERE id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados do paciente: " + e.getMessage());
        }
    }
    private void atualizarPainelPaciente(String alergia) {
        txtAlergia.setText(alergia != null && !alergia.trim().isEmpty() ? alergia : "Nenhuma registrada");
    }

    @FXML
    private void abrirDashboard() {
        try {
            if (BtnPacientes != null && BtnPacientes.getScene() != null) {
                PaginaPrincipalController principalController = (PaginaPrincipalController) BtnPacientes.getScene().getUserData();

                if (principalController != null) {
                    principalController.abrirDashboard();
                } else {
                    System.err.println("Erro: PaginaPrincipalController não foi encontrado no UserData da cena.");
                }
            } else {
                System.err.println("Erro: O botão 'BtnPacientes' ou a Cena é nula.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível carregar a tela de pacientes.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}