package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Carrega a tela de login
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/view/Login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Adiciona CSS se existir
            scene.getStylesheets().add(Main.class.getResource("/com/example/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Sistema de Gerenciamento de Parque");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 