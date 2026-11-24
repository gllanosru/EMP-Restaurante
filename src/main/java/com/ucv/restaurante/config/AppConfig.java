package com.ucv.restaurante.config;

import com.ucv.restaurante.controller.*;
import com.ucv.restaurante.repository.*;
import com.ucv.restaurante.service.*;

public class AppConfig {
    private static AppConfig instance;

    private final DatabaseConfig databaseConfig;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final MesaService mesaService;
    private final MeseroService meseroService;
    private final PedidoService pedidoService;
    private final PedidoDetalleService pedidoDetalleService;
    private final FacturaService facturaService;

    private AppConfig() {
        this.databaseConfig = new DatabaseConfig();

        UsuarioRepository usuarioRepository = new UsuarioRepository(databaseConfig);
        ProductoRepository productoRepository = new ProductoRepository(databaseConfig);
        MesaRepository mesaRepository = new MesaRepository(databaseConfig);
        MeseroRepository meseroRepository = new MeseroRepository(databaseConfig);
        PedidoDetalleRepository pedidoDetalleRepository = new PedidoDetalleRepository(databaseConfig);
        PedidoRepository pedidoRepository = new PedidoRepository(databaseConfig, pedidoDetalleRepository, productoRepository, mesaRepository);
        FacturaRepository facturaRepository = new FacturaRepository(databaseConfig);

        this.usuarioService = new UsuarioService(usuarioRepository);
        this.productoService = new ProductoService(productoRepository);
        this.mesaService = new MesaService(mesaRepository);
        this.meseroService = new MeseroService(meseroRepository);
        this.pedidoDetalleService = new PedidoDetalleService(pedidoDetalleRepository);
        this.pedidoService = new PedidoService(pedidoRepository, productoRepository, mesaRepository, meseroRepository);
        this.facturaService = new FacturaService(facturaRepository, pedidoRepository);
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public Object getController(Class<?> type) {
        if (type == LoginController.class) {
            return new LoginController(usuarioService, this);
        }
        if (type == MenuController.class) {
            return new MenuController(this);
        }
        if (type == ProductoController.class) {
            return new ProductoController(productoService);
        }
        if (type == ProductoFormController.class) {
            return new ProductoFormController(productoService);
        }
        if (type == MesaController.class) {
            return new MesaController(mesaService);
        }
        if (type == MesaFormController.class) {
            return new MesaFormController(mesaService);
        }
        if (type == MeseroController.class) {
            return new MeseroController(meseroService);
        }
        if (type == MeseroFormController.class) {
            return new MeseroFormController(meseroService);
        }
        if (type == PedidoController.class) {
            return new PedidoController(pedidoService, pedidoDetalleService);
        }
        if (type == PedidoFormController.class) {
            return new PedidoFormController(pedidoService, pedidoDetalleService, productoService);
        }
        if (type == FacturaController.class) {
            return new FacturaController(facturaService);
        }
        if (type == FacturaFormController.class) {
            return new FacturaFormController(facturaService);
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el controlador: " + type.getName(), e);
        }
    }

    public UsuarioService getUsuarioService() { return usuarioService; }
    public ProductoService getProductoService() { return productoService; }
    public MesaService getMesaService() { return mesaService; }
    public MeseroService getMeseroService() { return meseroService; }
    public PedidoService getPedidoService() { return pedidoService; }
    public PedidoDetalleService getPedidoDetalleService() { return pedidoDetalleService; }
    public FacturaService getFacturaService() { return facturaService; }

    public void destroy() {
        databaseConfig.close();
    }
}