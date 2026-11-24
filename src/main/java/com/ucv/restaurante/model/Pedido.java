package com.ucv.restaurante.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int idPedido;
    private int idMesa;
    private int numeroMesa;
    private int idMesero;
    private String nombreMesero;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fecha;
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {}

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public int getIdMesa() { return idMesa; }
    public void setIdMesa(int idMesa) { this.idMesa = idMesa; }
    public int getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }
    public int getIdMesero() { return idMesero; }
    public void setIdMesero(int idMesero) { this.idMesero = idMesero; }
    public String getNombreMesero() { return nombreMesero; }
    public void setNombreMesero(String nombreMesero) { this.nombreMesero = nombreMesero; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Pedido #" + idPedido + " - Mesa " + numeroMesa + " (S/ " + total + ")";
    }
}