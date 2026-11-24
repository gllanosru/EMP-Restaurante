package com.ucv.restaurante.service;

import com.ucv.restaurante.model.Mesa;
import com.ucv.restaurante.repository.MesaRepository;

import java.sql.SQLException;
import java.util.List;

public class MesaService {
    private final MesaRepository repository;

    public MesaService(MesaRepository repository) {
        this.repository = repository;
    }

    public List<Mesa> listar() throws SQLException {
        return repository.listarTodas();
    }

    public List<Mesa> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return repository.listarTodas();
        }
        return repository.buscar(filtro);
    }

    public void guardar(Mesa m, String numeroTexto, String capacidadTexto, boolean esNueva) throws SQLException {
        int numero;
        int capacidad;
        try {
            numero = Integer.parseInt(numeroTexto.trim());
            capacidad = Integer.parseInt(capacidadTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese números válidos.");
        }

        if (numero <= 0) {
            throw new IllegalArgumentException("El número de mesa debe ser mayor a cero.");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero.");
        }
        if (m.getEstado() == null || m.getEstado().isBlank()) {
            m.setEstado("Libre");
        }

        m.setNumero(numero);
        m.setCapacidad(capacidad);

        if (esNueva) {
            repository.insertar(m);
        } else {
            repository.actualizar(m);
        }
    }

    public void eliminar(int idMesa) throws SQLException {
        repository.eliminar(idMesa);
    }
}