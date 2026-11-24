package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Mesa;
import com.ucv.restaurante.service.MesaService;
import com.ucv.restaurante.util.AlertHelper;
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

public class MesaController {
    @FXML private TextField txtFiltro;
    @FXML private Label lblTotal;
    @FXML private TableView<Mesa> tablaMesas;
    @FXML private TableColumn<Mesa, Integer> colId;
    @FXML private TableColumn<Mesa, Integer> colNumero;
    @FXML private TableColumn<Mesa, Integer> colCapacidad;
    @FXML private TableColumn<Mesa, String> colEstado;

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdMesa()).asObject());
        colNumero.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getNumero()).asObject());
        colCapacidad.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCapacidad()).asObject());
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        cargarDatos("");
    }

    @FXML private void onBuscar() { cargarDatos(txtFiltro.getText()); }
    @FXML private void onNuevo() { abrirFormulario(null); }

    @FXML
    private void onEditar() {
        Mesa sel = tablaMesas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione una mesa.");
            return;
        }
        abrirFormulario(sel);
    }

    @FXML
    private void onEliminar() {
        Mesa sel = tablaMesas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione una mesa.");
            return;
        }
        if (!AlertHelper.confirm("Confirmar", "¿Desea eliminar la mesa seleccionada?")) {
            return;
        }
        try {
            service.eliminar(sel.getIdMesa());
            cargarDatos(txtFiltro.getText());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 547) {
                AlertHelper.showWarning("No se puede eliminar", "Esta mesa tiene pedidos asociados y no se puede eliminar.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    private void abrirFormulario(Mesa mesa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/mesa-form.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::getController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(mesa == null ? "Nueva mesa" : "Editar mesa");
            stage.setScene(new Scene(loader.load(), 360, 260));

            MesaFormController ctrl = loader.getController();
            ctrl.setData(mesa, () -> cargarDatos(txtFiltro.getText()));
            stage.showAndWait();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void cargarDatos(String filtro) {
        try {
            List<Mesa> lista = service.buscar(filtro);
            tablaMesas.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " mesa(s)");
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }
}