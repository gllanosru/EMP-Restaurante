package com.ucv.restaurante.controller;

import com.ucv.restaurante.config.AppConfig;
import com.ucv.restaurante.model.Factura;
import com.ucv.restaurante.service.FacturaService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacturaController {
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML private TextField txtFiltro;
    @FXML private Label lblTotal;
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Integer> colId;
    @FXML private TableColumn<Factura, Integer> colPedido;
    @FXML private TableColumn<Factura, String> colTipoComprobante;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colDocumento;
    @FXML private TableColumn<Factura, BigDecimal> colTotal;
    @FXML private TableColumn<Factura, String> colEstado;
    @FXML private TableColumn<Factura, LocalDateTime> colFecha;

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdFactura()).asObject());
        colPedido.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdPedido()).asObject());
        colTipoComprobante.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipoComprobante()));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCliente()));
        colDocumento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDocumento()));
        colTotal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getTotal()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        colFecha.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getFecha()));
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(FORMATO_FECHA));
            }
        });
        cargarDatos("");
    }

    @FXML private void onBuscar() { cargarDatos(txtFiltro.getText()); }
    @FXML private void onNuevo() { abrirFormulario(null); }

    @FXML
    private void onEditar() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione una factura.");
            return;
        }
        abrirFormulario(sel);
    }

    @FXML
    private void onEliminar() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione una factura.");
            return;
        }
        if (!AlertHelper.confirm("Confirmar", "¿Desea eliminar la factura seleccionada?")) {
            return;
        }
        try {
            service.eliminar(sel.getIdFactura());
            cargarDatos(txtFiltro.getText());
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }

    @FXML
    private void onVerComprobante() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertHelper.showWarning("Aviso", "Seleccione una factura para ver el comprobante.");
            return;
        }

        double monto = (sel.getTotal() != null) ? sel.getTotal().doubleValue() : 0.0;
        String fechaFormateada = (sel.getFecha() != null) ? sel.getFecha().format(FORMATO_FECHA) : "-";
        String texto = """
                ========================================
                           %S DE VENTA
                ========================================
                N° Comprobante : %d
                Pedido N°      : %d
                Cliente        : %s
                RUC / DNI      : %s
                Estado         : %s
                Fecha          : %s
                ----------------------------------------
                TOTAL          : S/ %.2f
                ========================================
                """.formatted(
                sel.getTipoComprobante().toUpperCase(),
                sel.getIdFactura(),
                sel.getIdPedido(),
                sel.getCliente(),
                sel.getDocumento(),
                sel.getEstado(),
                fechaFormateada,
                monto
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comprobante - " + sel.getTipoComprobante());
        alert.setHeaderText("Detalle del comprobante");

        TextArea area = new TextArea(texto);
        area.setEditable(false);
        area.setPrefSize(420, 260);
        area.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");

        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void abrirFormulario(Factura factura) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ucv/restaurante/factura-form.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::getController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(factura == null ? "Nueva factura" : "Editar factura");
            stage.setScene(new Scene(loader.load(), 380, 390));

            FacturaFormController ctrl = loader.getController();
            ctrl.setData(factura, () -> cargarDatos(txtFiltro.getText()));
            stage.showAndWait();
        } catch (Exception ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void cargarDatos(String filtro) {
        try {
            List<Factura> lista = service.buscar(filtro);
            tablaFacturas.setItems(FXCollections.observableArrayList(lista));
            lblTotal.setText("Total: " + lista.size() + " factura(s)");
        } catch (SQLException ex) {
            AlertHelper.showError("Error de base de datos", ex.getMessage());
        }
    }
}