package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Bilheteria {
    private int id;
    private double preco;
    private LocalTime horarioFechamento;
    private LocalDate funcionamento;
    private int quantidadeDisponivel;
    
    public Bilheteria() {
    }
    
    public Bilheteria(int id, double preco, LocalTime horarioFechamento, LocalDate funcionamento, int quantidadeDisponivel) {
        this.id = id;
        this.preco = preco;
        this.horarioFechamento = horarioFechamento;
        this.funcionamento = funcionamento;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getPreco() {
        return preco;
    }
    
    public void setPreco(double preco) {
        this.preco = preco;
    }
    
    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }
    
    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }
    
    public LocalDate getFuncionamento() {
        return funcionamento;
    }
    
    public void setFuncionamento(LocalDate funcionamento) {
        this.funcionamento = funcionamento;
    }
    
    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
    
    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
    
    @Override
    public String toString() {
        return "Bilheteria #" + id + " - R$ " + preco + " - Disponíveis: " + quantidadeDisponivel;
    }
} 