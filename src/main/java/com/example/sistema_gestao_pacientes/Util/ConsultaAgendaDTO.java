package com.example.sistema_gestao_pacientes.Util;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultaAgendaDTO {
    private LocalDate data;
    private LocalTime hora;
    private String nomePaciente;
    private String status;

    public ConsultaAgendaDTO(LocalDate data, LocalTime hora, String nomePaciente, String status) {
        this.data = data;
        this.hora = hora;
        this.nomePaciente = nomePaciente;
        this.status = status;
    }

    public LocalDate getData() { return data; }
    public LocalTime getHora() { return hora; }
    public String getNomePaciente() { return nomePaciente; }
    public String getStatus() { return status; }
}