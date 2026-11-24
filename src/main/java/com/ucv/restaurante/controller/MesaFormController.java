package com.ucv.restaurante.controller;

import com.ucv.restaurante.model.Mesa;
import com.ucv.restaurante.service.MesaService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MesaFormController {
    @FXML private TextField txtNumero;
    @FXML private TextField txtCapacidad;
    @FXML private ComboBox<String> cmbEstado;

    private final MesaService service;
    private Mesa mesaEdicion;
    private Runnable onSaved;

    public MesaFormController(MesaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        cmbEstado.setItems(FXCollections.observableArrayList("Libre", "Ocupada", "Reservada", "Mantenimiento"));
        cmbEstado.setValue("Libre");
    }

    public void setData(Mesa mesaEdicion, Runnable onSaved) {
        this.mesaEdicion = mesaEdicion;
        this.onSaved = onSaved;

        if (mesaEdicion != null) {
            txtNumero.setText(String.valueOf(mesaEdicion.getNumero()));
            txtCapacidad.setText(String.valueOf(mesaEdicion.getCapacidad()));
            cmbEstado.setValue(mesaEdicion.getEstado());
        }
    }

    @FXML
    private void onGuardar() {
        boolean esNuevo = (mesaEdicion == null);
        Mesa target = esNuevo ? new Mesa() : mesaEdicion;
        target.setEstado(cmbEstado.getValue());

        try {
            service.guardar(target, txtNumero.getText(), txtCapacidad.getText(), esNuevo);
            if (onSaved != null) onSaved.run();
            cerrar();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) {
                AlertHelper.showWarning("Mesa duplicada", "Ya existe una mesa registrada con ese número.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) txtNumero.getScene().getWindow()).close();
    }
}