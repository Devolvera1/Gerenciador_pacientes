package com.example.sistema_gestao_pacientes.Util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PacienteSession {
    private final int id;
    private final StringProperty nome;
    private final StringProperty contato;
    private final StringProperty utente;
    private final StringProperty consulta;

    public PacienteSession(int id, String nome, String contato, String utente, String consulta) {
        this.id = id;
        this.nome = new SimpleStringProperty(nome);
        this.contato = new SimpleStringProperty(contato);
        this.utente = new SimpleStringProperty(utente);
        this.consulta = new SimpleStringProperty(consulta);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public String getContato() {
        return contato.get();
    }

    public StringProperty contatoProperty() {
        return contato;
    }

    public String getUtente() {
        return utente.get();
    }

    public StringProperty utenteProperty() {
        return utente;
    }

    public String getConsulta() {
        return consulta.get();
    }

    public StringProperty consultaProperty() {
        return consulta;
    }

    // Este método define o texto exibido no ComboBox do JavaFX
    @Override
    public String toString() {
        String nomePaciente = getNome() != null ? getNome() : "";
        String numUtente = (getUtente() != null && !getUtente().isBlank()) ? " (Utente: " + getUtente() + ")" : "";
        return nomePaciente + numUtente;
    }
}