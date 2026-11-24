package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Usuario;
import com.ucv.restaurante.service.UsuarioService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final UsuarioService usuarioService;
    private final AppConfig appConfig;

    public LoginController(UsuarioService usuarioService, AppConfig appConfig) {
        this.usuarioService = usuarioService;
        this.appConfig = appConfig;
    }

    @FXML
    private void onIngresar() {
        try {
            Usuario usuario = usuarioService.login(txtUsuario.getText(), txtPassword.getText());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/menu.fxml"));
            loader.setControllerFactory(appConfig::getController);
            Scene scene = new Scene(loader.load(), 900, 600);

            MenuController menuController = loader.getController();
            menuController.setUsuarioActual(usuario);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setTitle("Menú Principal - " + usuario.getUsername() + " (" + usuario.getRol() + ")");
            stage.setScene(scene);
        } catch (IllegalArgumentException | SecurityException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }
}