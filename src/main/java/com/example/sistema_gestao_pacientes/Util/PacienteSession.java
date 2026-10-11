package com.example.sistema_gestao_pacientes.Util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PacienteSession {
    private final StringProperty nome;
    private final StringProperty contato;
    private final StringProperty utente;
    private final StringProperty consulta;
    private final StringProperty acoes; // Para os botões de editar/apagar, se geridos por texto ou personalizados

    public PacienteSession(String nome, String contato, String utente, String consulta, String acoes) {
        this.nome = new SimpleStringProperty(nome);
        this.contato = new SimpleStringProperty(contato);
        this.utente = new SimpleStringProperty(utente);
        this.consulta = new SimpleStringProperty(consulta);
        this.acoes = new SimpleStringProperty(acoes);
    }

    // Getters obrigatórios para o PropertyValueFactory do JavaFX
    public String getNome() { return nome.get(); }
    public StringProperty nomeProperty() { return nome; }

    public String getContato() { return contato.get(); }
    public StringProperty contatoProperty() { return contato; }

    public String getUtente() { return utente.get(); }
    public StringProperty utenteProperty() { return utente; }

    public String getConsulta() { return consulta.get(); }
    public StringProperty consultaProperty() { return consulta; }

    public String getAcoes() { return acoes.get(); }
    public StringProperty acoesProperty() { return acoes; }
}