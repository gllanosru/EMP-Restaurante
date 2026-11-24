package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Mesero;
import com.ucv.restaurante.service.MeseroService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class MeseroController {
    @FXML private TextField txtFiltro;
    @FXML private Label lblTotal;
    @FXML private TableView<Mesero> tablaMeseros;
    @FXML private TableColumn<Mesero, Integer> colId;
    @FXML private TableColumn<Mesero, String> colNombres;
    @FXML private TableColumn<Mesero, String> colDni;
    @FXML private TableColumn<Mesero, String> colTelefono;
    @FXML private TableColumn<Mesero, Boolean> colActivo;

    private final MeseroService service;

    public MeseroController(MeseroService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdMesero()).asObject());
        colNombres.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombres()));
        colDni.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDni()));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefono()));
        colActivo.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isActivo()));
        cargarDatos("");
    }

    @FXML private void onBuscar() { cargarDatos(txtFiltro.getText()); }
    @FXML private void onNuevo() { abrirFormulario(null); }

    @FXML
    private void onEditar() {
        Mesero sel = tablaMeseros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un mesero.");
            return;
        }
        abrirFormulario(sel);
    }

    @FXML
    private void onEliminar() {
        Mesero sel = tablaMeseros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un mesero.");
            return;
        }
        if (!AlertHelper.confirm("Confirmar", "¿Desea eliminar el mesero seleccionado?")) {
            return;
        }
        try {
            service.eliminar(sel.getIdMesero());
            cargarDatos(txtFiltro.getText());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 547) {
                AlertHelper.showWarning("No se puede eliminar", "Este mesero tiene pedidos registrados y no se puede eliminar.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    private void abrirFormulario(Mesero mesero) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/mesero-form.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::getController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(mesero == null ? "Nuevo mesero" : "Editar mesero");
            stage.setScene(new Scene(loader.load(), 380, 300));

            MeseroFormController ctrl = loader.getController();
            ctrl.setData(mesero, () -> cargarDatos(txtFiltro.getText()));
            stage.showAndWait();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void cargarDatos(String filtro) {
        try {
            List<Mesero> lista = service.buscar(filtro);
            tablaMeseros.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " mesero(s)");
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }
}