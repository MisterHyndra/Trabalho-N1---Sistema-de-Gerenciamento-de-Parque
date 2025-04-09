package com.example.controller;

import java.time.LocalDate;

import com.example.dao.BilheteriaDAO;
import com.example.dao.IngressoDAO;
import com.example.model.Bilheteria;
import com.example.model.Cliente;
import com.example.model.FormaPagamento;
import com.example.model.Ingresso;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class BilheteriaController {

    @FXML
    private DatePicker dataPicker;
    
    @FXML
    private ListView<Bilheteria> bilheteriasListView;
    
    @FXML
    private Label precoLabel;
    
    @FXML
    private Label horarioFechamentoLabel;
    
    @FXML
    private Label disponiveisLabel;
    
    @FXML
    private ComboBox<FormaPagamento> formaPagamentoComboBox;
    
    private final BilheteriaDAO bilheteriaDAO = new BilheteriaDAO();
    private final IngressoDAO ingressoDAO = new IngressoDAO();
    private Cliente cliente;
    private ObservableList<Bilheteria> bilheterias = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Inicializa o datePicker com a data atual
        dataPicker.setValue(LocalDate.now());
        
        // Preenche as formas de pagamento
        formaPagamentoComboBox.getItems().addAll(FormaPagamento.values());
        formaPagamentoComboBox.setValue(FormaPagamento.PIX);
        
        // Configura o listener para seleção de bilheteria
        bilheteriasListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> exibirDetalhesBilheteria(newValue));
        
        // Configura o listener para mudança de data
        dataPicker.valueProperty().addListener((observable, oldValue, newValue) -> carregarBilheterias());
        
        // Carrega as bilheterias para a data atual
        carregarBilheterias();
    }
    
    public void atualizarTela() {
        carregarBilheterias();
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    private void carregarBilheterias() {
        try {
            bilheterias.clear();
            
            LocalDate data = dataPicker.getValue();
            if (data == null) {
                data = LocalDate.now();
            }
            
            bilheterias.addAll(bilheteriaDAO.listarPorData(data));
            bilheteriasListView.setItems(bilheterias);
            
            if (!bilheterias.isEmpty()) {
                bilheteriasListView.getSelectionModel().selectFirst();
            } else {
                limparDetalhesBilheteria();
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao carregar bilheterias: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void exibirDetalhesBilheteria(Bilheteria bilheteria) {
        if (bilheteria != null) {
            precoLabel.setText(String.format("R$ %.2f", bilheteria.getPreco()));
            horarioFechamentoLabel.setText(bilheteria.getHorarioFechamento().toString());
            disponiveisLabel.setText(String.valueOf(bilheteria.getQuantidadeDisponivel()));
        } else {
            limparDetalhesBilheteria();
        }
    }
    
    private void limparDetalhesBilheteria() {
        precoLabel.setText("-");
        horarioFechamentoLabel.setText("-");
        disponiveisLabel.setText("-");
    }
    
    @FXML
    private void comprarIngresso() {
        if (cliente == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Você precisa estar logado para comprar um ingresso.");
            return;
        }
        
        Bilheteria bilheteria = bilheteriasListView.getSelectionModel().getSelectedItem();
        if (bilheteria == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Selecione uma bilheteria para comprar o ingresso.");
            return;
        }
        
        if (bilheteria.getQuantidadeDisponivel() <= 0) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Não há ingressos disponíveis para esta bilheteria.");
            return;
        }
        
        FormaPagamento formaPagamento = formaPagamentoComboBox.getValue();
        if (formaPagamento == null) {
            mostrarAlerta(AlertType.WARNING, "Aviso", "Selecione uma forma de pagamento.");
            return;
        }
        
        try {
            // Cria o ingresso
            Ingresso ingresso = new Ingresso();
            ingresso.setIdCliente(cliente.getId());
            ingresso.setIdBilheteria(bilheteria.getId());
            ingresso.setDataVenda(LocalDate.now());
            ingresso.setPagamento(formaPagamento);
            
            // Salva o ingresso (a trigger do banco irá decrementar a quantidade disponível)
            ingressoDAO.salvar(ingresso);
            
            // Atualiza a lista de bilheterias
            carregarBilheterias();
            
            mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Ingresso comprado com sucesso!");
            
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao comprar ingresso: " + e.getMessage());
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