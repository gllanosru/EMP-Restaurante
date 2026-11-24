package com.ucv.restaurante.service;

import com.ucv.restaurante.model.Usuario;
import com.ucv.restaurante.repository.UsuarioRepository;

import java.sql.SQLException;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ingrese usuario y contraseña.");
        }
        Usuario usuario = repository.autenticar(username.trim(), password);
        if (usuario == null) {
            throw new SecurityException("Usuario o contraseña incorrectos.");
        }
        return usuario;
    }
}