package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.dto.LembreteSaidaDTO;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LembreteDAO {
    private final DataSource dataSource;
    public LembreteDAO(DataSource dataSource) { this.dataSource = dataSource; }

    public void registrarResposta(Long consultaId, String resposta) {
        JdbcUtils.executar(dataSource, c -> {
            long id = JdbcUtils.proximoId(c, "SQ_TB_LEMBRETE");
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO LEMBRETE (ID_LEMBRETE, ID_CONSULTA, DATA_ENVIO, RESPOSTA) VALUES (?, ?, SYSDATE, ?)")) {
                ps.setLong(1, id);
                ps.setLong(2, consultaId);
                ps.setString(3, resposta);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public List<LembreteSaidaDTO> listarPorConsulta(Long consultaId) {
        return JdbcUtils.executar(dataSource, c -> {
            List<LembreteSaidaDTO> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM LEMBRETE WHERE ID_CONSULTA=? ORDER BY ID_LEMBRETE")) {
                ps.setLong(1, consultaId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(new LembreteSaidaDTO(
                            rs.getLong("ID_LEMBRETE"), rs.getLong("ID_CONSULTA"),
                            rs.getTimestamp("DATA_ENVIO").toLocalDateTime(), rs.getString("RESPOSTA")));
                }
            }
            return lista;
        });
    }
}
