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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicosController {

    private static final Logger LOGGER = Logger.getLogger(MedicosController.class.getName());

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
    @FXML private Button BtnAnterior;
    @FXML private Button BtnSeguinte;
    @FXML private Button BtnExportar;
    @FXML private Button BtnAdicionarMedico;
    @FXML private Button BtnEditarMedico;

    @FXML private Text QtdRegistros;
    @FXML private Text TextNumList;
    @FXML private Text TxtQtdList;
    @FXML private Text TxtNumMedicos;

    private final ObservableList<MedicoSession> listaCompleta = FXCollections.observableArrayList();
    private final ObservableList<MedicoSession> listaFiltrada = FXCollections.observableArrayList();

    private int paginaAtual = 1;
    private final int itensPorPagina = 10;

    @FXML
    public void initialize() {
        BtnAdicionarMedico.setOnAction(event -> novoMedico());
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));
        colCRM.setCellValueFactory(new PropertyValueFactory<>("crm"));
        colGabinete.setCellValueFactory(new PropertyValueFactory<>("gabinete"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        if (Pesquisa != null) {
            Pesquisa.textProperty().addListener((observable, oldValue, newValue) -> filtrarEAtualizar());
        }

        if (BtnAnterior != null) BtnAnterior.setOnAction(event -> paginaAnterior());
        if (BtnSeguinte != null) BtnSeguinte.setOnAction(event -> paginaSeguinte());
        if (BtnExportar != null) BtnExportar.setOnAction(event -> exportarParaXml());


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
            atualizarContadorTotalMedicos();
        }
    }

    private void atualizarContadorTotalMedicos() {
        if (TxtNumMedicos != null) {
            int total = listaCompleta.size();
            TxtNumMedicos.setText(total + " Médicos");
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
        atualizarTabelaPaginada();
    }

    private void atualizarTabelaPaginada() {
        int totalRegistros = listaFiltrada.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) totalRegistros / itensPorPagina));

        if (paginaAtual > totalPaginas) {
            paginaAtual = totalPaginas;
        }

        int indiceInicial = (paginaAtual - 1) * itensPorPagina;
        int indiceFinal = Math.min(indiceInicial + itensPorPagina, totalRegistros);

        ObservableList<MedicoSession> paginaAtualLista = FXCollections.observableArrayList();
        if (totalRegistros > 0 && indiceInicial < totalRegistros) {
            paginaAtualLista.addAll(listaFiltrada.subList(indiceInicial, indiceFinal));
        }

        table.setItems(paginaAtualLista);

        int exibidoInicio = totalRegistros == 0 ? 0 : indiceInicial + 1;

        if (QtdRegistros != null) {
            QtdRegistros.setText("A mostrar " + exibidoInicio + "–" + indiceFinal + " de " + totalRegistros + " registos");
        }
        if (TextNumList != null) {
            TextNumList.setText(String.valueOf(paginaAtual));
        }
        if (TxtQtdList != null) {
            TxtQtdList.setText("de " + totalPaginas);
        }
    }

    private void paginaAnterior() {
        if (paginaAtual > 1) {
            paginaAtual--;
            atualizarTabelaPaginada();
        }
    }

    private void paginaSeguinte() {
        int totalPaginas = Math.max(1, (int) Math.ceil((double) listaFiltrada.size() / itensPorPagina));
        if (paginaAtual < totalPaginas) {
            paginaAtual++;
            atualizarTabelaPaginada();
        }
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
                atualizarContadorTotalMedicos();

                Alert info = new Alert(Alert.AlertType.INFORMATION, "Médico apagado com sucesso!", ButtonType.OK);
                info.showAndWait();
            } else {
                Alert erro = new Alert(Alert.AlertType.ERROR, "Erro ao apagar médico do banco de dados.", ButtonType.OK);
                erro.showAndWait();
            }
        }
    }

    private void exportarParaXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Médicos para XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos XML (*.xml)", "*.xml"));

        File arquivo = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (arquivo != null) {
            try (PrintWriter writer = new PrintWriter(arquivo, "UTF-8")) {
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.println("<medicos>");

                for (MedicoSession m : listaFiltrada) {
                    writer.println("    <medico>");
                    writer.println("        <nome>" + escapeXml(m.getNome()) + "</nome>");
                    writer.println("        <especialidade>" + escapeXml(m.getEspecialidade()) + "</especialidade>");
                    writer.println("        <crm>" + escapeXml(m.getCrm()) + "</crm>");
                    writer.println("        <gabinete>" + escapeXml(m.getGabinete()) + "</gabinete>");
                    writer.println("        <estado>" + escapeXml(m.getEstado()) + "</estado>");
                    writer.println("    </medico>");
                }

                writer.println("</medicos>");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Dados exportados com sucesso para XML!");
                alert.showAndWait();

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao gerar arquivo XML", e);
            }
        }
    }

    private String escapeXml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    @FXML
    private void novoMedico() {
        try {
            if (BtnAdicionarMedico != null && BtnAdicionarMedico.getScene() != null) {
                Object userData = BtnAdicionarMedico.getScene().getUserData();
                if (userData instanceof PaginaPrincipalController) {
                    ((PaginaPrincipalController) userData).abrirTelaAdicionarMedico();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}