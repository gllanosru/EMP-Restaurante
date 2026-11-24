package com.ucv.restaurante.service;

import com.ucv.restaurante.model.Mesero;
import com.ucv.restaurante.repository.MeseroRepository;

import java.sql.SQLException;
import java.util.List;

public class MeseroService {
    private final MeseroRepository repository;

    public MeseroService(MeseroRepository repository) {
        this.repository = repository;
    }

    public List<Mesero> listar() throws SQLException {
        return repository.listarTodos();
    }

    public List<Mesero> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return repository.listarTodos();
        }
        return repository.buscar(filtro);
    }

    public void guardar(Mesero m, boolean esNuevo) throws SQLException {
        if (m.getNombres() == null || m.getNombres().isBlank()) {
            throw new IllegalArgumentException("El nombre del mesero es obligatorio.");
        }
        String dniLimpio = m.getDni() == null ? "" : m.getDni().trim();
        if (!dniLimpio.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos.");
        }

        m.setNombres(m.getNombres().trim());
        m.setDni(dniLimpio);
        m.setTelefono(m.getTelefono() == null ? "" : m.getTelefono().trim());

        if (esNuevo) {
            repository.insertar(m);
        } else {
            repository.actualizar(m);
        }
    }

    public void eliminar(int idMesero) throws SQLException {
        repository.eliminar(idMesero);
    }
}