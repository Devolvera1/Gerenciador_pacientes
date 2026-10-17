package com.example.sistema_gestao_pacientes.Pacientes;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Prontuario.ProntuarioController;
import com.example.sistema_gestao_pacientes.Util.PacienteSession;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacientesController {

    private static final Logger LOGGER = Logger.getLogger(PacientesController.class.getName());

    @FXML private TableView<PacienteSession> table;
    @FXML private TableColumn<PacienteSession, String> colNome;
    @FXML private TableColumn<PacienteSession, String> colContato;
    @FXML private TableColumn<PacienteSession, String> colUtente;
    @FXML private TableColumn<PacienteSession, String> colUltimaConsulta;
    @FXML private TableColumn<PacienteSession, Void> colAcoes;
    @FXML private Button BtnAdicionarPaciente;
    @FXML private TextField Pesquisa;
    @FXML private ComboBox<String> ComboPacientes;
    @FXML private Button BtnExportar;
    @FXML private Button BtnAnterior;
    @FXML private Button BtnSeguinte;
    @FXML private Text QtdRegistros;
    @FXML private Text TextNumList;
    @FXML private Text TxtQtdList;
    @FXML private Text TxtNumPacinetes;

    private final ObservableList<PacienteSession> listaCompleta = FXCollections.observableArrayList();
    private final ObservableList<PacienteSession> listaFiltrada = FXCollections.observableArrayList();

    private int paginaAtual = 1;
    private final int itensPorPagina = 10;

    @FXML
    public void initialize() {
        BtnAdicionarPaciente.setOnAction(event -> novoPaciente());
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));
        colUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        colUltimaConsulta.setCellValueFactory(new PropertyValueFactory<>("consulta"));

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnProntuario = new Button("Prontuario");
            private final Button btnEditar = new Button("Editar");
            private final Button btnApagar = new Button("Apagar");

            private final HBox pane = new HBox(8, btnProntuario, btnEditar, btnApagar);

            {
                btnEditar.setStyle("-fx-background-color: #225F9E; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4;");
                btnApagar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4;");
                btnProntuario.setStyle("-fx-background-color: #88e788; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4;");

                btnProntuario.setOnAction(event -> {
                    PacienteSession paciente = getTableView().getItems().get(getIndex());
                    abrirProntuario(paciente);
                });

                btnEditar.setOnAction(event -> {
                    PacienteSession paciente = getTableView().getItems().get(getIndex());
                    editarPaciente(paciente);
                });

                btnApagar.setOnAction(event -> {
                    PacienteSession paciente = getTableView().getItems().get(getIndex());
                    apagarPaciente(paciente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        carregarDadosDoBanco();
        configurarComboBox();

        Pesquisa.textProperty().addListener((observable, oldValue, newValue) -> filtrarEAtualizar());

        BtnAnterior.setOnAction(event -> paginaAnterior());
        BtnSeguinte.setOnAction(event -> paginaSeguinte());
        BtnExportar.setOnAction(event -> exportarParaXml());

        filtrarEAtualizar();
        atualizarContadorTotalPacientes();
    }

    private void abrirProntuario(PacienteSession paciente) {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) table.getScene().getUserData();

            if (principalController != null) {
                principalController.abrirProntuario(paciente.getId());
            } else {
                System.err.println("Erro: PaginaPrincipalController não encontrado na cena.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível carregar a tela de pacientes.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void editarPaciente(PacienteSession paciente) {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) table.getScene().getUserData();

            if (principalController != null) {
                principalController.abrirTelaEditarPaciente(paciente.getId());
            } else {
                System.err.println("Erro: PaginaPrincipalController não encontrado na cena.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Não foi possível carregar a tela de edição.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void apagarPaciente(PacienteSession paciente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Apagar paciente: " + paciente.getNome());
        alert.setContentText("Tem certeza que deseja apagar este registro do sistema?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            DatabaseConnection db = new DatabaseConnection();
            boolean sucesso = db.apagarPacienteDoBanco(paciente.getId());

            if (sucesso) {
                listaCompleta.remove(paciente);
                filtrarEAtualizar();
                atualizarContadorTotalPacientes();

                Alert info = new Alert(Alert.AlertType.INFORMATION, "Paciente apagado com sucesso!", ButtonType.OK);
                info.showAndWait();
            } else {
                Alert erro = new Alert(Alert.AlertType.ERROR, "Erro ao apagar paciente do banco de dados.", ButtonType.OK);
                erro.showAndWait();
            }
        }
    }

    private void carregarDadosDoBanco() {
        try {
            DatabaseConnection db = new DatabaseConnection();
            listaCompleta.clear();
            listaCompleta.addAll(db.listarPacientesDoBanco());
            listaFiltrada.addAll(listaCompleta);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Erro ao carregar dados dos pacientes", e);
        }
    }

    private void configurarComboBox() {
        ComboPacientes.setItems(FXCollections.observableArrayList(
                "Todos os pacientes",
                "Com Consultas",
                "Sem Consultas"
        ));
        ComboPacientes.getSelectionModel().selectFirst();
        ComboPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filtrarEAtualizar());
    }

    private void filtrarEAtualizar() {
        String termoPesquisa = Pesquisa.getText() == null ? "" : Pesquisa.getText().toLowerCase().trim();
        String filtroCombo = ComboPacientes.getSelectionModel().getSelectedItem();

        listaFiltrada.clear();

        for (PacienteSession p : listaCompleta) {
            boolean correspondePesquisa = termoPesquisa.isEmpty() ||
                    p.getNome().toLowerCase().contains(termoPesquisa) ||
                    p.getContato().toLowerCase().contains(termoPesquisa) ||
                    p.getUtente().toLowerCase().contains(termoPesquisa);

            boolean correspondeCombo = true;
            if ("Com Consultas".equals(filtroCombo)) {
                correspondeCombo = !p.getConsulta().equals("Sem consultas");
            } else if ("Sem Consultas".equals(filtroCombo)) {
                correspondeCombo = p.getConsulta().equals("Sem consultas");
            }

            if (correspondePesquisa && correspondeCombo) {
                listaFiltrada.add(p);
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

        ObservableList<PacienteSession> paginaAtualLista = FXCollections.observableArrayList();
        if (totalRegistros > 0) {
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

    private void atualizarContadorTotalPacientes() {
        if (TxtNumPacinetes != null) {
            try {
                DatabaseConnection db = new DatabaseConnection();
                int total = db.contarTotalPacientes();
                TxtNumPacinetes.setText(total + " pacientes");
            } catch (Exception e) {
                TxtNumPacinetes.setText("0 pacientes");
            }
        }
    }

    private void exportarParaXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Pacientes para XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos XML (*.xml)", "*.xml"));

        File arquivo = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (arquivo != null) {
            try (PrintWriter writer = new PrintWriter(arquivo, "UTF-8")) {
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.println("<pacientes>");

                for (PacienteSession p : listaFiltrada) {
                    writer.println("    <paciente>");
                    writer.println("        <nome>" + escapeXml(p.getNome()) + "</nome>");
                    writer.println("        <contato>" + escapeXml(p.getContato()) + "</contato>");
                    writer.println("        <utente>" + escapeXml(p.getUtente()) + "</utente>");
                    writer.println("        <ultimaConsulta>" + escapeXml(p.getConsulta()) + "</ultimaConsulta>");
                    writer.println("    </paciente>");
                }

                writer.println("</pacientes>");

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
    private void novoPaciente() {
        try {
            if (BtnAdicionarPaciente != null && BtnAdicionarPaciente.getScene() != null) {
                Object userData = BtnAdicionarPaciente.getScene().getUserData();
                if (userData instanceof PaginaPrincipalController) {
                    ((PaginaPrincipalController) userData).abrirTelaAdicionarPaciente();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}