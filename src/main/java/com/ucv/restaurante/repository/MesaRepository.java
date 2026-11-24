package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.Mesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaRepository {
    private final DatabaseConfig databaseConfig;

    public MesaRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Mesa> listarTodas() throws SQLException {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT id_mesa, numero, capacidad, estado FROM Mesa ORDER BY numero";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mesa(rs.getInt("id_mesa"), rs.getInt("numero"),
                        rs.getInt("capacidad"), rs.getString("estado")));
            }
        }
        return lista;
    }

    public List<Mesa> buscar(String filtro) throws SQLException {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT id_mesa, numero, capacidad, estado FROM Mesa " +
                "WHERE CAST(numero AS VARCHAR) LIKE ? OR LOWER(estado) LIKE LOWER(?) ORDER BY numero";
        String patron = "%" + filtro.trim() + "%";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mesa(rs.getInt("id_mesa"), rs.getInt("numero"),
                            rs.getInt("capacidad"), rs.getString("estado")));
                }
            }
        }
        return lista;
    }

    public void insertar(Mesa m) throws SQLException {
        String sql = "INSERT INTO Mesa (numero, capacidad, estado) VALUES (?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getNumero());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getEstado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Mesa m) throws SQLException {
        String sql = "UPDATE Mesa SET numero = ?, capacidad = ?, estado = ? WHERE id_mesa = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getNumero());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getEstado());
            ps.setInt(4, m.getIdMesa());
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(Connection conn, int idMesa, String estado) throws SQLException {
        String sql = "UPDATE Mesa SET estado = ? WHERE id_mesa = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idMesa) throws SQLException {
        String sql = "DELETE FROM Mesa WHERE id_mesa = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }
}