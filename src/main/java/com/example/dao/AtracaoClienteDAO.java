package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Atracao;
import com.example.model.AtracaoCliente;
import com.example.model.Ingresso;
import com.example.util.ConnectionFactory;

public class AtracaoClienteDAO {

    private final AtracaoDAO atracaoDAO = new AtracaoDAO();
    private final IngressoDAO ingressoDAO = new IngressoDAO();

    public AtracaoCliente registrarParticipacao(AtracaoCliente atracaoCliente) {
        String sql = "INSERT INTO atracao_cliente (id_atracao, id_ingresso, horario_uso) " +
                "VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, atracaoCliente.getIdAtracao());
            stmt.setInt(2, atracaoCliente.getIdIngresso());
            stmt.setTimestamp(3, Timestamp.valueOf(atracaoCliente.getHorarioUso()));

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    atracaoCliente.setId(rs.getInt(1));
                }
            }
            
            return atracaoCliente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar participação em atração", e);
        }
    }
    
    public AtracaoCliente buscarPorId(int id) {
        String sql = "SELECT * FROM atracao_cliente WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAtracaoCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar participação por ID", e);
        }
    }
    
    public AtracaoCliente buscarPorIngresso(int idIngresso) {
        String sql = "SELECT * FROM atracao_cliente WHERE id_ingresso = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idIngresso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAtracaoCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar participação por ingresso", e);
        }
    }
    
    public List<AtracaoCliente> listarPorAtracao(int idAtracao) {
        String sql = "SELECT * FROM atracao_cliente WHERE id_atracao = ? ORDER BY horario_uso DESC";
        List<AtracaoCliente> participacoes = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAtracao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participacoes.add(mapearAtracaoCliente(rs));
                }
            }
            
            return participacoes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar participações por atração", e);
        }
    }
    
    private AtracaoCliente mapearAtracaoCliente(ResultSet rs) throws SQLException {
        AtracaoCliente atracaoCliente = new AtracaoCliente();
        atracaoCliente.setId(rs.getInt("id"));
        atracaoCliente.setIdAtracao(rs.getInt("id_atracao"));
        atracaoCliente.setIdIngresso(rs.getInt("id_ingresso"));
        atracaoCliente.setHorarioUso(rs.getTimestamp("horario_uso").toLocalDateTime());
        
        // Carrega os objetos relacionados
        Atracao atracao = atracaoDAO.buscarPorId(atracaoCliente.getIdAtracao());
        Ingresso ingresso = ingressoDAO.buscarPorId(atracaoCliente.getIdIngresso());
        
        atracaoCliente.setAtracao(atracao);
        atracaoCliente.setIngresso(ingresso);
        
        return atracaoCliente;
    }
} 