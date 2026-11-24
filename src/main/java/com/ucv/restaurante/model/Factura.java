package com.ucv.restaurante.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Factura {
    private int idFactura;
    private int idPedido;
    private String tipoComprobante;
    private String cliente;
    private String documento;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fecha;

    public Factura() {
        this.tipoComprobante = "Factura";
    }

    public Factura(int idFactura, int idPedido, String cliente, String documento,
                   BigDecimal total, String estado, LocalDateTime fecha) {
        this.idFactura = idFactura;
        this.idPedido = idPedido;
        this.tipoComprobante = "Factura";
        this.cliente = cliente;
        this.documento = documento;
        this.total = total;
        this.estado = estado;
        this.fecha = fecha;
    }

    public Factura(int idFactura, int idPedido, String tipoComprobante, String cliente, String documento,
                   BigDecimal total, String estado, LocalDateTime fecha) {
        this.idFactura = idFactura;
        this.idPedido = idPedido;
        setTipoComprobante(tipoComprobante);
        this.cliente = cliente;
        this.documento = documento;
        this.total = total;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public String getTipoComprobante() {
        return (tipoComprobante == null || tipoComprobante.isBlank()) ? "Factura" : tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = (tipoComprobante == null || tipoComprobante.isBlank())
                ? "Factura"
                : tipoComprobante.trim();
    }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}