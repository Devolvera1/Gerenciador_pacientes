package com.example.sistema_gestao_pacientes.Medicos;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Util.MedicoSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class MedicosController {

    @FXML
    private TableView<MedicoSession> table;
    @FXML
    private TableColumn<MedicoSession, String> colNome;
    @FXML
    private TableColumn<MedicoSession, String> colEspecialidade;
    @FXML
    private TableColumn<MedicoSession, String> colCRM;
    @FXML
    private TableColumn<MedicoSession, String> colGabinete;
    @FXML
    private TableColumn<MedicoSession, String> colEstado;
    @FXML
    private TableColumn<MedicoSession, Void> colAcoes;

    @FXML private TextField Pesquisa;
    @FXML private ComboBox<String> ComboMedicos;

    private final ObservableList<MedicoSession> listaCompleta = FXCollections.observableArrayList();
    private final ObservableList<MedicoSession> listaFiltrada = FXCollections.observableArrayList();

    private int paginaAtual = 1;

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
        colCRM.setCellValueFactory(new PropertyValueFactory<>("crm"));
        colGabinete.setCellValueFactory(new PropertyValueFactory<>("gabinete"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        table.setItems(listaFiltrada);

        if (Pesquisa != null) {
            Pesquisa.textProperty().addListener((observable, oldValue, newValue) -> filtrarEAtualizar());
        }

        if (ComboMedicos != null) {
            ComboMedicos.valueProperty().addListener((observable, oldValue, newValue) -> filtrarEAtualizar());
        }

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnApagar = new Button("Apagar");
            private final HBox pane = new HBox(8, btnEditar, btnApagar);

            {
                btnEditar.setStyle("-fx-background-color: #225F9E; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4;");
                btnApagar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4;");

                btnEditar.setOnAction(event -> {
                    MedicoSession medico = getTableView().getItems().get(getIndex());
                    editarMedico(medico);
                });

                btnApagar.setOnAction(event -> {
                    MedicoSession medico = getTableView().getItems().get(getIndex());
                    apagarMedico(medico);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        carregarDadosDoBanco();
    }

    private void carregarDadosDoBanco() {
        DatabaseConnection db = new DatabaseConnection();
        List<MedicoSession> medicos = db.listarMedicosDoBanco();

        if (medicos != null) {
            listaCompleta.clear();
            listaCompleta.addAll(medicos);

            popularComboEspecialidades();
            filtrarEAtualizar();
        }
    }

    private void popularComboEspecialidades() {
        if (ComboMedicos == null) return;

        ObservableList<String> especialidades = FXCollections.observableArrayList();
        especialidades.add("Todas");

        for (MedicoSession m : listaCompleta) {
            if (m.getEspecialidade() != null && !m.getEspecialidade().isBlank()) {
                if (!especialidades.contains(m.getEspecialidade())) {
                    especialidades.add(m.getEspecialidade());
                }
            }
        }

        ComboMedicos.setItems(especialidades);
        ComboMedicos.getSelectionModel().selectFirst();
    }

    private void filtrarEAtualizar() {
        String termoPesquisa = (Pesquisa != null && Pesquisa.getText() != null) ? Pesquisa.getText().toLowerCase().trim() : "";
        String especialidadeSelecionada = (ComboMedicos != null) ? ComboMedicos.getSelectionModel().getSelectedItem() : null;

        listaFiltrada.clear();

        for (MedicoSession m : listaCompleta) {
            boolean correspondePesquisa = termoPesquisa.isEmpty() ||
                    (m.getNome() != null && m.getNome().toLowerCase().contains(termoPesquisa)) ||
                    (m.getCrm() != null && m.getCrm().toLowerCase().contains(termoPesquisa));

            boolean correspondeEspecialidade = true;
            if (especialidadeSelecionada != null && !"Todas".equals(especialidadeSelecionada)) {
                correspondeEspecialidade = especialidadeSelecionada.equalsIgnoreCase(m.getEspecialidade());
            }

            if (correspondePesquisa && correspondeEspecialidade) {
                listaFiltrada.add(m);
            }
        }

        paginaAtual = 1;
    }

    private void editarMedico(MedicoSession medico) {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) table.getScene().getUserData();

            if (principalController != null) {
                principalController.abrirTelaEditarMedico(medico.getId());
            } else {
                System.err.println("Erro: PaginaPrincipalController não encontrado na cena.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível carregar a tela de edição.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void apagarMedico(MedicoSession medico) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Apagar médico: " + medico.getNome());
        alert.setContentText("Tem certeza que deseja apagar este registro do sistema?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            DatabaseConnection db = new DatabaseConnection();
            boolean sucesso = db.apagarMedicoeDoBanco(medico.getId());

            if (sucesso) {
                listaCompleta.remove(medico);
                filtrarEAtualizar();

                Alert info = new Alert(Alert.AlertType.INFORMATION, "Médico apagado com sucesso!", ButtonType.OK);
                info.showAndWait();
            } else {
                Alert erro = new Alert(Alert.AlertType.ERROR, "Erro ao apagar médico do banco de dados.", ButtonType.OK);
                erro.showAndWait();
            }
        }
    }

    private void Totalmedicos() {
    }
}