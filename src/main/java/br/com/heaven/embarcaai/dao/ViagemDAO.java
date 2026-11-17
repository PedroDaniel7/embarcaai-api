package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.model.Viagem;
import br.com.heaven.embarcaai.exception.NaoEncontradoException;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ViagemDAO {
    private final DataSource dataSource;
    public ViagemDAO(DataSource dataSource) { this.dataSource = dataSource; }

    private Viagem mapear(ResultSet rs) throws SQLException {
        Viagem item = new Viagem();
        item.setId(rs.getLong("ID_VIAGEM"));
        item.setVanId(rs.getLong("ID_VAN"));
        item.setUnidadeDestinoId(rs.getLong("ID_UNIDADE_DESTINO"));
        item.setDataHoraSaida(rs.getTimestamp("DATA_HORA_SAIDA").toLocalDateTime());
        item.setStatus(rs.getString("STATUS"));
        return item;
    }

    public List<Viagem> listar() {
        return JdbcUtils.executar(dataSource, c -> {
            List<Viagem> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM VIAGEM ORDER BY ID_VIAGEM");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        });
    }

    public Viagem buscarPorId(Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM VIAGEM WHERE ID_VIAGEM=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
            throw new NaoEncontradoException("Viagem nao encontrado: " + id);
        });
    }

    public Viagem criar(Viagem item) {
        return JdbcUtils.executar(dataSource, c -> {
            Long id = JdbcUtils.proximoId(c, "SQ_TB_VIAGEM");
            try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO VIAGEM (ID_VIAGEM, ID_VAN, ID_UNIDADE_DESTINO, DATA_HORA_SAIDA, STATUS) VALUES (?, ?, ?, ?, ?)")) {
                ps.setLong(1, id);
                    ps.setLong(2, item.getVanId());
                    ps.setLong(3, item.getUnidadeDestinoId());
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(item.getDataHoraSaida()));
                    ps.setString(5, item.getStatus());
                ps.executeUpdate();
            }
            item.setId(id);
            return item;
        });
    }

    public Viagem atualizar(Long id, Viagem item) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                "UPDATE VIAGEM SET ID_VAN=?, ID_UNIDADE_DESTINO=?, DATA_HORA_SAIDA=?, STATUS=? WHERE ID_VIAGEM=?")) {
                    ps.setLong(1, item.getVanId());
                    ps.setLong(2, item.getUnidadeDestinoId());
                    ps.setTimestamp(3, java.sql.Timestamp.valueOf(item.getDataHoraSaida()));
                    ps.setString(4, item.getStatus());
                ps.setLong(5, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Viagem nao encontrado: " + id);
            }
            item.setId(id);
            return item;
        });
    }

    public void remover(Long id) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM VIAGEM WHERE ID_VIAGEM=?")) {
                ps.setLong(1, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("Viagem nao encontrado: " + id);
            }
            return null;
        });
    }

    public void bloquear(Long id) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT ID_VIAGEM FROM VIAGEM WHERE ID_VIAGEM=? FOR UPDATE")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new NaoEncontradoException("Viagem nao encontrada: " + id);
                }
            }
            return null;
        });
    }

    public int capacidadeDaVan(Long vanId) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT CAPACIDADE FROM VAN WHERE ID_VAN=?")) {
                ps.setLong(1, vanId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            throw new NaoEncontradoException("Van nao encontrada: " + vanId);
        });
    }

}
