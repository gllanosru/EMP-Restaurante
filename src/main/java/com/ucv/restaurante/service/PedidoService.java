package com.ucv.restaurante.service;

import com.ucv.restaurante.model.*;
import com.ucv.restaurante.repository.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {
    private final PedidoRepository repository;
    private final ProductoRepository productoRepository;
    private final MesaRepository mesaRepository;
    private final MeseroRepository meseroRepository;

    public PedidoService(PedidoRepository repository, ProductoRepository productoRepository,
                         MesaRepository mesaRepository, MeseroRepository meseroRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.mesaRepository = mesaRepository;
        this.meseroRepository = meseroRepository;
    }

    public List<Pedido> listar() throws SQLException {
        return repository.listarTodos();
    }

    public List<Pedido> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return repository.listarTodos();
        }
        return repository.buscar(filtro);
    }

    public List<Mesa> listarMesas() throws SQLException {
        return mesaRepository.listarTodas();
    }

    public List<Mesero> listarMeseros() throws SQLException {
        return meseroRepository.listarTodos();
    }

    public List<Producto> listarProductos() throws SQLException {
        return productoRepository.listarTodos();
    }

    public void registrarPedido(Mesa mesa, Mesero mesero, String estado, List<DetallePedido> detalles) throws SQLException {
        if (mesa == null || mesa.getIdMesa() <= 0) {
            throw new IllegalArgumentException("Seleccione una mesa válida.");
        }
        if (mesero == null || mesero.getIdMesero() <= 0) {
            throw new IllegalArgumentException("Indique el mesero.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto al pedido.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : detalles) {
            total = total.add(d.getSubtotal());
        }

        Pedido p = new Pedido();
        p.setIdMesa(mesa.getIdMesa());
        p.setIdMesero(mesero.getIdMesero());
        p.setEstado((estado == null || estado.isBlank()) ? "Abierto" : estado);
        p.setTotal(total);
        p.setFecha(LocalDateTime.now());
        p.setDetalles(detalles);

        repository.insertarConDetalles(p);
    }

    public void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("Seleccione un estado válido.");
        }
        repository.actualizarEstado(idPedido, nuevoEstado);
    }

    public void eliminar(int idPedido) throws SQLException {
        repository.eliminar(idPedido);
    }
}