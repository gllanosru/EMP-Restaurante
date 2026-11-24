package com.ucv.restaurante.service;

import com.ucv.restaurante.model.Producto;
import com.ucv.restaurante.repository.ProductoRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() throws SQLException {
        return repository.listarTodos();
    }

    public List<Producto> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return repository.listarTodos();
        }
        return repository.buscar(filtro);
    }

    public void guardar(Producto p, String precioTexto, String stockTexto, boolean esNuevo) throws SQLException {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (p.getCategoria() == null || p.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }

        BigDecimal precio;
        int stock;
        try {
            precio = new BigDecimal(precioTexto.trim());
            stock = Integer.parseInt(stockTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese precio y stock válidos.");
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        p.setNombre(p.getNombre().trim());
        p.setCategoria(p.getCategoria().trim());
        p.setPrecio(precio);
        p.setStock(stock);

        if (esNuevo) {
            repository.insertar(p);
        } else {
            repository.actualizar(p);
        }
    }

    public void eliminar(int idProducto) throws SQLException {
        if (repository.estaRelacionadoConPedidos(idProducto)) {
            throw new IllegalStateException("Producto relacionado");
        }
        repository.eliminar(idProducto);
    }
}