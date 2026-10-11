package com.example.sistema_gestao_pacientes.EditarPaciente;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EditarPacienteController {

    private int pacienteId;

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
    }

    public void setPacienteId(int id) {
        this.pacienteId = id;
        carregarDadosDoPaciente();
    }

    private void carregarDadosDoPaciente() {
        String sql = "SELECT * FROM pacientes WHERE id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String utente = rs.getString("numero_utente");
                String cpf = rs.getString("cpf");
                String dataNascimentoSql = rs.getString("data_nascimento");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String responsavel = rs.getString("nome_responsavel");
                String alergia = rs.getString("alergias");
                String dataCadastro = rs.getString("data_cadastro");

                String dataNascimentoBr = "";
                if (dataNascimentoSql != null && !dataNascimentoSql.isEmpty()) {
                    try {
                        LocalDate date = LocalDate.parse(dataNascimentoSql.substring(0, 10));
                        dataNascimentoBr = date.format(formatterBr);
                    } catch (Exception e) {
                        dataNascimentoBr = dataNascimentoSql;
                    }
                }

                if (fieldNome != null) fieldNome.setText(nome != null ? nome : "");
                if (fieldUtent != null) fieldUtent.setText(utente != null ? utente : "");
                if (fieldCpf != null) fieldCpf.setText(cpf != null ? cpf : "");
                if (fieldNacimento != null) fieldNacimento.setText(dataNascimentoBr);
                if (fieldTelefone != null) fieldTelefone.setText(telefone != null ? telefone : "");
                if (fieldEmail != null) fieldEmail.setText(email != null ? email : "");
                if (fieldResponsavel != null) fieldResponsavel.setText(responsavel != null ? responsavel : "");
                if (fieldAlergia != null) fieldAlergia.setText(alergia != null ? alergia : "");

                if (txtPaciente != null) txtPaciente.setText(nome != null ? nome : "Paciente");

                if (txtDatadecadastro != null) {
                    if (dataCadastro != null && dataCadastro.length() >= 10) {
                        try {
                            LocalDate dateCad = LocalDate.parse(dataCadastro.substring(0, 10));
                            txtDatadecadastro.setText(dateCad.format(formatterBr));
                        } catch (Exception e) {
                            txtDatadecadastro.setText(dataCadastro);
                        }
                    } else {
                        txtDatadecadastro.setText("-");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados do paciente: " + e.getMessage());
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

        String sql = "UPDATE pacientes SET nome = ?, numero_utente = ?, cpf = ?, data_nascimento = ?, telefone = ?, email = ?, nome_responsavel = ?, alergias = ? WHERE id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fieldNome.getText());
            pstmt.setString(2, fieldUtent.getText());
            pstmt.setString(3, fieldCpf.getText());
            pstmt.setString(4, dataNascimentoSql);
            pstmt.setString(5, fieldTelefone.getText());
            pstmt.setString(6, fieldEmail.getText());
            pstmt.setString(7, fieldResponsavel.getText());
            pstmt.setString(8, fieldAlergia.getText());
            pstmt.setInt(9, pacienteId);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados atualizados com sucesso!");
                voltarParaPacientes();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhuma alteração foi salva.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar dados: " + e.getMessage());
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