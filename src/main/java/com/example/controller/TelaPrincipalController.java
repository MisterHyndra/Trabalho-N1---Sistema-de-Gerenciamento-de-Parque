package com.example.controller;

import com.example.Main;
import com.example.model.Cliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class TelaPrincipalController {

    @FXML
    private Label nomeClienteLabel;
    
    @FXML
    private Label cpfClienteLabel;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private Tab bilheteriaTab;
    
    @FXML
    private Tab atracoesTab;
    
    @FXML
    private Tab meusIngressosTab;
    
    private Cliente cliente;
    private BilheteriaController bilheteriaController;
    private AtracoesController atracoesController;
    private MeusIngressosController meusIngressosController;
    
    @FXML
    public void initialize() {
        try {
            // Carrega a tela de bilheteria
            FXMLLoader bilheteriaLoader = new FXMLLoader(Main.class.getResource("/com/example/view/Bilheteria.fxml"));
            AnchorPane bilheteriaPane = bilheteriaLoader.load();
            bilheteriaTab.setContent(bilheteriaPane);
            bilheteriaController = bilheteriaLoader.getController();
            
            // Carrega a tela de atrações
            FXMLLoader atracoesLoader = new FXMLLoader(Main.class.getResource("/com/example/view/Atracoes.fxml"));
            AnchorPane atracoesPane = atracoesLoader.load();
            atracoesTab.setContent(atracoesPane);
            atracoesController = atracoesLoader.getController();
            
            // Carrega a tela de meus ingressos
            FXMLLoader ingressosLoader = new FXMLLoader(Main.class.getResource("/com/example/view/MeusIngressos.fxml"));
            AnchorPane ingressosPane = ingressosLoader.load();
            meusIngressosTab.setContent(ingressosPane);
            meusIngressosController = ingressosLoader.getController();
            
            // Configura o listener para a mudança de tabs
            tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == bilheteriaTab) {
                    bilheteriaController.atualizarTela();
                } else if (newValue == atracoesTab) {
                    atracoesController.atualizarTela();
                } else if (newValue == meusIngressosTab) {
                    meusIngressosController.atualizarTela();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        nomeClienteLabel.setText(cliente.getNome());
        cpfClienteLabel.setText("CPF: " + cliente.getCpf());
        
        // Atualiza os controladores
        if (bilheteriaController != null) {
            bilheteriaController.setCliente(cliente);
            bilheteriaController.atualizarTela();
        }
        
        if (atracoesController != null) {
            atracoesController.setCliente(cliente);
            atracoesController.atualizarTela();
        }
        
        if (meusIngressosController != null) {
            meusIngressosController.setCliente(cliente);
            meusIngressosController.atualizarTela();
        }
    }
} 