package com.example.sistema_gestao_pacientes.Util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class LinhaAgenda {
    private SimpleStringProperty horaEixoY;
    private SimpleObjectProperty<ConsultaSlot> segunda;
    private SimpleObjectProperty<ConsultaSlot> terca;
    private SimpleObjectProperty<ConsultaSlot> quarta;
    private SimpleObjectProperty<ConsultaSlot> quinta;
    private SimpleObjectProperty<ConsultaSlot> sexta;

    public LinhaAgenda(String hora) {
        this.horaEixoY = new SimpleStringProperty(hora);
        this.segunda = new SimpleObjectProperty<>(new ConsultaSlot("", "", "VAZIO"));
        this.terca = new SimpleObjectProperty<>(new ConsultaSlot("", "", "VAZIO"));
        this.quarta = new SimpleObjectProperty<>(new ConsultaSlot("", "", "VAZIO"));
        this.quinta = new SimpleObjectProperty<>(new ConsultaSlot("", "", "VAZIO"));
        this.sexta = new SimpleObjectProperty<>(new ConsultaSlot("", "", "VAZIO"));
    }

    public String getHoraEixoY() { return horaEixoY.get(); }
    public SimpleStringProperty horaEixoYProperty() { return horaEixoY; }

    public SimpleObjectProperty<ConsultaSlot> segundaProperty() { return segunda; }
    public SimpleObjectProperty<ConsultaSlot> tercaProperty() { return terca; }
    public SimpleObjectProperty<ConsultaSlot> quartaProperty() { return quarta; }
    public SimpleObjectProperty<ConsultaSlot> quintaProperty() { return quinta; }
    public SimpleObjectProperty<ConsultaSlot> sextaProperty() { return sexta; }

    public void setSegunda(ConsultaSlot slot) { this.segunda.set(slot); }
    public void setTerca(ConsultaSlot slot) { this.terca.set(slot); }
    public void setQuarta(ConsultaSlot slot) { this.quarta.set(slot); }
    public void setQuinta(ConsultaSlot slot) { this.quinta.set(slot); }
    public void setSexta(ConsultaSlot slot) { this.sexta.set(slot); }
}