package com.example.model;

import java.time.LocalDateTime;

public class AtracaoCliente {
    private int id;
    private int idAtracao;
    private int idIngresso;
    private LocalDateTime horarioUso;
    
    // Variáveis para facilitar exibição na interface
    private Atracao atracao;
    private Ingresso ingresso;
    
    public AtracaoCliente() {
    }
    
    public AtracaoCliente(int id, int idAtracao, int idIngresso, LocalDateTime horarioUso) {
        this.id = id;
        this.idAtracao = idAtracao;
        this.idIngresso = idIngresso;
        this.horarioUso = horarioUso;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdAtracao() {
        return idAtracao;
    }
    
    public void setIdAtracao(int idAtracao) {
        this.idAtracao = idAtracao;
    }
    
    public int getIdIngresso() {
        return idIngresso;
    }
    
    public void setIdIngresso(int idIngresso) {
        this.idIngresso = idIngresso;
    }
    
    public LocalDateTime getHorarioUso() {
        return horarioUso;
    }
    
    public void setHorarioUso(LocalDateTime horarioUso) {
        this.horarioUso = horarioUso;
    }
    
    public Atracao getAtracao() {
        return atracao;
    }
    
    public void setAtracao(Atracao atracao) {
        this.atracao = atracao;
        this.idAtracao = atracao.getId();
    }
    
    public Ingresso getIngresso() {
        return ingresso;
    }
    
    public void setIngresso(Ingresso ingresso) {
        this.ingresso = ingresso;
        this.idIngresso = ingresso.getId();
    }
    
    @Override
    public String toString() {
        return "Uso da atração: " + (atracao != null ? atracao.getNome() : "ID " + idAtracao) + 
               " - Ingresso: " + idIngresso + " - Horário: " + horarioUso;
    }
} 