package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.dto.UnidadeSaidaDTO;
import br.com.heaven.embarcaai.dto.VanSaidaDTO;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogoDAO {
    private final DataSource fonte;
    public CatalogoDAO(DataSource fonte) { this.fonte = fonte; }

    public List<UnidadeSaidaDTO> unidades() {
        return JdbcUtils.executar(fonte, c -> {
            List<UnidadeSaidaDTO> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM UNIDADE_SAUDE ORDER BY ID_UNIDADE");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new UnidadeSaidaDTO(rs.getLong("ID_UNIDADE"),
                        rs.getString("NOME"), rs.getString("TIPO"), rs.getString("CIDADE")));
            }
            return lista;
        });
    }

    public List<VanSaidaDTO> vans() {
        return JdbcUtils.executar(fonte, c -> {
            List<VanSaidaDTO> lista = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM VAN ORDER BY ID_VAN");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new VanSaidaDTO(rs.getLong("ID_VAN"),
                        rs.getString("PLACA"), rs.getInt("CAPACIDADE")));
            }
            return lista;
        });
    }
}
