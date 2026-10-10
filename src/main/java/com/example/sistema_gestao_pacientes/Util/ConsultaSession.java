package com.example.sistema_gestao_pacientes.Util;

public class ConsultaSession {
    private String data;
    private String hora;
    private String paciente;
    private String medico;
    private String tipo;
    private String estado;
    private String especialidade;
    private String sala;
    private String alergia;

    public ConsultaSession(String data, String hora, String paciente, String medico, String tipo, String estado, String especialidade, String sala, String alergia) {
        this.data = data;
        this.hora = hora;
        this.paciente = paciente;
        this.medico = medico;
        this.tipo = tipo;
        this.estado = estado;
        this.especialidade = especialidade;
        this.sala = sala;
        this.alergia = alergia;
    }

    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getPaciente() { return paciente; }
    public String getMedico() { return medico; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public String getEspecialidade() { return especialidade; }
    public String getSala() { return sala; }
    public String getAlergia() { return alergia; }
}