package com.ucv.restaurante.service;

import com.ucv.restaurante.model.Factura;
import com.ucv.restaurante.model.Pedido;
import com.ucv.restaurante.repository.FacturaRepository;
import com.ucv.restaurante.repository.PedidoRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaService {
    private final FacturaRepository repository;
    private final PedidoRepository pedidoRepository;

    public FacturaService(FacturaRepository repository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Factura> listar() throws SQLException {
        return repository.listarTodas();
    }

    public List<Factura> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return repository.listarTodas();
        }
        return repository.buscar(filtro);
    }

    public List<Pedido> listarPedidos() throws SQLException {
        return pedidoRepository.listarTodos();
    }

    public void guardar(Factura f, String pedidoTexto, String totalTexto, boolean esNueva) throws SQLException {
        if (pedidoTexto == null || pedidoTexto.isBlank() || totalTexto == null || totalTexto.isBlank()) {
            throw new IllegalArgumentException("Ingrese pedido y total válidos.");
        }

        int idPedido;
        BigDecimal total;
        try {
            idPedido = Integer.parseInt(pedidoTexto.trim());
            total = new BigDecimal(totalTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese pedido y total válidos.");
        }

        if (idPedido <= 0) {
            throw new IllegalArgumentException("El pedido asociado es obligatorio.");
        }
        if (f.getCliente() == null || f.getCliente().isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (f.getEstado() == null || f.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo.");
        }

        f.setIdPedido(idPedido);
        f.setCliente(f.getCliente().trim());
        f.setDocumento(f.getDocumento() == null ? "" : f.getDocumento().trim());
        f.setTotal(total);

        if (esNueva) {
            f.setFecha(LocalDateTime.now());
            repository.insertar(f);
        } else {
            repository.actualizar(f);
        }
    }
    public void guardarConPedido(Factura f, Pedido pedidoSeleccionado, String totalTexto, boolean esNuevo) throws SQLException {
        if (pedidoSeleccionado == null || pedidoSeleccionado.getIdPedido() <= 0) {
            throw new IllegalArgumentException("Seleccione un pedido de la lista.");
        }
        String totalFinal = (pedidoSeleccionado.getTotal() != null)
                ? pedidoSeleccionado.getTotal().toPlainString()
                : totalTexto;

        guardar(f, String.valueOf(pedidoSeleccionado.getIdPedido()), totalFinal, esNuevo);
    }
    public void eliminar(int idFactura) throws SQLException {
        repository.eliminar(idFactura);
    }
}