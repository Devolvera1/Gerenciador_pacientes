package com.example.sistema_gestao_pacientes.NovoMedico;

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

public class NovoMedicoController {

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
    @FXML private Text txtEspecialidade;

    @FXML
    public void initialize() {
        if (comboStatus != null) {
            comboStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo"));
            comboStatus.setValue("Ativo");
        }

        if (comboGabinete != null) {
            comboGabinete.setItems(FXCollections.observableArrayList("Gabinete 1", "Gabinete 2", "Gabinete 3", "Gabinete 4", "Sala de Consultas A"));
            comboGabinete.setValue("Gabinete 1");
        }

        carregarEspecialidadesCombo();

        if (BtnSalvar != null) BtnSalvar.setOnAction(event -> salvarDados());
        if (BtnCancelar != null) BtnCancelar.setOnAction(event -> voltarParaMedicos());
        if (BtnVoltarMedicos != null) BtnVoltarMedicos.setOnAction(event -> voltarParaMedicos());

        if (txtMedico != null) {
            txtMedico.setText("Novo Médico");
        }
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

    @FXML
    private void salvarDados() {
        if (fieldNome.getText() == null || fieldNome.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "O nome do médico é obrigatório.");
            return;
        }

        if (fieldCRM.getText() == null || fieldCRM.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "O CRM do médico é obrigatório.");
            return;
        }

        boolean statusAtivo = "Ativo".equals(comboStatus.getValue());

        String especialidadeSelecionada = comboEspecialidade.getValue();
        int especialidadeId = mapaEspecialidades.getOrDefault(especialidadeSelecionada, 1);

        String salaSelecionada = comboGabinete.getValue() != null ? comboGabinete.getValue() : "Gabinete 1";

        String sql = "INSERT INTO medicos (nome, crm, cpf, email, telefone, especialidade_id, sala, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fieldNome.getText().trim());
            pstmt.setString(2, fieldCRM.getText() != null ? fieldCRM.getText().trim() : "");
            pstmt.setString(3, fieldCpf.getText() != null ? fieldCpf.getText().trim() : "");
            pstmt.setString(4, fieldEmail.getText() != null ? fieldEmail.getText().trim() : "");
            pstmt.setString(5, fieldTelefone.getText() != null ? fieldTelefone.getText().trim() : "");
            pstmt.setInt(6, especialidadeId);
            pstmt.setString(7, salaSelecionada);
            pstmt.setBoolean(8, statusAtivo);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Médico cadastrado com sucesso!");
                voltarParaMedicos();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não foi possível cadastrar o médico.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar dados no banco: " + e.getMessage());
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