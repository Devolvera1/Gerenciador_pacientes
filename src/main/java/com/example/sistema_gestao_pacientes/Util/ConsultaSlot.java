package com.example.sistema_gestao_pacientes.Util;

public class ConsultaSlot {
    private String horario;
    private String titulo;
    private String status;

    public ConsultaSlot(String horario, String titulo, String status) {
        this.horario = horario;
        this.titulo = titulo;
        this.status = status;
    }

    public String getHorario() { return horario; }
    public String getTitulo() { return titulo; }
    public String getStatus() { return status; }
}