package com.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Bilheteria;
import com.example.model.Cliente;
import com.example.model.FormaPagamento;
import com.example.model.Ingresso;
import com.example.util.ConnectionFactory;

public class IngressoDAO {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final BilheteriaDAO bilheteriaDAO = new BilheteriaDAO();

    public Ingresso salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingresso (id_cliente, id_bilheteria, data_venda, pagamento) " +
                "VALUES (?, ?, ?, ?::forma_pagamento) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ingresso.getIdCliente());
            stmt.setInt(2, ingresso.getIdBilheteria());
            stmt.setDate(3, Date.valueOf(ingresso.getDataVenda()));
            stmt.setString(4, ingresso.getPagamento().getValor());

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ingresso.setId(rs.getInt(1));
                }
            }
            
            return ingresso;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ingresso", e);
        }
    }
    
    public Ingresso buscarPorId(int id) {
        String sql = "SELECT * FROM ingresso WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearIngresso(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingresso por ID", e);
        }
    }
    
    public List<Ingresso> listarPorCliente(int idCliente) {
        String sql = "SELECT * FROM ingresso WHERE id_cliente = ? ORDER BY data_venda DESC";
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(mapearIngresso(rs));
                }
            }
            
            return ingressos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos por cliente", e);
        }
    }
    
    public List<Ingresso> listarPorData(LocalDate data) {
        String sql = "SELECT * FROM ingresso WHERE data_venda = ? ORDER BY id";
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(data));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(mapearIngresso(rs));
                }
            }
            
            return ingressos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos por data", e);
        }
    }
    
    public boolean verificarIngressoDisponivel(int idIngresso) {
        String sql = "SELECT COUNT(*) FROM atracao_cliente WHERE id_ingresso = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idIngresso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se count for 0, o ingresso não está sendo usado em nenhuma atração
                    return rs.getInt(1) == 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar disponibilidade do ingresso", e);
        }
    }
    
    private Ingresso mapearIngresso(ResultSet rs) throws SQLException {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(rs.getInt("id"));
        ingresso.setIdCliente(rs.getInt("id_cliente"));
        ingresso.setIdBilheteria(rs.getInt("id_bilheteria"));
        ingresso.setDataVenda(rs.getDate("data_venda").toLocalDate());
        
        String pagamentoStr = rs.getString("pagamento");
        FormaPagamento formaPagamento = switch (pagamentoStr) {
            case "pix" -> FormaPagamento.PIX;
            case "credito" -> FormaPagamento.CREDITO;
            case "debito" -> FormaPagamento.DEBITO;
            case "dinheiro" -> FormaPagamento.DINHEIRO;
            default -> FormaPagamento.PIX;
        };
        ingresso.setPagamento(formaPagamento);
        
        // Carrega os objetos relacionados
        Cliente cliente = clienteDAO.buscarPorId(ingresso.getIdCliente());
        Bilheteria bilheteria = bilheteriaDAO.buscarPorId(ingresso.getIdBilheteria());
        
        ingresso.setCliente(cliente);
        ingresso.setBilheteria(bilheteria);
        
        return ingresso;
    }
} 