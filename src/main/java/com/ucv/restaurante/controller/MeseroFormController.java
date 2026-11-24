package com.ucv.restaurante.controller;

import com.ucv.restaurante.model.Mesero;
import com.ucv.restaurante.service.MeseroService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MeseroFormController {
    @FXML private TextField txtNombres;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private CheckBox chkActivo;

    private final MeseroService service;
    private Mesero meseroEdicion;
    private Runnable onSaved;

    public MeseroFormController(MeseroService service) {
        this.service = service;
    }

    public void setData(Mesero meseroEdicion, Runnable onSaved) {
        this.meseroEdicion = meseroEdicion;
        this.onSaved = onSaved;

        if (meseroEdicion != null) {
            txtNombres.setText(meseroEdicion.getNombres());
            txtDni.setText(meseroEdicion.getDni());
            txtTelefono.setText(meseroEdicion.getTelefono());
            chkActivo.setSelected(meseroEdicion.isActivo());
        } else {
            chkActivo.setSelected(true);
        }
    }

    @FXML
    private void onGuardar() {
        boolean esNuevo = (meseroEdicion == null);
        Mesero target = esNuevo ? new Mesero() : meseroEdicion;
        target.setNombres(txtNombres.getText());
        target.setDni(txtDni.getText());
        target.setTelefono(txtTelefono.getText());
        target.setActivo(chkActivo.isSelected());

        try {
            service.guardar(target, esNuevo);
            if (onSaved != null) onSaved.run();
            cerrar();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) {
                AlertHelper.showWarning("DNI duplicado", "Ya existe un mesero registrado con ese DNI.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) txtNombres.getScene().getWindow()).close();
    }
}