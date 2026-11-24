package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Pedido;
import com.ucv.restaurante.service.PedidoDetalleService;
import com.ucv.restaurante.service.PedidoService;
import com.ucv.restaurante.util.AlertHelper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PedidoController {
    @FXML private TextField txtFiltro;
    @FXML private Label lblTotal;
    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, Integer> colId;
    @FXML private TableColumn<Pedido, Integer> colMesa;
    @FXML private TableColumn<Pedido, String> colMesero;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private TableColumn<Pedido, BigDecimal> colTotal;
    @FXML private TableColumn<Pedido, LocalDateTime> colFecha;

    private final PedidoService pedidoService;
    private final PedidoDetalleService detalleService;

    public PedidoController(PedidoService pedidoService, PedidoDetalleService detalleService) {
        this.pedidoService = pedidoService;
        this.detalleService = detalleService;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdPedido()).asObject());
        colMesa.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getNumeroMesa()).asObject());
        colMesero.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreMesero()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        colTotal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getTotal()));
        colFecha.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getFecha()));
        cargarDatos("");
    }

    @FXML private void onBuscar() { cargarDatos(txtFiltro.getText()); }

    @FXML
    private void onNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/pedido-form.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::getController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo pedido");
            stage.setScene(new Scene(loader.load(), 620, 480));

            PedidoFormController ctrl = loader.getController();
            ctrl.setOnSaved(() -> cargarDatos(txtFiltro.getText()));
            stage.showAndWait();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        Pedido sel = tablaPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un pedido.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(sel.getEstado(), "Abierto", "Preparando", "Atendido", "Cancelado");
        dialog.setTitle("Actualizar estado");
        dialog.setHeaderText("Pedido #" + sel.getIdPedido());
        dialog.setContentText("Nuevo estado:");
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            try {
                pedidoService.actualizarEstado(sel.getIdPedido(), res.get());
                cargarDatos(txtFiltro.getText());
            } catch (Exception ex) {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    @FXML
    private void onEliminar() {
        Pedido sel = tablaPedidos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione un pedido.");
            return;
        }
        if (!AlertHelper.confirm("Confirmar", "¿Desea eliminar el pedido seleccionado?")) {
            return;
        }
        try {
            pedidoService.eliminar(sel.getIdPedido());
            cargarDatos(txtFiltro.getText());
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 547) {
                AlertHelper.showWarning("No se puede eliminar", "Este pedido tiene una factura asociada y no se puede eliminar.");
            } else {
                AlertHelper.showError("Error de base de datos", ex.getMessage());
            }
        }
    }

    private void cargarDatos(String filtro) {
        try {
            List<Pedido> lista = pedidoService.buscar(filtro);
            tablaPedidos.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " pedido(s)");
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }
}