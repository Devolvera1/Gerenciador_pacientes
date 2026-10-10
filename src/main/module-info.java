module com.example.sistema_gestao_pacientes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    opens com.example.sistema_gestao_pacientes to javafx.fxml;

    exports com.example.sistema_gestao_pacientes;
}
