package com.example.sistema_gestao_pacientes.Util;

public class ProximaConsulta {
    private String hora;
    private String paciente;
    private String medico;
    private String especialidade;
    private String sala;
    private String alergia;

    public ProximaConsulta(String hora, String paciente, String medico, String especialidade, String sala, String alergia) {
        this.hora = hora;
        this.paciente = paciente;
        this.medico = medico;
        this.especialidade = especialidade;
        this.sala = sala;
        this.alergia = alergia;
    }

    public String getHora() { return hora; }
    public String getPaciente() { return paciente; }
    public String getMedico() { return medico; }
    public String getEspecialidade() { return especialidade; }
    public String getSala() { return sala; }
    public String getAlergia() { return alergia; }
}

