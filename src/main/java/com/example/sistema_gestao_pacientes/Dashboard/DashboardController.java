package com.example.sistema_gestao_pacientes.Dashboard;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Util.ConsultaSession;
import com.example.sistema_gestao_pacientes.Util.ProximaConsulta;
import com.example.sistema_gestao_pacientes.Util.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class DashboardController {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    @FXML private TableView<ConsultaSession> table;
    @FXML private TableColumn<ConsultaSession, String> colHora;
    @FXML private TableColumn<ConsultaSession, String> colPaciente;
    @FXML private TableColumn<ConsultaSession, String> colMedico;
    @FXML private TableColumn<ConsultaSession, String> colTipo;
    @FXML private TableColumn<ConsultaSession, String> colEstado;

    @FXML private Text Total;
    @FXML private Text confcan;
    @FXML private Text totalpacientes;
    @FXML private Text newpaciente;
    @FXML private Text totalmedico;
    @FXML private Text ocups;
    @FXML private Text ocupa;

    @FXML private Text lblHora;
    @FXML private Text lblNomePaciente;
    @FXML private Text lblEspecialidade;
    @FXML private Text lblNomeDoutor;
    @FXML private Text lblSala;
    @FXML private Text lblAlergia;
    @FXML private Text lblInfosPaciente;
    @FXML private Text bemvindo;
    @FXML private Button btnProntuario;

    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    public void initialize() {
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("medico"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colHora.setCellFactory(column -> aplicarTextoNegrito());
        colPaciente.setCellFactory(column -> aplicarTextoNegrito());
        colTipo.setCellFactory(column -> aplicarTextoCinza());
        colEstado.setCellFactory(column -> aplicarBadgeStatus());

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                atualizarPainelASeguir(
                        newValue.getHora(),
                        newValue.getPaciente(),
                        newValue.getMedico(),
                        newValue.getEspecialidade(),
                        newValue.getSala(),
                        newValue.getAlergia(),
                        "Selecionado da agenda"
                );
            }
        });
        carregarTudo();
    }

    private void carregarTudo() {
        UserSession sessao = UserSession.getInstance();
        String perfil = sessao != null ? sessao.getPerfil() : "RECEPCAO";
        String nomeMedico = sessao != null ? sessao.getNomeMedico() : null;

        atualizarEstatisticasCards(perfil, nomeMedico);
        List<ConsultaSession> consultas = databaseConnection.listarConsultasDeHoje(perfil, nomeMedico);
        table.setItems(FXCollections.observableArrayList(consultas));

        ProximaConsulta proxima = databaseConnection.buscarProximaConsulta(perfil, nomeMedico);

        if (bemvindo != null) {
            String nomeExibicao = (nomeMedico != null && !nomeMedico.isEmpty()) ? nomeMedico : (sessao != null ? sessao.getUsername() : "Utilizador");
            bemvindo.setText("Bom dia, " + nomeExibicao + ". Este é o resumo da atividade da clínica.");
        }

        if (proxima != null) {
            atualizarPainelASeguir(
                    proxima.getHora(),
                    proxima.getPaciente(),
                    proxima.getMedico(),
                    proxima.getEspecialidade(),
                    proxima.getSala(),
                    proxima.getAlergia(),
                    "Próxima consulta do dia"
            );
        } else {
            lblHora.setText("--:--");
            lblNomePaciente.setText("Sem consultas agendadas");
            lblNomeDoutor.setText("---");
            lblEspecialidade.setText("---");
            lblSala.setText("--- · 60 minutos");
            lblAlergia.setText("---");
            if (lblInfosPaciente != null) {
                lblInfosPaciente.setText("---");
            }
            btnProntuario.setDisable(true);
        }
    }

    private void atualizarPainelASeguir(String hora, String paciente, String medico, String especialidade, String sala, String alergia, String infoExtra) {
        lblHora.setText(hora != null ? hora : "--:--");
        lblNomePaciente.setText(paciente != null ? paciente : "---");
        lblNomeDoutor.setText(medico != null ? medico : "---");
        lblEspecialidade.setText(especialidade != null ? especialidade : "---");
        lblSala.setText((sala != null ? sala : "---") + " · 60 minutos");
        lblAlergia.setText(alergia != null ? alergia : "Nenhuma registrada");

        if (lblInfosPaciente != null) {
            lblInfosPaciente.setText(infoExtra);
        }

        btnProntuario.setDisable(false);
    }

    private void atualizarEstatisticasCards(String perfil, String nomeMedico) {
        try {
            int[] contagens = databaseConnection.contarStatusConsultas(perfil, nomeMedico);
            if (Total != null) Total.setText(String.valueOf(contagens[0]));
            if (confcan != null) confcan.setText(contagens[1] + " confirmadas · " + contagens[2] + " cancelada");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao atualizar status de consultas nos cards", e);
        }

        try {
            int qtdPacientes = databaseConnection.contarTotalPacientes();
            if (totalpacientes != null) totalpacientes.setText(String.valueOf(qtdPacientes));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao atualizar total de pacientes", e);
        }

        try {
            int novosMes = databaseConnection.contarNovosPacientesMes();
            if (newpaciente != null) newpaciente.setText(novosMes + " novos pacientes este mês");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao atualizar novos pacientes do mês", e);
        }

        try {
            int qtdMedicos = databaseConnection.contarTotalMedicos();
            if (totalmedico != null) totalmedico.setText(String.valueOf(qtdMedicos));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao atualizar total de médicos", e);
        }

        try {
            if (ocups != null) ocups.setText("75%");
            if (ocupa != null) ocupa.setText("+5% face à semana anterior");
        } catch (Exception e) {
            // Apenas placeholders visuais
        }
    }

    private TableCell<ConsultaSession, String> aplicarTextoNegrito() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;"); }
            }
        };
    }

    private TableCell<ConsultaSession, String> aplicarTextoCinza() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-text-fill: #94A3B8;"); }
            }
        };
    }

    private TableCell<ConsultaSession, String> aplicarBadgeStatus() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-padding: 4 12 4 12; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");

                    if (item.equalsIgnoreCase("Confirmada")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #E6F4EA; -fx-text-fill: #1E8E3E;");
                    } else if (item.equalsIgnoreCase("Cancelada")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #FCE8E6; -fx-text-fill: #D93025;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #F1F3F4; -fx-text-fill: #5F6368;");
                    }
                    setGraphic(badge);
                }
            }
        };
    }
}