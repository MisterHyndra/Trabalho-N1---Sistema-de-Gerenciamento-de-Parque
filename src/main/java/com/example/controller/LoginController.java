package com.example.controller;

import com.example.Main;
import com.example.dao.ClienteDAO;
import com.example.model.Cliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField cpfField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button cadastrarButton;
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    
    @FXML
    public void initialize() {
        // Garante que a tabela cliente existe
        clienteDAO.criarTabela();
    }
    
    @FXML
    private void login() {
        String cpf = cpfField.getText().trim();
        
        if (cpf.isEmpty()) {
            mostrarAlerta("Erro", "Por favor, insira o CPF");
            return;
        }
        
        try {
            Cliente cliente = clienteDAO.buscarPorCpf(cpf);
            
            if (cliente == null) {
                mostrarAlerta("Não encontrado", "Cliente não encontrado. Por favor, cadastre-se primeiro.");
                return;
            }
            
            // Cliente encontrado, redireciona para a tela principal
            abrirTelaPrincipal(cliente);
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao fazer login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void abrirTelaCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/view/CadastroCliente.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Cadastro de Cliente");
            stage.setScene(new Scene(root));
            
            CadastroClienteController controller = loader.getController();
            controller.setLoginController(this);
            
            stage.showAndWait();
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao abrir tela de cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void atualizarClienteLogado(Cliente cliente) {
        cpfField.setText(cliente.getCpf());
    }
    
    private void abrirTelaPrincipal(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/view/TelaPrincipal.fxml"));
            Parent root = loader.load();
            
            TelaPrincipalController controller = loader.getController();
            controller.setCliente(cliente);
            
            Stage currentStage = (Stage) cpfField.getScene().getWindow();
            currentStage.setTitle("Sistema de Gerenciamento de Parque - " + cliente.getNome());
            currentStage.setScene(new Scene(root));
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao abrir tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 