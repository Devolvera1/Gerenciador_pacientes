package com.example.sistema_gestao_pacientes.NovaConsulta;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Util.MedicoSession;
import com.example.sistema_gestao_pacientes.Util.PacienteSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NovaConsultaController {

    @FXML private Button Btndash;
    @FXML private ComboBox<PacienteSession> fieldNome;
    @FXML private ComboBox<String> fieldEspecialidade;
    @FXML private ComboBox<MedicoSession> comboProfissional;
    @FXML private ComboBox<String> ComboTipoConsulta;
    @FXML private ComboBox<String> comboDuracao;
    @FXML private DatePicker DateDataConsulta;
    @FXML private ComboBox<String> comboHorario;
    @FXML private RadioButton radiopresencial;
    @FXML private Text txtinciais;
    @FXML private RadioButton radioteleconsulta;
    @FXML private ComboBox<String> combounidade;
    @FXML private ComboBox<String> comboconsutorio;
    @FXML private TextField textfieldmotivo;
    @FXML private TextField textfieldobs;
    @FXML private Text txtNome;
    @FXML private Text txtnumero;
    @FXML private Text txtDados;
    @FXML private Text TxtNomePaciente;
    @FXML private Text txtIdadeepronuario;
    @FXML private Text txtIdentificador2;
    @FXML private Text txtDr;
    @FXML private Text txtEspecialidade;
    @FXML private Text txtdatacompleta;
    @FXML private Text txtconfirmacao;
    @FXML private Text txtunidade;
    @FXML private CheckBox checkconfirmacao;
    @FXML private Button btnagendar;
    @FXML private Button btncancelar;


    private final ToggleGroup modalidadeGroup = new ToggleGroup();
    private final ObservableList<MedicoSession> listaMedicosCompleta = FXCollections.observableArrayList();
    private final ObservableList<PacienteSession> listaPacientesCompleta = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarRadioButtons();
        preencherOpcoesEstaticas();
        carregarPacientesDoBanco();
        carregarMedicosEEspecialidades();
        configurarListenersParaResumo();

        if (Btndash != null) Btndash.setOnAction(e -> voltarDashboard());
        if (btncancelar != null) btncancelar.setOnAction(e -> voltarDashboard());
        if (btnagendar != null) btnagendar.setOnAction(e -> agendarConsulta());
    }

    private void configurarRadioButtons() {
        if (radiopresencial != null && radioteleconsulta != null) {
            radiopresencial.setToggleGroup(modalidadeGroup);
            radioteleconsulta.setToggleGroup(modalidadeGroup);
            radiopresencial.setSelected(true);
        }
    }

    private void preencherOpcoesEstaticas() {
        if (ComboTipoConsulta != null) {
            ComboTipoConsulta.setItems(FXCollections.observableArrayList("Primeira Consulta", "Retorno", "Acompanhamento", "Exame"));
            ComboTipoConsulta.getSelectionModel().select("Primeira Consulta");
        }

        if (comboDuracao != null) {
            comboDuracao.setItems(FXCollections.observableArrayList("15 minutos", "30 minutos", "45 minutos", "60 minutos"));
            comboDuracao.getSelectionModel().select("30 minutos");
        }

        if (comboHorario != null) {
            comboHorario.setItems(FXCollections.observableArrayList(
                    "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                    "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
            ));
        }

        if (combounidade != null) {
            combounidade.setItems(FXCollections.observableArrayList("Unidade Paulista", "Unidade Jardins", "Unidade Pinheiros"));
            combounidade.getSelectionModel().selectFirst();
        }

        if (comboconsutorio != null) {
            comboconsutorio.setItems(FXCollections.observableArrayList("Consultório 01", "Consultório 02", "Consultório 03", "Consultório 04"));
            comboconsutorio.getSelectionModel().selectFirst();
        }
    }

    private void carregarPacientesDoBanco() {
        DatabaseConnection db = new DatabaseConnection();
        List<PacienteSession> pacientes = db.listarPacientesDoBanco();

        if (pacientes != null && !pacientes.isEmpty()) {
            listaPacientesCompleta.addAll(pacientes);
            if (fieldNome != null) {
                fieldNome.setItems(listaPacientesCompleta);
            }
        }
    }

    private void carregarMedicosEEspecialidades() {
        DatabaseConnection db = new DatabaseConnection();
        List<MedicoSession> medicos = db.listarMedicosDoBanco();

        if (medicos != null && !medicos.isEmpty()) {
            listaMedicosCompleta.addAll(medicos);

            if (comboProfissional != null) {
                comboProfissional.setItems(listaMedicosCompleta);
            }

            ObservableList<String> especialidades = FXCollections.observableArrayList();
            for (MedicoSession m : medicos) {
                if (m.getEspecialidade() != null && !especialidades.contains(m.getEspecialidade())) {
                    especialidades.add(m.getEspecialidade());
                }
            }
            if (fieldEspecialidade != null) {
                fieldEspecialidade.setItems(especialidades);
            }
        }

        if (fieldEspecialidade != null) {
            fieldEspecialidade.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && comboProfissional != null) {
                    ObservableList<MedicoSession> medicosFiltrados = FXCollections.observableArrayList();
                    for (MedicoSession m : listaMedicosCompleta) {
                        if (newVal.equalsIgnoreCase(m.getEspecialidade())) {
                            medicosFiltrados.add(m);
                        }
                    }
                    comboProfissional.setItems(medicosFiltrados);
                }
            });
        }
    }

    private void configurarListenersParaResumo() {
        if (fieldNome != null) {
            fieldNome.valueProperty().addListener((obs, oldVal, paciente) -> {
                if (paciente != null && paciente.getNome() != null && !paciente.getNome().isBlank()) {
                    if (TxtNomePaciente != null) TxtNomePaciente.setText(paciente.getNome());
                    if (txtNome != null) txtNome.setText(paciente.getNome());
                    if (txtnumero != null) txtnumero.setText(paciente.getContato() != null ? paciente.getContato() : "");
                    if (txtDados != null) txtDados.setText("Utente: " + (paciente.getUtente() != null ? paciente.getUtente() : "N/A"));
                    if (txtIdadeepronuario != null) txtIdadeepronuario.setText("Utente: " + (paciente.getUtente() != null ? paciente.getUtente() : "N/A"));

                    if (txtinciais != null) {
                        txtinciais.setText(extrairIniciais(paciente.getNome()));
                        txtinciais.setVisible(true);
                    }
                } else {
                    if (TxtNomePaciente != null) TxtNomePaciente.setText("Paciente não selecionado");
                    if (txtNome != null) txtNome.setText("Paciente não selecionado");
                    if (txtnumero != null) txtnumero.setText("");
                    if (txtDados != null) txtDados.setText("");
                    if (txtIdadeepronuario != null) txtIdadeepronuario.setText("");


                    if (txtinciais != null) {
                        txtinciais.setText("");
                        txtinciais.setVisible(false);
                    }
                }
            });
        }

        if (comboProfissional != null) {
            comboProfissional.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (txtDr != null) txtDr.setText("Dr(a). " + newVal.getNome());
                    if (txtEspecialidade != null) {
                        String tipo = (ComboTipoConsulta != null && ComboTipoConsulta.getValue() != null) ? ComboTipoConsulta.getValue() : "";
                        txtEspecialidade.setText(newVal.getEspecialidade() + " · " + tipo);
                    }
                }
            });
        }

        if (DateDataConsulta != null) {
            DateDataConsulta.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && txtdatacompleta != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy");
                    txtdatacompleta.setText(newVal.format(formatter));
                }
            });
        }

        if (comboHorario != null) {
            comboHorario.valueProperty().addListener((obs, oldVal, newVal) -> atualizarResumoAtendimento());
        }

        if (comboDuracao != null) {
            comboDuracao.valueProperty().addListener((obs, oldVal, newVal) -> atualizarResumoAtendimento());
        }

        if (combounidade != null) {
            combounidade.valueProperty().addListener((obs, oldVal, newVal) -> atualizarResumoLocal());
        }

        if (comboconsutorio != null) {
            comboconsutorio.valueProperty().addListener((obs, oldVal, newVal) -> atualizarResumoLocal());
        }
    }

    private void atualizarResumoAtendimento() {
        if (txtconfirmacao != null) {
            String duracao = (comboDuracao != null && comboDuracao.getValue() != null) ? comboDuracao.getValue() : "";
            String hora = (comboHorario != null && comboHorario.getValue() != null) ? comboHorario.getValue() : "--:--";
            String tipo = (ComboTipoConsulta != null && ComboTipoConsulta.getValue() != null) ? ComboTipoConsulta.getValue() : "";

            txtconfirmacao.setText(tipo + " às " + hora + " · " + duracao);
        }
    }

    private void atualizarResumoLocal() {
        if (txtunidade != null) {
            String unidade = (combounidade != null && combounidade.getValue() != null) ? combounidade.getValue() : "";
            String consultorio = (comboconsutorio != null && comboconsutorio.getValue() != null) ? comboconsutorio.getValue() : "";

            txtunidade.setText(unidade + " · " + consultorio);
        }
    }

    private void agendarConsulta() {
        if (!validarCampos()) return;

        PacienteSession pacienteSelecionado = fieldNome.getValue();
        MedicoSession medicoSelecionado = comboProfissional.getValue();
        LocalDate data = DateDataConsulta.getValue();
        String horario = comboHorario.getValue();
        String obs = (textfieldobs != null) ? textfieldobs.getText() : "";

        String sql = "INSERT INTO consultas (paciente_id, medico_id, data_consulta, hora_consulta, status, observacao_cancelamento) VALUES (?, ?, ?, ?, 'AGENDADA', ?)";
        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteSelecionado.getId());
            stmt.setInt(2, medicoSelecionado.getId());
            stmt.setString(3, data.toString());
            stmt.setString(4, horario + ":00");
            stmt.setString(5, obs);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Consulta agendada com sucesso!", ButtonType.OK);
                alert.setHeaderText("Sucesso");
                alert.showAndWait();
                voltarDashboard();
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                mostrarAlertaAviso("O médico selecionado já possui uma consulta agendada nesta mesma data e horário.");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao gravar agendamento no banco de dados: " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private boolean validarCampos() {
        if (fieldNome == null || fieldNome.getValue() == null) {
            mostrarAlertaAviso("Selecione um paciente cadastrado no sistema.");
            return false;
        }
        if (comboProfissional == null || comboProfissional.getValue() == null) {
            mostrarAlertaAviso("Selecione um profissional para o atendimento.");
            return false;
        }
        if (DateDataConsulta == null || DateDataConsulta.getValue() == null) {
            mostrarAlertaAviso("Selecione a data da consulta.");
            return false;
        }
        if (comboHorario == null || comboHorario.getValue() == null) {
            mostrarAlertaAviso("Selecione o horário da consulta.");
            return false;
        }
        if (textfieldmotivo == null || textfieldmotivo.getText() == null || textfieldmotivo.getText().isBlank()) {
            mostrarAlertaAviso("Informe o motivo da consulta.");
            return false;
        }
        return true;
    }

    private void mostrarAlertaAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensagem, ButtonType.OK);
        alert.setHeaderText("Atenção");
        alert.showAndWait();
    }

    private void voltarDashboard() {
        try {
            Button btnReferencia = btnagendar != null ? btnagendar : Btndash;
            if (btnReferencia != null) {
                PaginaPrincipalController principalController = (PaginaPrincipalController) btnReferencia.getScene().getUserData();
                if (principalController != null) {
                    principalController.abrirDashboard();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao retornar para a dashboard: " + e.getMessage());
        }
    }
    private String extrairIniciais(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            return "";
        }

        String[] partes = nomeCompleto.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        } else {
            String primeiraLetra = partes[0].substring(0, 1);
            String ultimaLetra = partes[partes.length - 1].substring(0, 1);
            return (primeiraLetra + ultimaLetra).toUpperCase();
        }
    }
}