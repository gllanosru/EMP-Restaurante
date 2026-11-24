package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Usuario;
import com.ucv.restaurante.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuController {
    @FXML private BorderPane rootPane;
    @FXML private Label lblUsuario;
    @FXML private Button btnProductos;
    @FXML private Button btnMesas;
    @FXML private Button btnMeseros;
    @FXML private Button btnPedidos;
    @FXML private Button btnFacturas;

    private final AppConfig appConfig;
    private Usuario usuarioActual;

    public MenuController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        String rol = (usuario != null && usuario.getRol() != null) ? usuario.getRol().trim() : "";
        String nombre = (usuario != null) ? usuario.getUsername() : "Usuario";
        lblUsuario.setText("Usuario: " + nombre + " | Rol: " + rol);
        aplicarPermisosPorRol(rol);
    }

    private void configurarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private void aplicarPermisosPorRol(String rol) {
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);
        boolean esMozo = "Mozo".equalsIgnoreCase(rol);
        boolean esCajero = "Cajero".equalsIgnoreCase(rol);
        boolean esCocinero = "Cocinero".equalsIgnoreCase(rol);

        configurarBoton(btnProductos, esAdmin || esCocinero);
        configurarBoton(btnMesas, esAdmin || esMozo);
        configurarBoton(btnMeseros, esAdmin);
        configurarBoton(btnPedidos, esAdmin || esMozo || esCajero || esCocinero);
        configurarBoton(btnFacturas, esAdmin || esCajero);

        if (esMozo) {
            cargarVista("mesa-view.fxml");
        } else if (esCajero) {
            cargarVista("factura-view.fxml");
        } else if (esCocinero) {
            cargarVista("pedido-view.fxml");
        } else {
            cargarVista("producto-view.fxml");
        }
    }

    @FXML private void onProductos() { cargarVista("producto-view.fxml"); }
    @FXML private void onMesas() { cargarVista("mesa-view.fxml"); }
    @FXML private void onMeseros() { cargarVista("mesero-view.fxml"); }
    @FXML private void onPedidos() { cargarVista("pedido-view.fxml"); }
    @FXML private void onFacturas() { cargarVista("factura-view.fxml"); }

    @FXML
    private void onSalir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/login.fxml"));
            loader.setControllerFactory(appConfig::getController);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 420, 320));
            stage.setTitle("Restaurante - Iniciar Sesión");
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/" + fxml));
            loader.setControllerFactory(appConfig::getController);
            Parent view = loader.load();
            rootPane.setCenter(view);
        } catch (Exception ex) {
            AlertHelper.showError("Error al cargar vista", ex.getMessage());
        }
    }
}