package com.example.sistema_gestao_pacientes.Pagina_principal;

import com.example.sistema_gestao_pacientes.EditarMedico.EditarMedicoController;
import com.example.sistema_gestao_pacientes.EditarPaciente.EditarPacienteController;
import com.example.sistema_gestao_pacientes.NovaConsulta.NovaConsultaController;
import com.example.sistema_gestao_pacientes.Util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PaginaPrincipalController {

    @FXML private Text lblData;
    @FXML private Text lblPage;
    @FXML private Text lblMedico;
    @FXML private Text lblPerfil;
    @FXML private AnchorPane conteudoCentral;

    @FXML
    public void initialize() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("E, d MMMM yyyy", new Locale("pt", "BR"));
        lblData.setText(LocalDateTime.now().format(formatador));

        UserSession sessao = UserSession.getInstance();

        if (sessao != null) {
            lblPerfil.setText(sessao.getPerfil());

            if ("MEDICO".equals(sessao.getPerfil()) && sessao.getNomeMedico() != null) {
                lblMedico.setText(sessao.getNomeMedico());
            } else {
                lblMedico.setText(sessao.getUsername());
            }
        } else {
            lblPerfil.setText("---");
            lblMedico.setText("Nenhum usuário logado");
        }

        // Listener único para injetar o controller na cena
        conteudoCentral.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setUserData(this);
            }
        });

        carregarTela("Dashboard.fxml", "Dashboard");
    }

    public void abrirNovaConsulta() {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/com/example/sistema_gestao_pacientes/NovaConsulta/NovaConsulta.fxml");

            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/NovaConsulta.fxml");
            }

            if (fxmlUrl == null) {
                throw new IOException("Arquivo NovaConsulta.fxml não foi encontrado nos recursos.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newtela = loader.load();

            NovaConsultaController controller = loader.getController();
            if (controller != null) {
            }

            AnchorPane.setTopAnchor(newtela, 0.0);
            AnchorPane.setBottomAnchor(newtela, 0.0);
            AnchorPane.setLeftAnchor(newtela, 0.0);
            AnchorPane.setRightAnchor(newtela, 0.0);

            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(newtela);
            lblPage.setText("Área de trabalho  /  Nova consulta");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRO GRAVE: Não foi possível carregar a tela de nova consulta.");
        }
    }


    private void carregarTela(String fxmlFileName, String nomePagina) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFileName));
            Parent novaTela = loader.load();

            AnchorPane.setTopAnchor(novaTela, 0.0);
            AnchorPane.setBottomAnchor(novaTela, 0.0);
            AnchorPane.setLeftAnchor(novaTela, 0.0);
            AnchorPane.setRightAnchor(novaTela, 0.0);

            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(novaTela);


            lblPage.setText("Área de trabalho  /  " + nomePagina);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRO GRAVE: Não foi possível carregar a tela: " + fxmlFileName);
        }
    }

    @FXML
    private void abrirDashboard() {
        carregarTela("Dashboard.fxml", "Dashboard");
    }

    @FXML
    private void abrirPacientes() {
        carregarTela("Pacientes.fxml", "Pacientes");
    }

    @FXML
    private void abrirMedicos() {
        carregarTela("Medicos.fxml", "Medicos");
    }

    @FXML
    private void abrirAgenda() {
        carregarTela("Agenda.fxml", "Agenda");
    }

    @FXML
    private void abrirProntuario() {
       carregarTela("Prontuario.fxml", "Prontuario");
    }

    @FXML
    private void onMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #E2EDF8; -fx-cursor: hand;");
    }

    @FXML
    private void onMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent;");
    }

    public void abrirTelaEditarPaciente(int pacienteId) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/com/example/sistema_gestao_pacientes/EditarPaciente/EditarPaciente.fxml");

            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/EditarPaciente.fxml");
            }

            if (fxmlUrl == null) {
                throw new IOException("Arquivo EditarPaciente.fxml não foi encontrado nos recursos.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent novaTela = loader.load();

            EditarPacienteController controller = loader.getController();
            if (controller != null) {
                controller.setPacienteId(pacienteId);
            }

            AnchorPane.setTopAnchor(novaTela, 0.0);
            AnchorPane.setBottomAnchor(novaTela, 0.0);
            AnchorPane.setLeftAnchor(novaTela, 0.0);
            AnchorPane.setRightAnchor(novaTela, 0.0);

            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(novaTela);
            lblPage.setText("Área de trabalho  /  Pacientes  /  Editar paciente");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRO GRAVE: Não foi possível carregar a tela de edição.");
        }
    }

    public void abrirTelaEditarMedico(int medicoId) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/EditarMedico.fxml");

            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/EditarMedico.fxml");
            }

            if (fxmlUrl == null) {
                throw new IOException("Arquivo EditarMedico.fxml não foi encontrado nos recursos.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent novaTela = loader.load();

            EditarMedicoController controller = loader.getController();
            if (controller != null) {
                controller.setMedicoId(medicoId);
            }

            AnchorPane.setTopAnchor(novaTela, 0.0);
            AnchorPane.setBottomAnchor(novaTela, 0.0);
            AnchorPane.setLeftAnchor(novaTela, 0.0);
            AnchorPane.setRightAnchor(novaTela, 0.0);

            conteudoCentral.getChildren().clear();
            conteudoCentral.getChildren().add(novaTela);
            lblPage.setText("Área de trabalho  /  Medico  /  Editar medico");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRO GRAVE: Não foi possível carregar a tela de edição.");
        }
    }




    @FXML
    private void sair(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            stage.setMaximized(true);


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao tentar voltar para a tela de Login.");
        }
    }
}