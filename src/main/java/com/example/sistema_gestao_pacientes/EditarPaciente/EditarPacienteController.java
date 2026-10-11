package com.example.sistema_gestao_pacientes.EditarPaciente;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarPacienteController {

    private int pacienteId;

    @FXML private TextField txtNome;
    @FXML private TextField txtContato;
    @FXML private TextField txtUtente;

    public void setPacienteId(int id) {
        this.pacienteId = id;
        carregarDadosEdicao();
    }

    private void carregarDadosEdicao() {
    }

    @FXML
    private void salvarAlteracoes() {
        fecharJanela();
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}