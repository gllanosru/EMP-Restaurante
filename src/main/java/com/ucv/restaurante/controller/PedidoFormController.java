package com.ucv.restaurante.controller;

import com.ucv.restaurante.model.*;
import com.ucv.restaurante.service.PedidoDetalleService;
import com.ucv.restaurante.service.PedidoService;
import com.ucv.restaurante.service.ProductoService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;

public class PedidoFormController {
    @FXML private ComboBox<Mesa> cmbMesa;
    @FXML private ComboBox<Mesero> cmbMesero;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private Label lblTotalPedido;
    @FXML private TableView<DetallePedido> tablaDetalle;
    @FXML private TableColumn<DetallePedido, String> colProducto;
    @FXML private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML private TableColumn<DetallePedido, BigDecimal> colPrecio;
    @FXML private TableColumn<DetallePedido, BigDecimal> colSubtotal;

    private final ObservableList<DetallePedido> lineas = FXCollections.observableArrayList();
    private final PedidoService pedidoService;
    private final PedidoDetalleService detalleService;
    private final ProductoService productoService;
    private Runnable onSaved;

    public PedidoFormController(PedidoService pedidoService, PedidoDetalleService detalleService, ProductoService productoService) {
        this.pedidoService = pedidoService;
        this.detalleService = detalleService;
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        cmbEstado.setItems(FXCollections.observableArrayList("Abierto", "Preparando", "Atendido", "Cancelado"));
        cmbEstado.setValue("Abierto");

        colProducto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreProducto()));
        colCantidad.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getPrecioUnitario()));
        colSubtotal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getSubtotal()));
        tablaDetalle.setItems(lineas);
        actualizarTotalLabel();

        try {
            cmbMesa.setItems(FXCollections.observableArrayList(pedidoService.listarMesas()));
            cmbMesero.setItems(FXCollections.observableArrayList(pedidoService.listarMeseros()));
            cmbProducto.setItems(FXCollections.observableArrayList(productoService.listar()));
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void onAgregar() {
        try {
            DetallePedido linea = detalleService.crearLinea(cmbProducto.getValue(), txtCantidad.getText());
            lineas.add(linea);
            txtCantidad.clear();
            actualizarTotalLabel();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        }
    }

    @FXML
    private void onQuitar() {
        DetallePedido sel = tablaDetalle.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un producto del detalle.");
            return;
        }
        lineas.remove(sel);
        actualizarTotalLabel();
    }

    @FXML
    private void onGuardarPedido() {
        try {
            pedidoService.registrarPedido(cmbMesa.getValue(), cmbMesero.getValue(), cmbEstado.getValue(), lineas);
            if (onSaved != null) onSaved.run();
            cerrar();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void actualizarTotalLabel() {
        BigDecimal total = detalleService.calcularTotal(lineas);
        lblTotalPedido.setText(String.format("Total: S/ %.2f", total));
    }

    private void cerrar() {
        ((Stage) cmbMesa.getScene().getWindow()).close();
    }
}