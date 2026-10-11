module com.example.sistema_gestao_pacientes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    exports com.example.sistema_gestao_pacientes;
    opens com.example.sistema_gestao_pacientes to javafx.fxml;

    exports Dashboard;
    opens Dashboard to javafx.fxml;

    exports Agenda;
    opens Agenda to javafx.fxml;

    exports EditarPaciente;
    opens EditarPaciente to javafx.fxml;

    exports Login;
    opens Login to javafx.fxml;

    exports Medicos;
    opens Medicos to javafx.fxml;

    exports Pacientes;
    opens Pacientes to javafx.fxml;

    exports PaginaPrincipal;
    opens PaginaPrincipal to javafx.fxml;

    exports Prontuario;
    opens Prontuario to javafx.fxml;
}