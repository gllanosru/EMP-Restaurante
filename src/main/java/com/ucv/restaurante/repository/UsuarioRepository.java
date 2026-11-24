package com.ucv.restaurante.repository;

import com.ucv.restaurante.config.DatabaseConfig;
import com.ucv.restaurante.model.Usuario;

import java.sql.*;

public class UsuarioRepository {
    private final DatabaseConfig databaseConfig;

    public UsuarioRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Usuario autenticar(String username, String password) throws SQLException {
        String sql = "SELECT id_usuario, username, rol, activo FROM Usuario WHERE username = ? AND password_hash = ? AND activo = 1";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        }
        return null;
    }
}