package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {
    private final DatabaseConfig databaseConfig;

    public ProductoRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, categoria, precio, stock, activo FROM Producto ORDER BY id_producto";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Producto> buscar(String filtro) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, categoria, precio, stock, activo FROM Producto " +
                "WHERE LOWER(nombre) LIKE LOWER(?) OR LOWER(categoria) LIKE LOWER(?) ORDER BY id_producto";
        String patron = "%" + filtro.trim() + "%";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto (nombre, categoria, precio, stock, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setBoolean(5, p.isActivo());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE Producto SET nombre = ?, categoria = ?, precio = ?, stock = ?, activo = ? WHERE id_producto = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setBoolean(5, p.isActivo());
            ps.setInt(6, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void descontarStock(Connection conn, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE Producto SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Stock insuficiente para el producto ID: " + idProducto);
            }
        }
    }

    public void devolverStock(Connection conn, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE Producto SET stock = stock + ? WHERE id_producto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public boolean estaRelacionadoConPedidos(int idProducto) throws SQLException {
        String sql = "SELECT COUNT(1) FROM DetallePedido WHERE id_producto = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM Producto WHERE id_producto = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    private Producto mapRow(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"),
                rs.getBoolean("activo")
        );
    }
}