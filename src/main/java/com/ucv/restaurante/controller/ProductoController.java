package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Producto;
import com.ucv.restaurante.service.ProductoService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {
    @FXML private TextField txtFiltro;
    @FXML private Label lblTotal;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdProducto()).asObject());
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoria()));
        colPrecio.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getPrecio()));
        colStock.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getStock()).asObject());
        colActivo.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isActivo()));
        cargarDatos("");
    }

    @FXML private void onBuscar() { cargarDatos(txtFiltro.getText()); }
    @FXML private void onNuevo() { abrirFormulario(null); }

    @FXML
    private void onEditar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un producto.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    private void onEliminar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un producto.");
            return;
        }
        if (!AlertHelper.confirm("Confirmar", "¿Desea eliminar el producto seleccionado?")) {
            return;
        }
        try {
            service.eliminar(seleccionado.getIdProducto());
            cargarDatos(txtFiltro.getText());
        } catch (IllegalStateException ex) {
            AlertHelper.showWarning("Advertencia", ex.getMessage());
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }

    private void abrirFormulario(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/producto-form.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::getController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(producto == null ? "Nuevo producto" : "Editar producto");
            stage.setScene(new Scene(loader.load(), 380, 320));

            ProductoFormController ctrl = loader.getController();
            ctrl.setData(producto, () -> cargarDatos(txtFiltro.getText()));
            stage.showAndWait();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void cargarDatos(String filtro) {
        try {
            List<Producto> lista = service.buscar(filtro);
            tablaProductos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " producto(s)");
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }
}