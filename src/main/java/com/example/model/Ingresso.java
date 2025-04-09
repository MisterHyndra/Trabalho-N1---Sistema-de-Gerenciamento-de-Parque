package com.example.model;

import java.time.LocalDate;

public class Ingresso {
    private int id;
    private int idCliente;
    private int idBilheteria;
    private LocalDate dataVenda;
    private FormaPagamento pagamento;
    
    // Variáveis para facilitar exibição na interface
    private Cliente cliente;
    private Bilheteria bilheteria;
    
    public Ingresso() {
    }
    
    public Ingresso(int id, int idCliente, int idBilheteria, LocalDate dataVenda, FormaPagamento pagamento) {
        this.id = id;
        this.idCliente = idCliente;
        this.idBilheteria = idBilheteria;
        this.dataVenda = dataVenda;
        this.pagamento = pagamento;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    
    public int getIdBilheteria() {
        return idBilheteria;
    }
    
    public void setIdBilheteria(int idBilheteria) {
        this.idBilheteria = idBilheteria;
    }
    
    public LocalDate getDataVenda() {
        return dataVenda;
    }
    
    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }
    
    public FormaPagamento getPagamento() {
        return pagamento;
    }
    
    public void setPagamento(FormaPagamento pagamento) {
        this.pagamento = pagamento;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.idCliente = cliente.getId();
    }
    
    public Bilheteria getBilheteria() {
        return bilheteria;
    }
    
    public void setBilheteria(Bilheteria bilheteria) {
        this.bilheteria = bilheteria;
        this.idBilheteria = bilheteria.getId();
    }
    
    @Override
    public String toString() {
        return "Ingresso #" + id + " - Cliente: " + (cliente != null ? cliente.getNome() : "ID " + idCliente);
    }
} 