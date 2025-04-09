package com.example.model;

import java.time.LocalTime;

public class Atracao {
    private int id;
    private String nome;
    private String descricao;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private int capacidade;
    
    public Atracao() {
    }
    
    public Atracao(int id, String nome, String descricao, LocalTime horarioInicio, LocalTime horarioFim, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.capacidade = capacidade;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }
    
    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }
    
    public LocalTime getHorarioFim() {
        return horarioFim;
    }
    
    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }
    
    public int getCapacidade() {
        return capacidade;
    }
    
    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
    
    @Override
    public String toString() {
        return nome + " (" + horarioInicio + " - " + horarioFim + ")";
    }
} 