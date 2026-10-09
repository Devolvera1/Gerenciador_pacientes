module com.example.sistema_gestao_pacientes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.sistema_gestao_pacientes to javafx.fxml;

    exports com.example.sistema_gestao_pacientes;
}