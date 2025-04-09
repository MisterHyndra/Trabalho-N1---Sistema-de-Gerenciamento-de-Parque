package com.example.controller;

import com.example.dao.ClienteDAO;
import com.example.model.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroClienteController {

    @FXML
    private TextField nomeField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telefoneField;
    
    @FXML
    private TextField cpfField;
    
    @FXML
    private Button salvarButton;
    
    @FXML
    private Button cancelarButton;
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private LoginController loginController;
    
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
    
    @FXML
    private void salvar() {
        // Validação de campos obrigatórios
        if (nomeField.getText().trim().isEmpty() || cpfField.getText().trim().isEmpty()) {
            mostrarAlerta("Erro", "Nome e CPF são campos obrigatórios.");
            return;
        }
        
        try {
            // Verifica se já existe um cliente com o CPF informado
            Cliente clienteExistente = clienteDAO.buscarPorCpf(cpfField.getText().trim());
            if (clienteExistente != null) {
                mostrarAlerta("Erro", "CPF já cadastrado.");
                return;
            }
            
            // Cria um novo cliente
            Cliente cliente = new Cliente();
            cliente.setNome(nomeField.getText().trim());
            cliente.setEmail(emailField.getText().trim());
            cliente.setTelefone(telefoneField.getText().trim());
            cliente.setCpf(cpfField.getText().trim());
            
            // Salva o cliente
            cliente = clienteDAO.salvar(cliente);
            
            // Atualiza o cliente na tela de login
            if (loginController != null) {
                loginController.atualizarClienteLogado(cliente);
            }
            
            mostrarAlerta("Sucesso", "Cliente cadastrado com sucesso!");
            
            // Fecha a janela
            Stage stage = (Stage) salvarButton.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void cancelar() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 