package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.Mesero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeseroRepository {
    private final DatabaseConfig databaseConfig;

    public MeseroRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public List<Mesero> listarTodos() throws SQLException {
        List<Mesero> lista = new ArrayList<>();
        String sql = "SELECT id_mesero, nombres, dni, telefono, activo FROM Mesero ORDER BY id_mesero";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mesero(rs.getInt("id_mesero"), rs.getString("nombres"),
                        rs.getString("dni"), rs.getString("telefono"), rs.getBoolean("activo")));
            }
        }
        return lista;
    }

    public List<Mesero> buscar(String filtro) throws SQLException {
        List<Mesero> lista = new ArrayList<>();
        String sql = "SELECT id_mesero, nombres, dni, telefono, activo FROM Mesero " +
                "WHERE LOWER(nombres) LIKE LOWER(?) OR dni LIKE ? ORDER BY id_mesero";
        String patron = "%" + filtro.trim() + "%";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Mesero(rs.getInt("id_mesero"), rs.getString("nombres"),
                            rs.getString("dni"), rs.getString("telefono"), rs.getBoolean("activo")));
                }
            }
        }
        return lista;
    }

    public void insertar(Mesero m) throws SQLException {
        String sql = "INSERT INTO Mesero (nombres, dni, telefono, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombres());
            ps.setString(2, m.getDni());
            ps.setString(3, m.getTelefono());
            ps.setBoolean(4, m.isActivo());
            ps.executeUpdate();
        }
    }

    public void actualizar(Mesero m) throws SQLException {
        String sql = "UPDATE Mesero SET nombres = ?, dni = ?, telefono = ?, activo = ? WHERE id_mesero = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombres());
            ps.setString(2, m.getDni());
            ps.setString(3, m.getTelefono());
            ps.setBoolean(4, m.isActivo());
            ps.setInt(5, m.getIdMesero());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idMesero) throws SQLException {
        String sql = "DELETE FROM Mesero WHERE id_mesero = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMesero);
            ps.executeUpdate();
        }
    }
}