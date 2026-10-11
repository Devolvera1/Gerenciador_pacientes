package com.example.sistema_gestao_pacientes.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.sistema_gestao_pacientes.Util.ConsultaSession;
import com.example.sistema_gestao_pacientes.Util.PacienteSession;
import com.example.sistema_gestao_pacientes.Util.ProximaConsulta;
import com.example.sistema_gestao_pacientes.Util.UserSession;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String URL = "jdbc:mysql://192.168.68.110:3306/clinica_medica";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC do MySQL não foi encontrado!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT u.senha, u.perfil, m.nome AS nome_medico " +
                "FROM usuarios u " +
                "LEFT JOIN medicos m ON u.medico_id = m.id " +
                "WHERE u.username = ? AND u.status = 'ATIVO'";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPasswordBD = resultSet.getString("senha");

                    if (BCrypt.checkpw(password, hashedPasswordBD)) {
                        String perfil = resultSet.getString("perfil");
                        String nomeMedico = resultSet.getString("nome_medico");

                        UserSession.iniciarSessao(username, perfil, nomeMedico);
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERRO DE CONEXÃO/SQL na autenticação.", e);
        }
        return false;
    }

    public List<ConsultaSession> listarConsultasDeHoje(String perfil, String nomeMedico) {
        List<ConsultaSession> lista = new ArrayList<>();

        String query = "SELECT c.data_consulta, DATE_FORMAT(c.hora_consulta, '%H:%i') AS hora_formatada, " +
                "p.nome AS nome_paciente, m.nome AS nome_medico, c.status, " +
                "e.nome AS especialidade, m.sala AS sala, IFNULL(p.alergias, 'Nenhuma registrada') AS alergia " +
                "FROM consultas c " +
                "INNER JOIN pacientes p ON c.paciente_id = p.id " +
                "INNER JOIN medicos m ON c.medico_id = m.id " +
                "INNER JOIN especialidades e ON m.especialidade_id = e.id " +
                "WHERE c.data_consulta = CURRENT_DATE ";

        if ("MEDICO".equals(perfil) && nomeMedico != null) {
            query += "AND m.nome = ? ";
        }

        query += "ORDER BY c.hora_consulta ASC";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if ("MEDICO".equals(perfil) && nomeMedico != null) {
                preparedStatement.setString(1, nomeMedico);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    lista.add(new ConsultaSession(
                            resultSet.getString("data_consulta"),
                            resultSet.getString("hora_formatada"),
                            resultSet.getString("nome_paciente"),
                            resultSet.getString("nome_medico"),
                            "Rotina",
                            resultSet.getString("status"),
                            resultSet.getString("especialidade"),
                            resultSet.getString("sala"),
                            resultSet.getString("alergia")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERRO AO LISTAR CONSULTAS DE HOJE.", e);
        }
        return lista;
    }

    public ProximaConsulta buscarProximaConsulta(String perfil, String nomeMedico) {
        String query = "SELECT DATE_FORMAT(c.hora_consulta, '%H:%i') AS hora, " +
                "p.nome AS paciente, m.nome AS medico, e.nome AS especialidade, " +
                "m.sala AS sala, IFNULL(p.alergias, 'Nenhuma registrada') AS alergia " +
                "FROM consultas c " +
                "INNER JOIN pacientes p ON c.paciente_id = p.id " +
                "INNER JOIN medicos m ON c.medico_id = m.id " +
                "INNER JOIN especialidades e ON m.especialidade_id = e.id " +
                "WHERE c.data_consulta = CURRENT_DATE " +
                "AND c.hora_consulta >= CURRENT_TIME " +
                "AND c.status IN ('AGENDADA', 'CONFIRMADA') ";

        if ("MEDICO".equals(perfil) && nomeMedico != null) {
            query += "AND m.nome = ? ";
        }

        query += "ORDER BY c.hora_consulta ASC LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if ("MEDICO".equals(perfil) && nomeMedico != null) {
                preparedStatement.setString(1, nomeMedico);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProximaConsulta(
                            resultSet.getString("hora"),
                            resultSet.getString("paciente"),
                            resultSet.getString("medico"),
                            resultSet.getString("especialidade"),
                            resultSet.getString("sala"),
                            resultSet.getString("alergia")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ERRO AO BUSCAR PRÓXIMA CONSULTA.", e);
        }

        return null;
    }

    public int[] contarStatusConsultas(String perfil, String nomeMedico) {
        int[] resultados = new int[3];
        String query = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN c.status = 'CONFIRMADA' THEN 1 ELSE 0 END) AS confirmadas, " +
                "SUM(CASE WHEN c.status = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas " +
                "FROM consultas c " +
                "INNER JOIN medicos m ON c.medico_id = m.id " +
                "WHERE c.data_consulta = CURRENT_DATE ";

        if ("MEDICO".equals(perfil) && nomeMedico != null) {
            query += "AND m.nome = ? ";
        }

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if ("MEDICO".equals(perfil) && nomeMedico != null) {
                ps.setString(1, nomeMedico);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultados[0] = rs.getInt("total");
                    resultados[1] = rs.getInt("confirmadas");
                    resultados[2] = rs.getInt("canceladas");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    public int contarTotalPacientes() {
        String query = "SELECT COUNT(*) AS total FROM pacientes";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar total de pacientes", e);
        }
        return 0;
    }

    public int contarNovosPacientesMes() {
        String query = "SELECT COUNT(*) AS total FROM pacientes WHERE MONTH(data_cadastro) = MONTH(CURRENT_DATE) AND YEAR(data_cadastro) = YEAR(CURRENT_DATE)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar novos pacientes do mês", e);
        }
        return 0;
    }

    public int contarTotalMedicos() {
        String query = "SELECT COUNT(*) AS total FROM medicos WHERE ativo = TRUE";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao contar total de médicos", e);
        }
        return 0;
    }

    public List<PacienteSession> listarPacientesDoBanco() {
        List<PacienteSession> lista = new ArrayList<>();

        String sql = "SELECT p.id, p.nome, p.telefone, p.numero_utente, " +
                "(SELECT MAX(c.data_consulta) FROM consultas c WHERE c.paciente_id = p.id) AS ultima_consulta " +
                "FROM pacientes p";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String contato = rs.getString("telefone");
                String utente = rs.getString("numero_utente");
                String ultimaConsulta = rs.getString("ultima_consulta");

                if (ultimaConsulta == null || ultimaConsulta.isEmpty()) {
                    ultimaConsulta = "Sem consultas";
                }

                lista.add(new PacienteSession(id, nome, contato, utente, ultimaConsulta));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean apagarPacienteDoBanco(int pacienteId) {
        String sqlProntuarios = "DELETE FROM prontuarios WHERE consulta_id IN (SELECT id FROM consultas WHERE paciente_id = ?)";
        String sqlConsultas = "DELETE FROM consultas WHERE paciente_id = ?";
        String sqlPaciente = "DELETE FROM pacientes WHERE id = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlProntuarios)) {
                pstmt1.setInt(1, pacienteId);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlConsultas)) {
                pstmt2.setInt(1, pacienteId);
                pstmt2.executeUpdate();
            }

            boolean sucesso;
            try (PreparedStatement pstmt3 = conn.prepareStatement(sqlPaciente)) {
                pstmt3.setInt(1, pacienteId);
                sucesso = pstmt3.executeUpdate() > 0;
            }

            conn.commit();
            return sucesso;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}