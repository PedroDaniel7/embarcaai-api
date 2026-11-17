package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.model.Consulta;
import br.com.heaven.embarcaai.exception.NaoEncontradoException;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsultaDAO {
    private final DataSource dataSource;
    public ConsultaDAO(DataSource dataSource) { this.dataSource = dataSource; }

    private Consulta mapear(ResultSet rs) throws SQLException {
        Consulta item = new Consulta();
        item.setId(rs.getLong("ID_CONSULTA"));
        item.setPacienteId(rs.getLong("ID_PACIENTE"));
        item.setUnidadeId(rs.getLong("ID_UNIDADE"));
        item.setEspecialidade(rs.getString("ESPECIALIDADE"));
        item.setDataHora(rs.getTimestamp("DATA_HORA").toLocalDateTime());
        item.setPrecisaTransporte(rs.getString("PRECISA_TRANSPORTE"));
        item.setStatus(rs.getString("STATUS"));
        return item;
    }

    public List<Consulta> listar() {
        return JdbcUtils.executar(dataSource, c -> {
            List<Consulta> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM CONSULTA ORDER BY ID_CONSULTA");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        });
    }

    public Consulta buscarPorId(Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM CONSULTA WHERE ID_CONSULTA=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
            throw new NaoEncontradoException("Consulta nao encontrado: " + id);
        });
    }

    public Consulta criar(Consulta item) {
        return JdbcUtils.executar(dataSource, c -> {
            Long id = JdbcUtils.proximoId(c, "SQ_TB_CONSULTA");
            try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO CONSULTA (ID_CONSULTA, ID_PACIENTE, ID_UNIDADE, ESPECIALIDADE, DATA_HORA, PRECISA_TRANSPORTE, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setLong(1, id);
                    ps.setLong(2, item.getPacienteId());
                    ps.setLong(3, item.getUnidadeId());
                    ps.setString(4, item.getEspecialidade());
                    ps.setTimestamp(5, java.sql.Timestamp.valueOf(item.getDataHora()));
                    ps.setString(6, item.getPrecisaTransporte());
                    ps.setString(7, item.getStatus());
                ps.executeUpdate();
            }
            item.setId(id);
            return item;
        });
    }

    public Consulta atualizar(Long id, Consulta item) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                "UPDATE CONSULTA SET ID_PACIENTE=?, ID_UNIDADE=?, ESPECIALIDADE=?, DATA_HORA=?, PRECISA_TRANSPORTE=?, STATUS=? WHERE ID_CONSULTA=?")) {
                    ps.setLong(1, item.getPacienteId());
                    ps.setLong(2, item.getUnidadeId());
                    ps.setString(3, item.getEspecialidade());
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(item.getDataHora()));
                    ps.setString(5, item.getPrecisaTransporte());
                    ps.setString(6, item.getStatus());
                ps.setLong(7, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Consulta nao encontrado: " + id);
            }
            item.setId(id);
            return item;
        });
    }

    public void remover(Long id) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM CONSULTA WHERE ID_CONSULTA=?")) {
                ps.setLong(1, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Consulta nao encontrado: " + id);
            }
            return null;
        });
    }

    public List<Consulta> listarPorPaciente(Long pacienteId) {
        return listarPorCampo("ID_PACIENTE", pacienteId);
    }

    // Bloqueia consulta enquanto registra a resposta do paciente.
    public Consulta bloquear(Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM CONSULTA WHERE ID_CONSULTA=? FOR UPDATE")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
            throw new NaoEncontradoException("Consulta nao encontrada: " + id);
        });
    }

    private List<Consulta> listarPorCampo(String campo, Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            List<Consulta> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM CONSULTA WHERE " + campo + "=? ORDER BY ID_CONSULTA")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
            return lista;
        });
    }

}
