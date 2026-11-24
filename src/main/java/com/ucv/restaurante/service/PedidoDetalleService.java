package com.ucv.restaurante.service;

import com.ucv.restaurante.model.DetallePedido;
import com.ucv.restaurante.model.Producto;
import com.ucv.restaurante.repository.PedidoDetalleRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PedidoDetalleService {
    private final PedidoDetalleRepository repository;

    public PedidoDetalleService(PedidoDetalleRepository repository) {
        this.repository = repository;
    }

    public DetallePedido crearLinea(Producto producto, String cantidadTexto) {
        if (producto == null) {
            throw new IllegalArgumentException("Seleccione un producto.");
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad > producto.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        BigDecimal precioUnitario = producto.getPrecio();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        DetallePedido det = new DetallePedido();
        det.setIdProducto(producto.getIdProducto());
        det.setNombreProducto(producto.getNombre());
        det.setCantidad(cantidad);
        det.setPrecioUnitario(precioUnitario);
        det.setSubtotal(subtotal);
        return det;
    }

    public BigDecimal calcularTotal(List<DetallePedido> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : detalles) {
            total = total.add(d.getSubtotal());
        }
        return total;
    }

    public List<DetallePedido> listarPorPedido(int idPedido) throws SQLException {
        return repository.listarPorPedido(idPedido);
    }
}