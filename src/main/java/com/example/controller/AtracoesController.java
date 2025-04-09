package com.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.example.dao.AtracaoClienteDAO;
import com.example.dao.AtracaoDAO;
import com.example.dao.IngressoDAO;
import com.example.model.Atracao;
import com.example.model.AtracaoCliente;
import com.example.model.Cliente;
import com.example.model.Ingresso;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class AtracoesController {

    @FXML
    private ListView<Atracao> atracoesListView;
    
    @FXML
    private Label nomeAtracaoLabel;
    
    @FXML
    private Label horarioLabel;
    
    @FXML
    private Label capacidadeLabel;
    
    @FXML
    private Label ocupacaoLabel;
    
    @FXML
    private TextArea descricaoTextArea;
    
    @FXML
    private ComboBox<Ingresso> ingressoComboBox;
    
    private final AtracaoDAO atracaoDAO = new AtracaoDAO();
    private final IngressoDAO ingressoDAO = new IngressoDAO();
    private final AtracaoClienteDAO atracaoClienteDAO = new AtracaoClienteDAO();
    
    private Cliente cliente;
    private ObservableList<Atracao> atracoes = FXCollections.observableArrayList();
    private ObservableList<Ingresso> ingressos = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Configura o listener para seleção de atração
        atracoesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> exibirDetalhesAtracao(newValue));
        
        // Carrega as atrações
        carregarAtracoes();
    }
    
    public void atualizarTela() {
        carregarAtracoes();
        carregarIngressosDisponiveis();
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        carregarIngressosDisponiveis();
    }
    
    private void carregarAtracoes() {
        try {
            atracoes.clear();
            atracoes.addAll(atracaoDAO.listarTodas());
            atracoesListView.setItems(atracoes);
            
            if (!atracoes.isEmpty()) {
                atracoesListView.getSelectionModel().selectFirst();
            } else {
                limparDetalhesAtracao();
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao carregar atrações: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarIngressosDisponiveis() {
        if (cliente == null) {
            ingressos.clear();
            ingressoComboBox.setItems(ingressos);
            return;
        }
        
        try {
            ingressos.clear();
            
            // Busca os ingressos do cliente
            List<Ingresso> ingressosCliente = ingressoDAO.listarPorCliente(cliente.getId());
            
            // Filtra apenas os ingressos que estão disponíveis (não usados em atrações)
            for (Ingresso ingresso : ingressosCliente) {
                if (ingressoDAO.verificarIngressoDisponivel(ingresso.getId())) {
                    ingressos.add(ingresso);
                }
            }
            
            ingressoComboBox.setItems(ingressos);
            
            if (!ingressos.isEmpty()) {
                ingressoComboBox.getSelectionModel().selectFirst();
            } else {
                ingressoComboBox.setValue(null);
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao carregar ingressos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void exibirDetalhesAtracao(Atracao atracao) {
        if (atracao != null) {
            nomeAtracaoLabel.setText(atracao.getNome());
            horarioLabel.setText(atracao.getHorarioInicio() + " - " + atracao.getHorarioFim());
            descricaoTextArea.setText(atracao.getDescricao());
            
            int capacidade = atracao.getCapacidade();
            int ocupacao = atracaoDAO.contarClientesNaAtracao(atracao.getId());
            
            capacidadeLabel.setText(String.valueOf(capacidade));
            ocupacaoLabel.setText(ocupacao + " / " + capacidade);
        } else {
            limparDetalhesAtracao();
        }
    }
    
    private void limparDetalhesAtracao() {
        nomeAtracaoLabel.setText("-");
        horarioLabel.setText("-");
        capacidadeLabel.setText("-");
        ocupacaoLabel.setText("-");
        descricaoTextArea.setText("");
    }
    
    @FXML
    private void participarAtracao() {
        if (cliente == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Você precisa estar logado para participar de uma atração.");
            return;
        }
        
        Atracao atracao = atracoesListView.getSelectionModel().getSelectedItem();
        if (atracao == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Selecione uma atração para participar.");
            return;
        }
        
        // Verifica se há capacidade disponível
        if (!atracaoDAO.verificarCapacidadeDisponivel(atracao.getId())) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Esta atração está com capacidade máxima!");
            return;
        }
        
        Ingresso ingresso = ingressoComboBox.getValue();
        if (ingresso == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Selecione um ingresso válido.");
            return;
        }
        
        try {
            // Verifica se o ingresso já foi usado
            if (!ingressoDAO.verificarIngressoDisponivel(ingresso.getId())) {
                mostrarAlerta(AlertType.WARNING, "Aviso", "Este ingresso já foi utilizado em outra atração!");
                return;
            }
            
            // Registra a participação
            AtracaoCliente atracaoCliente = new AtracaoCliente();
            atracaoCliente.setIdAtracao(atracao.getId());
            atracaoCliente.setIdIngresso(ingresso.getId());
            atracaoCliente.setHorarioUso(LocalDateTime.now());
            
            atracaoClienteDAO.registrarParticipacao(atracaoCliente);
            
            // Atualiza as informações
            exibirDetalhesAtracao(atracao);
            carregarIngressosDisponiveis();
            
            mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Participação registrada com sucesso!");
            
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao registrar participação: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 