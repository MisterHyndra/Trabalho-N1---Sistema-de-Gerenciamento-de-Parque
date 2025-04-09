package com.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Bilheteria;
import com.example.util.ConnectionFactory;

public class BilheteriaDAO {

    public Bilheteria salvar(Bilheteria bilheteria) {
        String sql = "INSERT INTO bilheteria (preco, horario_fechamento, funcionamento, quantidade_disponivel) " +
                "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, bilheteria.getPreco());
            stmt.setTime(2, Time.valueOf(bilheteria.getHorarioFechamento()));
            stmt.setDate(3, Date.valueOf(bilheteria.getFuncionamento()));
            stmt.setInt(4, bilheteria.getQuantidadeDisponivel());

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bilheteria.setId(rs.getInt(1));
                }
            }
            
            return bilheteria;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar bilheteria", e);
        }
    }
    
    public Bilheteria buscarPorId(int id) {
        String sql = "SELECT * FROM bilheteria WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearBilheteria(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bilheteria por ID", e);
        }
    }
    
    public List<Bilheteria> listarTodas() {
        String sql = "SELECT * FROM bilheteria WHERE funcionamento >= CURRENT_DATE ORDER BY funcionamento";
        List<Bilheteria> bilheterias = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bilheterias.add(mapearBilheteria(rs));
            }
            
            return bilheterias;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar bilheterias", e);
        }
    }
    
    public List<Bilheteria> listarPorData(LocalDate data) {
        String sql = "SELECT * FROM bilheteria WHERE funcionamento = ? ORDER BY horario_fechamento";
        List<Bilheteria> bilheterias = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(data));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bilheterias.add(mapearBilheteria(rs));
                }
            }
            
            return bilheterias;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar bilheterias por data", e);
        }
    }
    
    public void atualizar(Bilheteria bilheteria) {
        String sql = "UPDATE bilheteria SET preco = ?, horario_fechamento = ?, " +
                "funcionamento = ?, quantidade_disponivel = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, bilheteria.getPreco());
            stmt.setTime(2, Time.valueOf(bilheteria.getHorarioFechamento()));
            stmt.setDate(3, Date.valueOf(bilheteria.getFuncionamento()));
            stmt.setInt(4, bilheteria.getQuantidadeDisponivel());
            stmt.setInt(5, bilheteria.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar bilheteria", e);
        }
    }
    
    private Bilheteria mapearBilheteria(ResultSet rs) throws SQLException {
        Bilheteria bilheteria = new Bilheteria();
        bilheteria.setId(rs.getInt("id"));
        bilheteria.setPreco(rs.getDouble("preco"));
        bilheteria.setHorarioFechamento(rs.getTime("horario_fechamento").toLocalTime());
        bilheteria.setFuncionamento(rs.getDate("funcionamento").toLocalDate());
        bilheteria.setQuantidadeDisponivel(rs.getInt("quantidade_disponivel"));
        return bilheteria;
    }
} 