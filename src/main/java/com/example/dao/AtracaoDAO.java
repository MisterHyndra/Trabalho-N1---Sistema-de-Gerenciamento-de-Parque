package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Atracao;
import com.example.util.ConnectionFactory;

public class AtracaoDAO {

    public Atracao salvar(Atracao atracao) {
        String sql = "INSERT INTO atracao (nome, descricao, horario_inicio, horario_fim, capacidade) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, atracao.getNome());
            stmt.setString(2, atracao.getDescricao());
            stmt.setTime(3, Time.valueOf(atracao.getHorarioInicio()));
            stmt.setTime(4, Time.valueOf(atracao.getHorarioFim()));
            stmt.setInt(5, atracao.getCapacidade());

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    atracao.setId(rs.getInt(1));
                }
            }
            
            return atracao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar atração", e);
        }
    }
    
    public Atracao buscarPorId(int id) {
        String sql = "SELECT * FROM atracao WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAtracao(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atração por ID", e);
        }
    }
    
    public List<Atracao> listarTodas() {
        String sql = "SELECT * FROM atracao ORDER BY horario_inicio";
        List<Atracao> atracoes = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                atracoes.add(mapearAtracao(rs));
            }
            
            return atracoes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar atrações", e);
        }
    }
    
    /**
     * Verifica se a atração tem capacidade disponível
     * @param idAtracao ID da atração
     * @return true se há capacidade disponível, false caso contrário
     */
    public boolean verificarCapacidadeDisponivel(int idAtracao) {
        String sql = """
                SELECT a.capacidade > COUNT(ac.id) AS disponivel 
                FROM atracao a 
                LEFT JOIN atracao_cliente ac ON a.id = ac.id_atracao
                WHERE a.id = ?
                GROUP BY a.capacidade
                """;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAtracao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("disponivel");
                }
                // Se não retornou resultado, assume que está disponível (ainda não há registros)
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar capacidade da atração", e);
        }
    }
    
    /**
     * Conta quantos clientes já estão usando a atração
     * @param idAtracao ID da atração
     * @return Número de clientes usando a atração
     */
    public int contarClientesNaAtracao(int idAtracao) {
        String sql = "SELECT COUNT(*) FROM atracao_cliente WHERE id_atracao = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAtracao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar clientes na atração", e);
        }
    }
    
    private Atracao mapearAtracao(ResultSet rs) throws SQLException {
        Atracao atracao = new Atracao();
        atracao.setId(rs.getInt("id"));
        atracao.setNome(rs.getString("nome"));
        atracao.setDescricao(rs.getString("descricao"));
        atracao.setHorarioInicio(rs.getTime("horario_inicio").toLocalTime());
        atracao.setHorarioFim(rs.getTime("horario_fim").toLocalTime());
        atracao.setCapacidade(rs.getInt("capacidade"));
        return atracao;
    }
} 