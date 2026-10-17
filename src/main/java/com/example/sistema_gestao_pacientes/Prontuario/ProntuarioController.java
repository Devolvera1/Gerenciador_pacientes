package com.example.sistema_gestao_pacientes.Prontuario;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProntuarioController {

    private int pacienteId;
    private int consultaAtualId = -1;
    private int medicoLogadoId = -1;

    private int prontuarioSelecionadoId = -1;
    private int medicoDoProntuarioSelecionado = -1;
    private boolean modoEdicao = false;

    @FXML private Button BtnPacientes;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;
    @FXML private Button btnEditar;

    @FXML private VBox vboxHistorico;
    @FXML private Text txtTituloPainel;
    @FXML private Text txtRegistros;

    @FXML private Text txtNome;
    @FXML private Text dtxDataNacimento;
    @FXML private Text txtUtente;
    @FXML private Text txtTelefone;
    @FXML private Text txtAlergia;
    @FXML private Text TxtMedicamentos;

    @FXML private Text txtDataHoraGabinete;
    @FXML private Text txtMedicoeCrm;
    @FXML private Text txtTipoconsulta;

    @FXML private TextArea txtareaTensaoArterial;
    @FXML private TextArea txtareaFrenquencia;
    @FXML private TextArea txtareaTemp;
    @FXML private TextArea txtareaNotas;

    @FXML
    public void initialize() {
        BtnPacientes.setOnAction(event -> abrirDashboard());
        btnCancelar.setOnAction(event -> resetarParaConsultaAtual());
        btnSalvar.setOnAction(event -> salvarProntuario());

        if (btnEditar != null) {
            btnEditar.setOnAction(event -> habilitarEdicao());
        }

        carregarMedicoLogado();
    }

    public void setPacienteId(int id) {
        this.pacienteId = id;
        carregarDadosDoPaciente();
        carregarConsultaAtual();
        carregarHistoricoConsultas();
    }

    private void carregarMedicoLogado() {
        if (UserSession.getInstance() != null && UserSession.getInstance().getNomeMedico() != null) {
            String sql = "SELECT id FROM medicos WHERE nome = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, UserSession.getInstance().getNomeMedico());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    this.medicoLogadoId = rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarDadosDoPaciente() {
        String sql = "SELECT * FROM pacientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtNome.setText(rs.getString("nome"));
                LocalDate dataNasc = rs.getDate("data_nascimento").toLocalDate();
                dtxDataNacimento.setText(dataNasc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                txtUtente.setText(rs.getString("numero_utente") != null ? rs.getString("numero_utente") : "N/A");
                txtTelefone.setText(rs.getString("telefone"));
                String alergias = rs.getString("alergias");
                txtAlergia.setText(alergias != null && !alergias.trim().isEmpty() ? "Alergia: " + alergias : "Sem alergias");
                String meds = rs.getString("medicacao_habitual");
                TxtMedicamentos.setText(meds != null && !meds.trim().isEmpty() ? meds : "Sem medicação habitual");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetarParaConsultaAtual() {
        modoEdicao = false;
        prontuarioSelecionadoId = -1;
        medicoDoProntuarioSelecionado = -1;

        limparCampos();
        carregarConsultaAtual();
    }

    private void carregarConsultaAtual() {
        String sql = "SELECT c.id, c.data_consulta, c.hora_consulta, c.medico_id, m.nome AS medico_nome, m.crm, m.sala " +
                "FROM consultas c INNER JOIN medicos m ON c.medico_id = m.id " +
                "WHERE c.paciente_id = ? AND c.status IN ('AGENDADA', 'CONFIRMADA', 'EM_ANDAMENTO') " +
                "ORDER BY c.data_consulta ASC, c.hora_consulta ASC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                consultaAtualId = rs.getInt("id");
                int medicoResponsavelDaConsultaId = rs.getInt("medico_id");

                LocalDate data = rs.getDate("data_consulta").toLocalDate();
                String hora = rs.getTime("hora_consulta").toString().substring(0, 5);
                txtDataHoraGabinete.setText(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " · " + hora + " · " + rs.getString("sala"));
                txtMedicoeCrm.setText(rs.getString("medico_nome") + " · " + rs.getString("crm"));
                txtTipoconsulta.setText("Nova Consulta");

                String perfilLogado = UserSession.getInstance().getPerfil();
                boolean isMedicoResponsavel = perfilLogado.equals("MEDICO") && (medicoLogadoId == medicoResponsavelDaConsultaId);

                if (isMedicoResponsavel) {
                    btnSalvar.setDisable(false);
                    alternarBloqueioCampos(false);
                    if(txtTituloPainel != null) txtTituloPainel.setText("Registar consulta");
                } else {
                    btnSalvar.setDisable(true);
                    alternarBloqueioCampos(true);
                    if(txtTituloPainel != null) txtTituloPainel.setText("Visualização (Apenas o Médico Responsável pode registar)");
                }

                if(btnEditar != null) btnEditar.setDisable(true);

            } else {
                txtDataHoraGabinete.setText("Nenhuma consulta pendente para hoje.");
                txtMedicoeCrm.setText("-");
                btnSalvar.setDisable(true);
                alternarBloqueioCampos(true);
                if(btnEditar != null) btnEditar.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void abrirRegistoExistente(int idProntuario, String dataStr, String nomeMedico) {
        prontuarioSelecionadoId = idProntuario;
        modoEdicao = false;

        String sql = "SELECT p.*, c.data_consulta, m.sala, m.crm FROM prontuarios p " +
                "INNER JOIN consultas c ON p.consulta_id = c.id " +
                "INNER JOIN medicos m ON p.medico_id = m.id WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProntuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                medicoDoProntuarioSelecionado = rs.getInt("medico_id");

                if (txtTituloPainel != null) txtTituloPainel.setText("Visualizar Registo Médico");
                txtDataHoraGabinete.setText(dataStr + " · " + rs.getString("sala"));
                txtMedicoeCrm.setText(nomeMedico + " · " + rs.getString("crm"));
                txtTipoconsulta.setText("Registo Histórico");

                txtareaTensaoArterial.setText(rs.getString("tensao_arterial") != null ? rs.getString("tensao_arterial") : "");
                txtareaFrenquencia.setText(rs.getString("frequencia_cardiaca") != null ? rs.getString("frequencia_cardiaca") : "");
                txtareaTemp.setText(rs.getString("temperatura") != null ? rs.getString("temperatura") : "");
                txtareaNotas.setText(rs.getString("observacoes_clinicas"));

                alternarBloqueioCampos(true);
                btnSalvar.setDisable(true);

                if (btnEditar != null) {
                    String perfil = UserSession.getInstance().getPerfil();
                    boolean podeEditar = perfil.equals("ADMIN") || (perfil.equals("MEDICO") && medicoLogadoId == medicoDoProntuarioSelecionado);
                    btnEditar.setDisable(!podeEditar);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void habilitarEdicao() {
        String perfil = UserSession.getInstance().getPerfil();
        boolean podeEditar = perfil.equals("ADMIN") || (perfil.equals("MEDICO") && medicoLogadoId == medicoDoProntuarioSelecionado);

        if (!podeEditar) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Apenas o Administrador ou o Médico responsável podem editar este registo.");
            return;
        }

        modoEdicao = true;
        alternarBloqueioCampos(false);

        if (txtTituloPainel != null) txtTituloPainel.setText("Editar Registo Médico");
        btnSalvar.setDisable(false);
        if (btnEditar != null) btnEditar.setDisable(true);
    }

    private void carregarHistoricoConsultas() {
        if (vboxHistorico != null) {
            vboxHistorico.getChildren().clear();
        }

        String sql = "SELECT p.id as prontuario_id, p.data_registro, e.nome AS especialidade, " +
                "p.observacoes_clinicas, m.nome AS medico " +
                "FROM prontuarios p " +
                "INNER JOIN consultas c ON p.consulta_id = c.id " +
                "INNER JOIN medicos m ON p.medico_id = m.id " +
                "INNER JOIN especialidades e ON m.especialidade_id = e.id " +
                "WHERE c.paciente_id = ? ORDER BY p.data_registro DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                int idProntuario = rs.getInt("prontuario_id");
                String dataStr = rs.getTimestamp("data_registro").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm"));
                String esp = rs.getString("especialidade");
                String obs = rs.getString("observacoes_clinicas");
                String medico = rs.getString("medico");

                AnchorPane card = criarCardHistorico(idProntuario, dataStr, esp, obs, medico);
                if (vboxHistorico != null) vboxHistorico.getChildren().add(card);
            }

            txtRegistros.setText(count + (count == 1 ? " registo" : " registos"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private AnchorPane criarCardHistorico(int idProntuario, String data, String especialidade, String obs, String medico) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(332, 130);
        pane.setStyle("-fx-background-color: #EAF2FB; -fx-background-radius: 8;");
        pane.setCursor(Cursor.HAND);

        pane.setOnMouseClicked(event -> abrirRegistoExistente(idProntuario, data, medico));

        VBox vbox = new VBox(5);
        vbox.setLayoutX(12); vbox.setLayoutY(10);
        vbox.setPrefSize(308, 110);

        Text txtData = new Text(data);
        txtData.setFill(javafx.scene.paint.Color.valueOf("#225f9e"));
        txtData.setFont(Font.font("System", FontWeight.BOLD, 11));

        Text txtEsp = new Text(especialidade);
        txtEsp.setFont(Font.font("System", FontWeight.BOLD, 13));

        Text txtObs = new Text(obs);
        txtObs.setFill(javafx.scene.paint.Color.valueOf("#6d7c8e"));
        txtObs.setWrappingWidth(300);

        if(txtObs.getText().length() > 60) {
            txtObs.setText(txtObs.getText().substring(0, 60) + "...");
        }

        HBox hboxRodape = new HBox();
        Text txtMed = new Text(medico);
        txtMed.setFill(javafx.scene.paint.Color.valueOf("#6d7c8e"));
        txtMed.setFont(Font.font("System", 10));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAbrir = new Button();
        btnAbrir.setAlignment(javafx.geometry.Pos.CENTER);
        btnAbrir.setStyle("-fx-background-color: #EAF2FB; -fx-cursor: hand;");

        ImageView icon = new ImageView();
        icon.setFitWidth(14);
        icon.setFitHeight(14);

        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream("imagens/file.png");
            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream("/com/example/sistema_gestao_pacientes/Prontuario/imagens/file.png");
            }
            if (imageStream != null) {
                icon.setImage(new Image(imageStream));
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }

        btnAbrir.setGraphic(icon);
        btnAbrir.setOnAction(event -> abrirRegistoExistente(idProntuario, data, medico));

        hboxRodape.getChildren().addAll(txtMed, spacer, btnAbrir);
        vbox.getChildren().addAll(txtData, txtEsp, txtObs, hboxRodape);
        pane.getChildren().add(vbox);

        return pane;
    }

    private void salvarProntuario() {
        String notas = txtareaNotas.getText();
        if (notas == null || notas.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Preencha as notas clínicas.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (!modoEdicao) {
                String sqlProntuario = "INSERT INTO prontuarios (consulta_id, medico_id, tensao_arterial, frequencia_cardiaca, temperatura, observacoes_clinicas) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlProntuario)) {
                    pstmt.setInt(1, consultaAtualId);
                    pstmt.setInt(2, medicoLogadoId);
                    preencherParametrosPreparedStatement(pstmt);
                    pstmt.executeUpdate();
                }
                String sqlUpdateConsulta = "UPDATE consultas SET status = 'FINALIZADA' WHERE id = ?";
                try (PreparedStatement pstmtConsulta = conn.prepareStatement(sqlUpdateConsulta)) {
                    pstmtConsulta.setInt(1, consultaAtualId);
                    pstmtConsulta.executeUpdate();
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registo criado com sucesso!");

            } else {
                String sqlUpdate = "UPDATE prontuarios SET tensao_arterial = ?, frequencia_cardiaca = ?, temperatura = ?, observacoes_clinicas = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                    pstmt.setString(1, txtareaTensaoArterial.getText().isEmpty() ? null : txtareaTensaoArterial.getText());
                    if (txtareaFrenquencia.getText().isEmpty()) pstmt.setNull(2, java.sql.Types.INTEGER);
                    else pstmt.setInt(2, Integer.parseInt(txtareaFrenquencia.getText()));
                    if (txtareaTemp.getText().isEmpty()) pstmt.setNull(3, java.sql.Types.DECIMAL);
                    else pstmt.setDouble(3, Double.parseDouble(txtareaTemp.getText().replace(",", ".")));

                    pstmt.setString(4, notas);
                    pstmt.setInt(5, prontuarioSelecionadoId);
                    pstmt.executeUpdate();
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registo médico atualizado com sucesso!");
            }

            conn.commit();
            carregarHistoricoConsultas();
            resetarParaConsultaAtual();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Verifique os dados inseridos. (Temperatura e Frequência devem ser números).");
        }
    }

    private void preencherParametrosPreparedStatement(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(3, txtareaTensaoArterial.getText().isEmpty() ? null : txtareaTensaoArterial.getText());
        if (txtareaFrenquencia.getText().isEmpty()) pstmt.setNull(4, java.sql.Types.INTEGER);
        else pstmt.setInt(4, Integer.parseInt(txtareaFrenquencia.getText()));
        if (txtareaTemp.getText().isEmpty()) pstmt.setNull(5, java.sql.Types.DECIMAL);
        else pstmt.setDouble(5, Double.parseDouble(txtareaTemp.getText().replace(",", ".")));
        pstmt.setString(6, txtareaNotas.getText());
    }

    private void limparCampos() {
        txtareaTensaoArterial.clear();
        txtareaFrenquencia.clear();
        txtareaTemp.clear();
        txtareaNotas.clear();
    }

    private void alternarBloqueioCampos(boolean bloquear) {
        txtareaTensaoArterial.setEditable(!bloquear);
        txtareaFrenquencia.setEditable(!bloquear);
        txtareaTemp.setEditable(!bloquear);
        txtareaNotas.setEditable(!bloquear);

        String estilo = bloquear ? "-fx-opacity: 0.7;" : "-fx-opacity: 1.0;";
        txtareaTensaoArterial.setStyle(estilo);
        txtareaFrenquencia.setStyle(estilo);
        txtareaTemp.setStyle(estilo);
        txtareaNotas.setStyle(estilo);
    }

    @FXML
    private void abrirDashboard() {
        try {
            if (BtnPacientes != null && BtnPacientes.getScene() != null) {
                Object userData = BtnPacientes.getScene().getUserData();
                if (userData instanceof PaginaPrincipalController) {
                    ((PaginaPrincipalController) userData).abrirDashboard();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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