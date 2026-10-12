package com.example.sistema_gestao_pacientes.Agenda;

import com.example.sistema_gestao_pacientes.Config.DatabaseConnection;
import com.example.sistema_gestao_pacientes.Pagina_principal.PaginaPrincipalController;
import com.example.sistema_gestao_pacientes.Util.ConsultaAgendaDTO;
import com.example.sistema_gestao_pacientes.Util.ConsultaSlot;
import com.example.sistema_gestao_pacientes.Util.LinhaAgenda;
import com.example.sistema_gestao_pacientes.Util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AgendaController {

    @FXML private Button BtnNovaConsulta;
    @FXML private Node BtnAnterior;
    @FXML private Node BtnSeguinte;
    @FXML private Button BtnHoje;
    @FXML private Button BtnDiario;
    @FXML private Button BtnSemanal;
    @FXML private Text TxtSemana;
    @FXML private ComboBox<String> ComboDr;
    @FXML private Text txtConsultas;

    @FXML private TableView<LinhaAgenda> table;
    @FXML private TableColumn<LinhaAgenda, String> colEixoHora;
    @FXML private TableColumn<LinhaAgenda, ConsultaSlot> colSegunda;
    @FXML private TableColumn<LinhaAgenda, ConsultaSlot> colTerca;
    @FXML private TableColumn<LinhaAgenda, ConsultaSlot> colQuarta;
    @FXML private TableColumn<LinhaAgenda, ConsultaSlot> colQuinta;
    @FXML private TableColumn<LinhaAgenda, ConsultaSlot> colSexta;

    private LocalDate dataExibicaoAtual;
    private final Locale localePT = new Locale("pt", "PT");
    private boolean isVisaoDiaria = false;

    @FXML
    public void initialize() {
        dataExibicaoAtual = LocalDate.now();
        configurarBotoesNavegacao();
        configurarFiltrosVisuais();
        configurarComboBoxMedicos();
        configurarTabela();
        atualizarInterface();
    }

    private void configurarBotoesNavegacao() {
        BtnNovaConsulta.setOnAction(event -> NovaConsulta());

        BtnHoje.setOnAction(event -> {
            dataExibicaoAtual = LocalDate.now();
            if (isVisaoDiaria && (dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SATURDAY || dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                dataExibicaoAtual = dataExibicaoAtual.with(DayOfWeek.MONDAY);
            }
            atualizarInterface();
            aplicarFiltroColunas();
        });

        BtnAnterior.setOnMouseClicked(event -> {
            if (isVisaoDiaria) {
                dataExibicaoAtual = dataExibicaoAtual.minusDays(1);
                if (dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dataExibicaoAtual = dataExibicaoAtual.minusDays(2);
                }
            } else {
                dataExibicaoAtual = dataExibicaoAtual.minusWeeks(1);
            }
            atualizarInterface();
            aplicarFiltroColunas();
        });

        BtnSeguinte.setOnMouseClicked(event -> {
            if (isVisaoDiaria) {
                dataExibicaoAtual = dataExibicaoAtual.plusDays(1);
                if (dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    dataExibicaoAtual = dataExibicaoAtual.plusDays(2);
                }
            } else {
                dataExibicaoAtual = dataExibicaoAtual.plusWeeks(1);
            }
            atualizarInterface();
            aplicarFiltroColunas();
        });
    }

    private void atualizarInterface() {
        atualizarTextoSemana();
        carregarDadosDoBanco();
    }

    private void atualizarTextoSemana() {
        LocalDate segunda = dataExibicaoAtual.with(DayOfWeek.MONDAY);
        LocalDate sexta = dataExibicaoAtual.with(DayOfWeek.FRIDAY);

        DateTimeFormatter formatadorMes = DateTimeFormatter.ofPattern("MMMM", localePT);
        DateTimeFormatter formatadorAno = DateTimeFormatter.ofPattern("yyyy", localePT);

        String textoData;
        if (segunda.getMonth() == sexta.getMonth()) {
            textoData = segunda.getDayOfMonth() + "–" + sexta.getDayOfMonth() + " " +
                    sexta.format(formatadorMes) + " " + sexta.format(formatadorAno);
        } else {
            textoData = segunda.getDayOfMonth() + " " + segunda.format(formatadorMes) + "–" +
                    sexta.getDayOfMonth() + " " + sexta.format(formatadorMes) + " " + sexta.format(formatadorAno);
        }

        TxtSemana.setText(textoData);
    }

    private void configurarFiltrosVisuais() {
        BtnDiario.setOnAction(event -> {
            isVisaoDiaria = true;

            if (dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SATURDAY || dataExibicaoAtual.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dataExibicaoAtual = dataExibicaoAtual.with(DayOfWeek.MONDAY);
                atualizarInterface();
            }

            aplicarFiltroColunas();

            BtnDiario.setStyle("-fx-background-color: #225F9E; -fx-border-color: #DEE5ED; -fx-border-radius: 5; -fx-text-fill: WHITE;");
            BtnSemanal.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DEE5ED; -fx-border-radius: 5; -fx-text-fill: #233449;");
        });

        BtnSemanal.setOnAction(event -> {
            isVisaoDiaria = false;

            aplicarFiltroColunas();

            BtnSemanal.setStyle("-fx-background-color: #225F9E; -fx-border-color: #DEE5ED; -fx-border-radius: 5; -fx-text-fill: WHITE;");
            BtnDiario.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DEE5ED; -fx-border-radius: 5; -fx-text-fill: #233449;");
        });
    }

    private void aplicarFiltroColunas() {
        table.getColumns().clear();

        if (isVisaoDiaria) {
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            table.getColumns().add(colEixoHora);

            TableColumn<LinhaAgenda, ConsultaSlot> colDiaAtual;
            switch (dataExibicaoAtual.getDayOfWeek()) {
                case TUESDAY -> colDiaAtual = colTerca;
                case WEDNESDAY -> colDiaAtual = colQuarta;
                case THURSDAY -> colDiaAtual = colQuinta;
                case FRIDAY -> colDiaAtual = colSexta;
                default -> colDiaAtual = colSegunda;
            }

            colDiaAtual.setPrefWidth(600);
            colDiaAtual.setMaxWidth(600);

            table.getColumns().add(colDiaAtual);

        } else {
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            colSegunda.setMaxWidth(5000);
            colTerca.setMaxWidth(5000);
            colQuarta.setMaxWidth(5000);
            colQuinta.setMaxWidth(5000);
            colSexta.setMaxWidth(5000);

            table.getColumns().addAll(colEixoHora, colSegunda, colTerca, colQuarta, colQuinta, colSexta);
        }
    }

    private void configurarComboBoxMedicos() {
        ObservableList<String> medicos = FXCollections.observableArrayList(
                "Dr. Roberto Alves", "Dra. Fernanda Lima", "Dr. Carlos Mendes",
                "Dra. Juliana Silva", "Dr. Marcos Vinicius", "Dra. Patricia Rocha"
        );
        ComboDr.setItems(medicos);

        UserSession session = UserSession.getInstance();
        if (session != null) {
            if ("MEDICO".equals(session.getPerfil())) {
                ComboDr.setValue(session.getNomeMedico());
                ComboDr.setDisable(true);
            } else {
                ComboDr.setValue(medicos.get(0));
            }
        } else {
            ComboDr.setValue(medicos.get(0));
        }

        ComboDr.setOnAction(event -> carregarDadosDoBanco());
    }

    private void configurarTabela() {
        table.setStyle("-fx-table-cell-border-color: transparent; -fx-padding: 10;");
        table.setFixedCellSize(80.0);

        colEixoHora.setMinWidth(80);
        colEixoHora.setMaxWidth(80);
        colEixoHora.setPrefWidth(80);

        colEixoHora.setCellValueFactory(cellData -> cellData.getValue().horaEixoYProperty());
        colSegunda.setCellValueFactory(cellData -> cellData.getValue().segundaProperty());
        colTerca.setCellValueFactory(cellData -> cellData.getValue().tercaProperty());
        colQuarta.setCellValueFactory(cellData -> cellData.getValue().quartaProperty());
        colQuinta.setCellValueFactory(cellData -> cellData.getValue().quintaProperty());
        colSexta.setCellValueFactory(cellData -> cellData.getValue().sextaProperty());

        colSegunda.setCellFactory(column -> criarCelulaCustomizada());
        colTerca.setCellFactory(column -> criarCelulaCustomizada());
        colQuarta.setCellFactory(column -> criarCelulaCustomizada());
        colQuinta.setCellFactory(column -> criarCelulaCustomizada());
        colSexta.setCellFactory(column -> criarCelulaCustomizada());
    }

    private TableCell<LinhaAgenda, ConsultaSlot> criarCelulaCustomizada() {
        return new TableCell<>() {
            @Override
            protected void updateItem(ConsultaSlot slot, boolean empty) {
                super.updateItem(slot, empty);

                if (empty || slot == null || slot.getStatus().equals("VAZIO")) {
                    setGraphic(null);
                    setStyle("-fx-border-color: #EAEEF4; -fx-border-width: 0 0 1 0;");
                    return;
                }

                VBox container = new VBox(5);
                container.setPadding(new Insets(8, 10, 8, 10));
                container.setMaxHeight(60);

                if (slot.getStatus().equals("ALMOCO")) {
                    Text txtAlmoco = new Text(slot.getTitulo());
                    txtAlmoco.setFill(Color.web("#A0AAB5"));
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().add(txtAlmoco);
                    setStyle("-fx-border-color: #EAEEF4; -fx-border-width: 0 0 1 0;");
                    setGraphic(container);
                    return;
                }

                Text txtHorario = new Text(slot.getHorario());
                txtHorario.setFont(Font.font("Arial", 10));

                Text txtTitulo = new Text(slot.getTitulo());
                txtTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 12));

                container.setAlignment(Pos.TOP_LEFT);
                container.getChildren().addAll(txtHorario, txtTitulo);

                String cssBase = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-width: 0 0 0 4; ";

                switch (slot.getStatus()) {
                    case "CONFIRMADA" -> {
                        txtHorario.setFill(Color.web("#225F9E"));
                        txtTitulo.setFill(Color.web("#225F9E"));
                        setStyle(cssBase + "-fx-background-color: #EDF3F8; -fx-border-color: #225F9E;");
                    }
                    case "CANCELADA" -> {
                        txtHorario.setFill(Color.web("#D9534F"));
                        txtTitulo.setFill(Color.web("#D9534F"));
                        setStyle(cssBase + "-fx-background-color: #FDEAEA; -fx-border-color: #D9534F;");
                    }
                    case "LIVRE" -> {
                        txtHorario.setFill(Color.web("#28A745"));
                        txtTitulo.setFill(Color.web("#28A745"));
                        setStyle(cssBase + "-fx-background-color: #EAF7EE; -fx-border-color: #28A745;");
                    }
                }

                VBox wrapper = new VBox(container);
                wrapper.setPadding(new Insets(5));
                wrapper.setStyle("-fx-border-color: #EAEEF4; -fx-border-width: 0 0 1 0;");

                setGraphic(wrapper);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        };
    }

    private void carregarDadosDoBanco() {
        String medicoSelecionado = ComboDr.getValue();
        if (medicoSelecionado == null) return;

        LocalDate segunda = dataExibicaoAtual.with(DayOfWeek.MONDAY);
        LocalDate sexta = dataExibicaoAtual.with(DayOfWeek.FRIDAY);

        DatabaseConnection db = new DatabaseConnection();
        List<ConsultaAgendaDTO> consultasBD = db.buscarConsultasDaSemana(medicoSelecionado, segunda, sexta);

        ObservableList<LinhaAgenda> dadosTabela = FXCollections.observableArrayList();
        for (int hora = 8; hora <= 18; hora++) {
            LinhaAgenda linha = new LinhaAgenda(String.format("%02d:00", hora));

            if (hora == 13) {
                ConsultaSlot almoco = new ConsultaSlot("", "Pausa de almoço", "ALMOCO");
                linha.setSegunda(almoco); linha.setTerca(almoco); linha.setQuarta(almoco);
                linha.setQuinta(almoco); linha.setSexta(almoco);
            }
            dadosTabela.add(linha);
        }

        int totalConfirmadas = 0;
        int totalCanceladas = 0;

        if (consultasBD != null) {
            for (ConsultaAgendaDTO c : consultasBD) {
                int horaConsulta = c.getHora().getHour();

                if (horaConsulta < 8 || horaConsulta > 18 || horaConsulta == 13) continue;

                LinhaAgenda linha = dadosTabela.get(horaConsulta - 8);

                String statusVisual;
                if (c.getStatus().equals("CANCELADA")) {
                    statusVisual = "CANCELADA";
                    totalCanceladas++;
                } else {
                    statusVisual = "CONFIRMADA";
                    totalConfirmadas++;
                }

                String horarioFormatado = String.format("%02d:00-%02d:00", horaConsulta, horaConsulta + 1);
                String nomeExibicao = c.getStatus().equals("CANCELADA") ? c.getNomePaciente() + " - Cancelada" : c.getNomePaciente();
                ConsultaSlot slot = new ConsultaSlot(horarioFormatado, nomeExibicao, statusVisual);

                switch (c.getData().getDayOfWeek()) {
                    case MONDAY -> linha.setSegunda(slot);
                    case TUESDAY -> linha.setTerca(slot);
                    case WEDNESDAY -> linha.setQuarta(slot);
                    case THURSDAY -> linha.setQuinta(slot);
                    case FRIDAY -> linha.setSexta(slot);
                }
            }
        }

        table.setItems(dadosTabela);
        atualizarTextoRodape(medicoSelecionado, totalConfirmadas, totalCanceladas);
    }

    private void atualizarTextoRodape(String medico, int confirmadas, int canceladas) {
        String texto = String.format("Hoje, %s: %d consultas confirmadas · %d cancelada · Horários disponíveis para marcação",
                medico, confirmadas, canceladas);
        txtConsultas.setText(texto);
    }

    private void NovaConsulta() {
        try {
            PaginaPrincipalController principalController = (PaginaPrincipalController) BtnNovaConsulta.getScene().getUserData();
            if (principalController != null) {
                principalController.abrirNovaConsulta();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}