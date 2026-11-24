package com.ucv.restaurante.controller;

import com.ucv.restaurante.model.Factura;
import com.ucv.restaurante.model.Pedido;
import com.ucv.restaurante.service.FacturaService;
import com.ucv.restaurante.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class FacturaFormController {
    @FXML private ComboBox<Pedido> cmbPedido;
    @FXML private ComboBox<String> cmbTipoComprobante;
    @FXML private TextField txtCliente;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbEstado;

    private final FacturaService service;
    private Factura facturaEdicion;
    private Runnable onSaved;

    public FacturaFormController(FacturaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        cmbTipoComprobante.setItems(FXCollections.observableArrayList("Boleta", "Factura", "Ticket"));
        cmbTipoComprobante.setValue("Factura");

        cmbEstado.setItems(FXCollections.observableArrayList("Emitida", "Pagada", "Anulada"));
        cmbEstado.setValue("Emitida");

        try {
            List<Pedido> pedidos = service.listarPedidos();
            cmbPedido.setItems(FXCollections.observableArrayList(pedidos));
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }

        cmbPedido.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getTotal() != null) {
                txtTotal.setText(newVal.getTotal().toPlainString());
            } else {
                txtTotal.clear();
            }
        });
    }

    public void setData(Factura facturaEdicion, Runnable onSaved) {
        this.facturaEdicion = facturaEdicion;
        this.onSaved = onSaved;

        if (facturaEdicion != null) {
            for (Pedido p : cmbPedido.getItems()) {
                if (p.getIdPedido() == facturaEdicion.getIdPedido()) {
                    cmbPedido.setValue(p);
                    break;
                }
            }
            cmbTipoComprobante.setValue(facturaEdicion.getTipoComprobante());
            txtCliente.setText(facturaEdicion.getCliente());
            txtDocumento.setText(facturaEdicion.getDocumento());
            txtTotal.setText(facturaEdicion.getTotal().toPlainString());
            cmbEstado.setValue(facturaEdicion.getEstado());
        }
    }

    @FXML
    private void onGuardar() {
        boolean esNuevo = (facturaEdicion == null);
        Factura target = esNuevo ? new Factura() : facturaEdicion;
        target.setTipoComprobante(cmbTipoComprobante.getValue());
        target.setCliente(txtCliente.getText());
        target.setDocumento(txtDocumento.getText());
        target.setEstado(cmbEstado.getValue());

        try {
            service.guardarConPedido(target, cmbPedido.getValue(), txtTotal.getText(), esNuevo);
            if (onSaved != null) onSaved.run();
            cerrar();
        } catch (IllegalArgumentException ex) {
            AlertHelper.showWarning("Validación", ex.getMessage());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) {
                AlertHelper.showWarning("Factura duplicada", "Ya existe una factura registrada para ese pedido.");
            } else if (ex.getErrorCode() == 547) {
                AlertHelper.showWarning("Pedido no válido", "El pedido seleccionado no es válido.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    @FXML private void onCancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) txtCliente.getScene().getWindow()).close();
    }
}