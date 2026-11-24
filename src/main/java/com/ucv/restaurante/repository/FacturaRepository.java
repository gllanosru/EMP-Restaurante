package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaRepository {
    private final DatabaseConfig databaseConfig;

    public FacturaRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Factura> listarTodas() throws SQLException {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT id_factura, id_pedido, tipo_comprobante, cliente, documento, total, estado, fecha " +
                "FROM Factura ORDER BY id_factura DESC";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Factura> buscar(String filtro) throws SQLException {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT id_factura, id_pedido, tipo_comprobante, cliente, documento, total, estado, fecha " +
                "FROM Factura " +
                "WHERE LOWER(cliente) LIKE LOWER(?) OR documento LIKE ? OR LOWER(estado) LIKE LOWER(?) " +
                "OR LOWER(tipo_comprobante) LIKE LOWER(?) ORDER BY id_factura DESC";
        String patron = "%" + filtro.trim() + "%";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public void insertar(Factura f) throws SQLException {
        String sql = "INSERT INTO Factura (id_pedido, tipo_comprobante, cliente, documento, total, estado, fecha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPedido());
            ps.setString(2, f.getTipoComprobante());
            ps.setString(3, f.getCliente());
            ps.setString(4, f.getDocumento());
            ps.setBigDecimal(5, f.getTotal());
            ps.setString(6, f.getEstado());
            ps.setTimestamp(7, Timestamp.valueOf(f.getFecha()));
            ps.executeUpdate();
        }
    }

    public void actualizar(Factura f) throws SQLException {
        String sql = "UPDATE Factura SET id_pedido = ?, tipo_comprobante = ?, cliente = ?, documento = ?, " +
                "total = ?, estado = ? WHERE id_factura = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPedido());
            ps.setString(2, f.getTipoComprobante());
            ps.setString(3, f.getCliente());
            ps.setString(4, f.getDocumento());
            ps.setBigDecimal(5, f.getTotal());
            ps.setString(6, f.getEstado());
            ps.setInt(7, f.getIdFactura());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idFactura) throws SQLException {
        String sql = "DELETE FROM Factura WHERE id_factura = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.executeUpdate();
        }
    }

    private Factura mapRow(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setIdFactura(rs.getInt("id_factura"));
        f.setIdPedido(rs.getInt("id_pedido"));
        f.setTipoComprobante(rs.getString("tipo_comprobante"));
        f.setCliente(rs.getString("cliente"));
        f.setDocumento(rs.getString("documento"));
        f.setTotal(rs.getBigDecimal("total"));
        f.setEstado(rs.getString("estado"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            f.setFecha(ts.toLocalDateTime());
        }
        return f;
    }
}