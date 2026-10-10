package com.example.sistema_gestao_pacientes.Util;

public class UserSession {
    private static UserSession instance;
    private String username;
    private String perfil;
    private String nomeMedico;

    private UserSession(String username, String perfil, String nomeMedico) {
        this.username = username;
        this.perfil = perfil;
        this.nomeMedico = nomeMedico;
    }

    public static void iniciarSessao(String username, String perfil, String nomeMedico) {
        instance = new UserSession(username, perfil, nomeMedico);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void encerrarSessao() {
        instance = null;
    }

    public String getUsername() { return username; }
    public String getPerfil() { return perfil; }
    public String getNomeMedico() { return nomeMedico; }
}
