package com.example.sistema_gestao_pacientes.Util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedicoSession {
    private final IntegerProperty id;
    private final StringProperty nome;
    private final StringProperty especialidade;
    private final StringProperty crm;
    private final StringProperty gabinete;
    private final StringProperty estado;

    public MedicoSession() {
        this(0, "", "", "", "", "");
    }

    public MedicoSession(int id, String nome, String especialidade, String crm, String gabinete, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.especialidade = new SimpleStringProperty(especialidade);
        this.crm = new SimpleStringProperty(crm);
        this.gabinete = new SimpleStringProperty(gabinete);
        this.estado = new SimpleStringProperty(estado);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }
    public StringProperty nomeProperty() { return nome; }

    public String getEspecialidade() { return especialidade.get(); }
    public void setEspecialidade(String especialidade) { this.especialidade.set(especialidade); }
    public StringProperty especialidadeProperty() { return especialidade; }

    public String getCrm() { return crm.get(); }
    public void setCrm(String crm) { this.crm.set(crm); }
    public StringProperty crmProperty() { return crm; }

    public String getGabinete() { return gabinete.get(); }
    public void setGabinete(String gabinete) { this.gabinete.set(gabinete); }
    public StringProperty gabineteProperty() { return gabinete; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    @Override
    public String toString() {
        return getNome() != null ? getNome() : "";
    }
}