package com.example.sistema_gestao_pacientes.Pacientes;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Util.PacienteSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PacientesController {

    @FXML private TableView<PacienteSession> table;
    @FXML private TableColumn<PacienteSession, String> colNome;
    @FXML private TableColumn<PacienteSession, String> colContato;
    @FXML private TableColumn<PacienteSession, String> colUtente;
    @FXML private TableColumn<PacienteSession, String> colUltimaConsulta;
    @FXML private TableColumn<PacienteSession, String> colAções;



    private DatabaseConnection dbConnection = new DatabaseConnection();
    private ObservableList<PacienteSession> listarPacientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));
        colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        colUltimaConsulta.setCellValueFactory(new PropertyValueFactory<>("consulta"));
        colAções.setCellValueFactory(new PropertyValueFactory<>("acoes"));

        carregarTabelaPacientes();
    }

    private void carregarTabelaPacientes() {
        listarPacientes.clear();
        listarPacientes.addAll(dbConnection.listarPacientesDoBanco());
        table.setItems(listarPacientes);
    }
}