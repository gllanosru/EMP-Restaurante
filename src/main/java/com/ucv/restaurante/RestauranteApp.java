package com.ucv.restaurante;

import com.ucv.restaurante.config.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RestauranteApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/login.fxml"));
        loader.setControllerFactory(AppConfig.getInstance()::getController);
        Scene scene = new Scene(loader.load(), 420, 320);

        stage.setTitle("Iniciar Sesión");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        AppConfig.getInstance().destroy();
    }
}