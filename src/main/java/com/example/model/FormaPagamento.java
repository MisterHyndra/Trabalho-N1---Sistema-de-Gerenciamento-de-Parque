package com.example.model;

public enum FormaPagamento {
    PIX("pix"),
    CREDITO("credito"),
    DEBITO("debito"),
    DINHEIRO("dinheiro");
    
    private final String valor;
    
    FormaPagamento(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    @Override
    public String toString() {
        return switch (this) {
            case PIX -> "PIX";
            case CREDITO -> "Cartão de Crédito";
            case DEBITO -> "Cartão de Débito";
            case DINHEIRO -> "Dinheiro";
        };
    }
} 