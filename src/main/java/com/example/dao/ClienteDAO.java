package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Cliente;
import com.example.util.ConnectionFactory;

public class ClienteDAO {

    public Cliente salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, email, telefone, cpf) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getCpf());

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
            
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
    }
    
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por ID", e);
        }
    }
    
    public Cliente buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por CPF", e);
        }
    }
    
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
            return clientes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes", e);
        }
    }
    
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, email = ?, telefone = ?, cpf = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getCpf());
            stmt.setInt(5, cliente.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente", e);
        }
    }
    
    public void excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir cliente", e);
        }
    }
    
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setCpf(rs.getString("cpf"));
        return cliente;
    }
    
    public void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS cliente (
                    id SERIAL PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    telefone VARCHAR(20),
                    cpf VARCHAR(14) UNIQUE NOT NULL
                )
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela cliente", e);
        }
    }
} 