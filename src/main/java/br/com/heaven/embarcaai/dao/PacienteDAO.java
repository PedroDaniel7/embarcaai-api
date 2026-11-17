package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.model.Paciente;
import br.com.heaven.embarcaai.exception.NaoEncontradoException;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PacienteDAO {
    private final DataSource dataSource;
    public PacienteDAO(DataSource dataSource) { this.dataSource = dataSource; }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente item = new Paciente();
        item.setId(rs.getLong("ID_PACIENTE"));
        item.setNome(rs.getString("NOME"));
        item.setTelefone(rs.getString("TELEFONE"));
        item.setDataNascimento(rs.getDate("DATA_NASCIMENTO").toLocalDate());
        item.setZonaRural(rs.getString("ZONA_RURAL"));
        return item;
    }

    public List<Paciente> listar() {
        return JdbcUtils.executar(dataSource, c -> {
            List<Paciente> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM PACIENTE ORDER BY ID_PACIENTE");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        });
    }

    public Paciente buscarPorId(Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM PACIENTE WHERE ID_PACIENTE=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
            throw new NaoEncontradoException("Paciente nao encontrado: " + id);
        });
    }

    public Paciente criar(Paciente item) {
        return JdbcUtils.executar(dataSource, c -> {
            Long id = JdbcUtils.proximoId(c, "SQ_TB_PACIENTE");
            try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO PACIENTE (ID_PACIENTE, NOME, TELEFONE, DATA_NASCIMENTO, ZONA_RURAL) VALUES (?, ?, ?, ?, ?)")) {
                ps.setLong(1, id);
                    ps.setString(2, item.getNome());
                    ps.setString(3, item.getTelefone());
                    ps.setDate(4, java.sql.Date.valueOf(item.getDataNascimento()));
                    ps.setString(5, item.getZonaRural());
                ps.executeUpdate();
            }
            item.setId(id);
            return item;
        });
    }

    public Paciente atualizar(Long id, Paciente item) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                "UPDATE PACIENTE SET NOME=?, TELEFONE=?, DATA_NASCIMENTO=?, ZONA_RURAL=? WHERE ID_PACIENTE=?")) {
                    ps.setString(1, item.getNome());
                    ps.setString(2, item.getTelefone());
                    ps.setDate(3, java.sql.Date.valueOf(item.getDataNascimento()));
                    ps.setString(4, item.getZonaRural());
                ps.setLong(5, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Paciente nao encontrado: " + id);
            }
            item.setId(id);
            return item;
        });
    }

    public void remover(Long id) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM PACIENTE WHERE ID_PACIENTE=?")) {
                ps.setLong(1, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Paciente nao encontrado: " + id);
            }
            return null;
        });
    }

}
