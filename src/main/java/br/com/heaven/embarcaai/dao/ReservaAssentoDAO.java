package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.model.ReservaAssento;
import br.com.heaven.embarcaai.exception.NaoEncontradoException;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservaAssentoDAO {
    private final DataSource dataSource;
    public ReservaAssentoDAO(DataSource dataSource) { this.dataSource = dataSource; }

    private ReservaAssento mapear(ResultSet rs) throws SQLException {
        ReservaAssento item = new ReservaAssento();
        item.setId(rs.getLong("ID_RESERVA"));
        item.setConsultaId(rs.getLong("ID_CONSULTA"));
        item.setViagemId(rs.getLong("ID_VIAGEM"));
        item.setStatusTransporte(rs.getString("STATUS_TRANSPORTE"));
        return item;
    }

    public List<ReservaAssento> listar() {
        return JdbcUtils.executar(dataSource, c -> {
            List<ReservaAssento> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM RESERVA_ASSENTO ORDER BY ID_RESERVA");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        });
    }

    public ReservaAssento buscarPorId(Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM RESERVA_ASSENTO WHERE ID_RESERVA=?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapear(rs);
                }
            }
            throw new NaoEncontradoException("ReservaAssento nao encontrado: " + id);
        });
    }

    public ReservaAssento criar(ReservaAssento item) {
        return JdbcUtils.executar(dataSource, c -> {
            Long id = JdbcUtils.proximoId(c, "SQ_TB_RESERVA_ASSENTO");
            try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO RESERVA_ASSENTO (ID_RESERVA, ID_CONSULTA, ID_VIAGEM, STATUS_TRANSPORTE) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, id);
                    ps.setLong(2, item.getConsultaId());
                    ps.setLong(3, item.getViagemId());
                    ps.setString(4, item.getStatusTransporte());
                ps.executeUpdate();
            }
            item.setId(id);
            return item;
        });
    }

    public ReservaAssento atualizar(Long id, ReservaAssento item) {
        return JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                "UPDATE RESERVA_ASSENTO SET ID_CONSULTA=?, ID_VIAGEM=?, STATUS_TRANSPORTE=? WHERE ID_RESERVA=?")) {
                    ps.setLong(1, item.getConsultaId());
                    ps.setLong(2, item.getViagemId());
                    ps.setString(3, item.getStatusTransporte());
                ps.setLong(4, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("ReservaAssento nao encontrado: " + id);
            }
            item.setId(id);
            return item;
        });
    }

    public void remover(Long id) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM RESERVA_ASSENTO WHERE ID_RESERVA=?")) {
                ps.setLong(1, id);
                if (ps.executeUpdate() == 0) throw new NaoEncontradoException("ReservaAssento nao encontrado: " + id);
            }
            return null;
        });
    }

    public List<ReservaAssento> listarPorConsulta(Long consultaId) {
        return listarPorCampo("ID_CONSULTA", consultaId);
    }
    public List<ReservaAssento> listarPorViagem(Long viagemId) {
        return listarPorCampo("ID_VIAGEM", viagemId);
    }

    public int contarOcupados(Long viagemId, Long ignorarId) {
        return JdbcUtils.executar(dataSource, c -> {
            String sql = "SELECT COUNT(*) FROM RESERVA_ASSENTO WHERE ID_VIAGEM=? " +
                    "AND STATUS_TRANSPORTE IN ('PENDENTE','CONFIRMADO') AND ID_RESERVA<>?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, viagemId);
                ps.setLong(2, ignorarId == null ? 0L : ignorarId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
            }
        });
    }

    public void cancelarReservasDaConsulta(Long consultaId) {
        JdbcUtils.executar(dataSource, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                "UPDATE RESERVA_ASSENTO SET STATUS_TRANSPORTE='CANCELADO' WHERE ID_CONSULTA=?")) {
                ps.setLong(1, consultaId);
                ps.executeUpdate();
            }
            return null;
        });
    }

    private List<ReservaAssento> listarPorCampo(String campo, Long id) {
        return JdbcUtils.executar(dataSource, c -> {
            List<ReservaAssento> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM RESERVA_ASSENTO WHERE " + campo + "=? ORDER BY ID_RESERVA")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapear(rs));
                }
            }
            return lista;
        });
    }

}
