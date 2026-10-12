package com.example.sistema_gestao_pacientes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Login.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Sistema de Gestão de Pacientes");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/LogoIcon.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}