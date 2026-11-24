package com.ucv.restaurante.controller;

import com.ucv.restaurante.model.Producto;
import com.ucv.restaurante.service.ProductoService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProductoFormController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private CheckBox chkActivo;

    private final ProductoService service;
    private Producto productoEdicion;
    private Runnable onSaved;

    public ProductoFormController(ProductoService service) {
        this.service = service;
    }

    public void setData(Producto productoEdicion, Runnable onSaved) {
        this.productoEdicion = productoEdicion;
        this.onSaved = onSaved;

        if (productoEdicion != null) {
            txtNombre.setText(productoEdicion.getNombre());
            txtCategoria.setText(productoEdicion.getCategoria());
            txtPrecio.setText(productoEdicion.getPrecio().toPlainString());
            txtStock.setText(String.valueOf(productoEdicion.getStock()));
            chkActivo.setSelected(productoEdicion.isActivo());
        } else {
            chkActivo.setSelected(true);
        }
    }

    @FXML
    private void onGuardar() {
        boolean esNuevo = (productoEdicion == null);
        Producto target = esNuevo ? new Producto() : productoEdicion;
        target.setNombre(txtNombre.getText());
        target.setCategoria(txtCategoria.getText());
        target.setActivo(chkActivo.isSelected());

        try {
            service.guardar(target, txtPrecio.getText(), txtStock.getText(), esNuevo);
            if (onSaved != null) onSaved.run();
            cerrar();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}