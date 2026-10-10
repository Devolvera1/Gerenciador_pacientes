package com.example.sistema_gestao_pacientes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PaginaPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaginaPrincipal.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Sistema de Gestão de Pacientes - Pagina Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}