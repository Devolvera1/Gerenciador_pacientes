package com.example.sistema_gestao_pacientes.NovoPaciente;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NovoPacienteController {

    @FXML private TextField fieldNome;
    @FXML private TextField fieldUtent;
    @FXML private TextField fieldCpf;
    @FXML private TextField fieldNacimento;
    @FXML private TextField fieldTelefone;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldResponsavel;
    @FXML private TextField fieldAlergia;

    @FXML private Button BtnCancelar;
    @FXML private Button BtnSalvar;
    @FXML private Button BtnVoltarPaciente;

    @FXML private Text txtPaciente;
    @FXML private Text txtDatadecadastro;

    private final DateTimeFormatter formatterBr = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatterSql = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        if (BtnSalvar != null) BtnSalvar.setOnAction(event -> salvarDados());
        if (BtnCancelar != null) BtnCancelar.setOnAction(event -> voltarParaPacientes());
        if (BtnVoltarPaciente != null) BtnVoltarPaciente.setOnAction(event -> voltarParaPacientes());
        if (txtDatadecadastro != null) {
            txtDatadecadastro.setText(LocalDate.now().format(formatterBr));
        }

        if (txtPaciente != null) {
            txtPaciente.setText("Novo Paciente");
        }
    }

    @FXML
    private void salvarDados() {
        String dataNascimentoSql = null;
        String textoData = fieldNacimento.getText() != null ? fieldNacimento.getText().trim() : "";

        if (!textoData.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(textoData, formatterBr);
                dataNascimentoSql = date.format(formatterSql);
            } catch (DateTimeParseException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Data Inválida", "Por favor, insira a data de nascimento no formato dd/MM/aaaa (ex: 15/05/1990).");
                return;
            }
        }

        if (fieldNome.getText() == null || fieldNome.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "O nome do paciente é obrigatório.");
            return;
        }

        String sql = "INSERT INTO pacientes (nome, numero_utente, cpf, data_nascimento, telefone, email, nome_responsavel, alergias, data_cadastro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fieldNome.getText().trim());
            pstmt.setString(2, fieldUtent.getText() != null ? fieldUtent.getText().trim() : "");
            pstmt.setString(3, fieldCpf.getText() != null ? fieldCpf.getText().trim() : "");
            pstmt.setString(4, dataNascimentoSql);
            pstmt.setString(5, fieldTelefone.getText() != null ? fieldTelefone.getText().trim() : "");
            pstmt.setString(6, fieldEmail.getText() != null ? fieldEmail.getText().trim() : "");
            pstmt.setString(7, fieldResponsavel.getText() != null ? fieldResponsavel.getText().trim() : "");
            pstmt.setString(8, fieldAlergia.getText() != null ? fieldAlergia.getText().trim() : "");
            pstmt.setString(9, LocalDate.now().format(formatterSql));

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Paciente cadastrado com sucesso!");
                voltarParaPacientes();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não foi possível cadastrar o paciente.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar dados no banco: " + e.getMessage());
        }
    }

    private void voltarParaPacientes() {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) BtnCancelar.getScene().getRoot().getUserData();

            if (principalController != null) {
                java.lang.reflect.Method metodo = PaginaPrincipalController.class.getDeclaredMethod("abrirPacientes");
                metodo.setAccessible(true);
                metodo.invoke(principalController);
            } else {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Pacientes.fxml"));
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