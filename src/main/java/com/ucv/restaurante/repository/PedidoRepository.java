package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.DetallePedido;
import com.ucv.restaurante.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {
    private final DatabaseConfig databaseConfig;
    private final PedidoDetalleRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final MesaRepository mesaRepository;

    public PedidoRepository(DatabaseConfig databaseConfig, PedidoDetalleRepository detalleRepository,
                            ProductoRepository productoRepository, MesaRepository mesaRepository) {
        this.databaseConfig = databaseConfig;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.mesaRepository = mesaRepository;
    }

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.id_pedido, p.id_mesa, m.numero, p.id_mesero, ms.nombres, p.estado, p.total, p.fecha " +
                "FROM Pedido p " +
                "INNER JOIN Mesa m ON p.id_mesa = m.id_mesa " +
                "INNER JOIN Mesero ms ON p.id_mesero = ms.id_mesero " +
                "ORDER BY p.id_pedido DESC";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Pedido> buscar(String filtro) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.id_pedido, p.id_mesa, m.numero, p.id_mesero, ms.nombres, p.estado, p.total, p.fecha " +
                "FROM Pedido p " +
                "INNER JOIN Mesa m ON p.id_mesa = m.id_mesa " +
                "INNER JOIN Mesero ms ON p.id_mesero = ms.id_mesero " +
                "WHERE CAST(m.numero AS VARCHAR) LIKE ? OR LOWER(ms.nombres) LIKE LOWER(?) OR LOWER(p.estado) LIKE LOWER(?) " +
                "ORDER BY p.id_pedido DESC";
        String patron = "%" + filtro.trim() + "%";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public void insertarConDetalles(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO Pedido (id_mesa, id_mesero, estado, total, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pedido.getIdMesa());
                ps.setInt(2, pedido.getIdMesero());
                ps.setString(3, pedido.getEstado());
                ps.setBigDecimal(4, pedido.getTotal());
                ps.setTimestamp(5, Timestamp.valueOf(pedido.getFecha()));
                ps.executeUpdate();

                int idPedido;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No se generó el ID del pedido.");
                    }
                    idPedido = keys.getInt(1);
                }

                for (DetallePedido det : pedido.getDetalles()) {
                    productoRepository.descontarStock(conn, det.getIdProducto(), det.getCantidad());
                    detalleRepository.insertarDetalle(conn, idPedido, det);
                }

                if ("Abierto".equalsIgnoreCase(pedido.getEstado()) || "Preparando".equalsIgnoreCase(pedido.getEstado())) {
                    mesaRepository.actualizarEstado(conn, pedido.getIdMesa(), "Ocupada");
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException {
        String sql = "UPDATE Pedido SET estado = ? WHERE id_pedido = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idPedido) throws SQLException {
        List<DetallePedido> detalles = detalleRepository.listarPorPedido(idPedido);
        String sql = "DELETE FROM Pedido WHERE id_pedido = ?";
        try (Connection conn = databaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (DetallePedido d : detalles) {
                    productoRepository.devolverStock(conn, d.getIdProducto(), d.getCantidad());
                }
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idPedido);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Pedido mapRow(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdMesa(rs.getInt("id_mesa"));
        p.setNumeroMesa(rs.getInt("numero"));
        p.setIdMesero(rs.getInt("id_mesero"));
        p.setNombreMesero(rs.getString("nombres"));
        p.setEstado(rs.getString("estado"));
        p.setTotal(rs.getBigDecimal("total"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            p.setFecha(ts.toLocalDateTime());
        }
        return p;
    }
}