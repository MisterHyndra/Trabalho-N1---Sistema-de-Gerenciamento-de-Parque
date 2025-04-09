package com.example.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.dao.AtracaoClienteDAO;
import com.example.dao.IngressoDAO;
import com.example.model.AtracaoCliente;
import com.example.model.Cliente;
import com.example.model.Ingresso;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MeusIngressosController {

    @FXML
    private ListView<Ingresso> ingressosListView;
    
    @FXML
    private Label bilheteriaLabel;
    
    @FXML
    private Label dataCompraLabel;
    
    @FXML
    private Label precoLabel;
    
    @FXML
    private Label formaPagamentoLabel;
    
    @FXML
    private Label statusIngressoLabel;
    
    @FXML
    private TableView<AtracaoClienteVO> atracoesTableView;
    
    @FXML
    private TableColumn<AtracaoClienteVO, String> atracaoColumn;
    
    @FXML
    private TableColumn<AtracaoClienteVO, String> dataHoraColumn;
    
    private final IngressoDAO ingressoDAO = new IngressoDAO();
    private final AtracaoClienteDAO atracaoClienteDAO = new AtracaoClienteDAO();
    
    private Cliente cliente;
    private ObservableList<Ingresso> ingressos = FXCollections.observableArrayList();
    private ObservableList<AtracaoClienteVO> atracoes = FXCollections.observableArrayList();
    
    // Classe auxiliar para exibir informações de atrações na tabela
    public static class AtracaoClienteVO {
        private String nomeAtracao;
        private String dataHora;
        
        public AtracaoClienteVO(String nomeAtracao, String dataHora) {
            this.nomeAtracao = nomeAtracao;
            this.dataHora = dataHora;
        }
        
        public String getNomeAtracao() {
            return nomeAtracao;
        }
        
        public String getDataHora() {
            return dataHora;
        }
    }
    
    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        atracaoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeAtracao"));
        dataHoraColumn.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        
        // Configura o listener para seleção de ingresso
        ingressosListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> exibirDetalhesIngresso(newValue));
    }
    
    public void atualizarTela() {
        carregarIngressos();
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        carregarIngressos();
    }
    
    private void carregarIngressos() {
        if (cliente == null) {
            return;
        }
        
        try {
            ingressos.clear();
            ingressos.addAll(ingressoDAO.listarPorCliente(cliente.getId()));
            ingressosListView.setItems(ingressos);
            
            if (!ingressos.isEmpty()) {
                ingressosListView.getSelectionModel().selectFirst();
            } else {
                limparDetalhesIngresso();
            }
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao carregar ingressos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void exibirDetalhesIngresso(Ingresso ingresso) {
        if (ingresso == null) {
            limparDetalhesIngresso();
            return;
        }
        
        try {
            bilheteriaLabel.setText("Bilheteria #" + ingresso.getIdBilheteria());
            
            LocalDate dataCompra = ingresso.getDataVenda();
            dataCompraLabel.setText(dataCompra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            if (ingresso.getBilheteria() != null) {
                precoLabel.setText(String.format("R$ %.2f", ingresso.getBilheteria().getPreco()));
            } else {
                precoLabel.setText("-");
            }
            
            formaPagamentoLabel.setText(ingresso.getPagamento().toString());
            
            // Verifica se o ingresso já foi usado
            boolean ingressoDisponivel = ingressoDAO.verificarIngressoDisponivel(ingresso.getId());
            statusIngressoLabel.setText(ingressoDisponivel ? "Disponível" : "Utilizado");
            
            // Carrega as atrações utilizadas com este ingresso
            carregarAtracoes(ingresso.getId());
            
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao exibir detalhes do ingresso: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void carregarAtracoes(int idIngresso) {
        try {
            atracoes.clear();
            
            AtracaoCliente atracaoCliente = atracaoClienteDAO.buscarPorIngresso(idIngresso);
            if (atracaoCliente != null) {
                String nomeAtracao = atracaoCliente.getAtracao() != null 
                        ? atracaoCliente.getAtracao().getNome() 
                        : "Atração #" + atracaoCliente.getIdAtracao();
                
                LocalDateTime dataHora = atracaoCliente.getHorarioUso();
                String dataHoraFormatada = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                
                atracoes.add(new AtracaoClienteVO(nomeAtracao, dataHoraFormatada));
            }
            
            atracoesTableView.setItems(atracoes);
            
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Erro", "Erro ao carregar atrações: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void limparDetalhesIngresso() {
        bilheteriaLabel.setText("-");
        dataCompraLabel.setText("-");
        precoLabel.setText("-");
        formaPagamentoLabel.setText("-");
        statusIngressoLabel.setText("-");
        atracoes.clear();
        atracoesTableView.setItems(atracoes);
    }
    
    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} 