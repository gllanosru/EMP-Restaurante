package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.DetallePedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDetalleRepository {
    private final DatabaseConfig databaseConfig;

    public PedidoDetalleRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void insertarDetalle(Connection conn, int idPedido, DetallePedido d) throws SQLException {
        String sql = "INSERT INTO DetallePedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getPrecioUnitario());
            ps.setBigDecimal(5, d.getSubtotal());
            ps.executeUpdate();
        }
    }

    public List<DetallePedido> listarPorPedido(int idPedido) throws SQLException {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT d.id_detalle, d.id_pedido, d.id_producto, p.nombre, d.cantidad, d.precio_unitario, d.subtotal " +
                "FROM DetallePedido d INNER JOIN Producto p ON d.id_producto = p.id_producto " +
                "WHERE d.id_pedido = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetallePedido(
                            rs.getInt("id_detalle"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getBigDecimal("subtotal")
                    ));
                }
            }
        }
        return lista;
    }
}