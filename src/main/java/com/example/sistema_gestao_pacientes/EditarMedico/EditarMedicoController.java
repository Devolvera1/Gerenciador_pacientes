package com.example.sistema_gestao_pacientes.EditarMedico;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EditarMedicoController {

    private int medicoId;

    private final Map<String, Integer> mapaEspecialidades = new HashMap<>();

    @FXML private TextField fieldNome;
    @FXML private TextField fieldCpf;
    @FXML private TextField fieldCRM;
    @FXML private TextField fieldTelefone;
    @FXML private TextField fieldEmail;

    @FXML private ComboBox<String> comboStatus;
    @FXML private ComboBox<String> comboEspecialidade;
    @FXML private ComboBox<String> comboGabinete;

    @FXML private Button BtnCancelar;
    @FXML private Button BtnSalvar;
    @FXML private Button BtnVoltarMedicos;

    @FXML private Text txtMedico;
    @FXML private Text txtID;
    @FXML private Text txtCRM;
    @FXML private Text txtEspecialidade;

    @FXML
    public void initialize() {
        if (comboStatus != null) {
            comboStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo"));
        }

        if (comboGabinete != null) {
            comboGabinete.setItems(FXCollections.observableArrayList("Gabinete 1", "Gabinete 2", "Gabinete 3", "Gabinete 4", "Sala de Consultas A"));
        }

        carregarEspecialidadesCombo();

        if (BtnSalvar != null) BtnSalvar.setOnAction(event -> salvarDados());
        if (BtnCancelar != null) BtnCancelar.setOnAction(event -> voltarParaMedicos());
        if (BtnVoltarMedicos != null) BtnVoltarMedicos.setOnAction(event -> voltarParaMedicos());
    }

    private void carregarEspecialidadesCombo() {
        String sql = "SELECT id, nome FROM especialidades ORDER BY nome";
        ObservableList<String> listaNomes = FXCollections.observableArrayList();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                listaNomes.add(nome);
                mapaEspecialidades.put(nome, id);
            }

            if (comboEspecialidade != null) {
                comboEspecialidade.setItems(listaNomes);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar as especialidades: " + e.getMessage());
        }
    }

    public void setMedicoId(int id) {
        this.medicoId = id;
        carregarDadosDoMedico();
    }

    private void carregarDadosDoMedico() {
        String sql = "SELECT m.*, e.nome AS nome_especialidade " +
                "FROM medicos m " +
                "JOIN especialidades e ON m.especialidade_id = e.id " +
                "WHERE m.id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, medicoId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                String crm = rs.getString("crm");
                boolean ativo = rs.getBoolean("ativo");
                String nomeEspecialidade = rs.getString("nome_especialidade");
                String sala = rs.getString("sala");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");

                if (fieldNome != null) fieldNome.setText(nome != null ? nome : "");
                if (fieldCpf != null) fieldCpf.setText(cpf != null ? cpf : "");
                if (fieldCRM != null) fieldCRM.setText(crm != null ? crm : "");
                if (fieldTelefone != null) fieldTelefone.setText(telefone != null ? telefone : "");
                if (fieldEmail != null) fieldEmail.setText(email != null ? email : "");

                if (comboStatus != null) {
                    comboStatus.setValue(ativo ? "Ativo" : "Inativo");
                }
                if (comboEspecialidade != null) {
                    comboEspecialidade.setValue(nomeEspecialidade);
                }
                if (comboGabinete != null && sala != null) {
                    comboGabinete.setValue(sala);
                }

                if (txtMedico != null) txtMedico.setText(nome != null ? nome : "Médico");
                if (txtID != null) txtID.setText("M-" + String.format("%05d", medicoId));
                if (txtCRM != null) txtCRM.setText(crm != null ? crm : "CRM");
                if (txtEspecialidade != null) txtEspecialidade.setText(nomeEspecialidade != null ? nomeEspecialidade : "Especialidade");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados do médico: " + e.getMessage());
        }
    }

    @FXML
    private void salvarDados() {
        if (fieldNome.getText() == null || fieldNome.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "O nome do médico é obrigatório.");
            return;
        }

        boolean statusAtivo = "Ativo".equals(comboStatus.getValue());

        String especialidadeSelecionada = comboEspecialidade.getValue();
        int especialidadeId = mapaEspecialidades.getOrDefault(especialidadeSelecionada, 1);

        String salaSelecionada = comboGabinete.getValue() != null ? comboGabinete.getValue() : "Gabinete 1";

        String sql = "UPDATE medicos SET nome = ?, cpf = ?, crm = ?, ativo = ?, especialidade_id = ?, sala = ?, telefone = ?, email = ? WHERE id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fieldNome.getText().trim());
            pstmt.setString(2, fieldCpf.getText() != null ? fieldCpf.getText().trim() : "");
            pstmt.setString(3, fieldCRM.getText() != null ? fieldCRM.getText().trim() : "");
            pstmt.setBoolean(4, statusAtivo);
            pstmt.setInt(5, especialidadeId);
            pstmt.setString(6, salaSelecionada);
            pstmt.setString(7, fieldTelefone.getText() != null ? fieldTelefone.getText().trim() : "");
            pstmt.setString(8, fieldEmail.getText() != null ? fieldEmail.getText().trim() : "");
            pstmt.setInt(9, medicoId);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados do médico atualizados com sucesso!");
                voltarParaMedicos();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhuma alteração foi salva.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar dados do médico: " + e.getMessage());
        }
    }

    private void voltarParaMedicos() {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) BtnCancelar.getScene().getRoot().getUserData();

            if (principalController != null) {
                java.lang.reflect.Method metodo = PaginaPrincipalController.class.getDeclaredMethod("abrirMedicos");
                metodo.setAccessible(true);
                metodo.invoke(principalController);
            } else {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Medicos.fxml"));
                javafx.scene.Parent novaTela = loader.load();

                javafx.scene.Scene scene = BtnCancelar.getScene();
                AnchorPane conteudoCentral = (AnchorPane) scene.lookup("#conteudoCentral");

                if (conteudoCentral != null) {
                    AnchorPane.setTopAnchor(novaTela, 0.0);
                    AnchorPane.setBottomAnchor(novaTela, 0.0);
                    AnchorPane.setLeftAnchor(novaTela, 0.0);
                    AnchorPane.setRightAnchor(novaTela, 0.0);

                    conteudoCentral.getChildren().clear();
                    conteudoCentral.getChildren().add(novaTela);
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