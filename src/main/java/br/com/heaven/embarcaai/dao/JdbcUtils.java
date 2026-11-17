package br.com.heaven.embarcaai.dao;

import br.com.heaven.embarcaai.exception.RegraNegocioException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Pequeno utilitario: abre/libera a conexao respeitando @Transactional. */
public final class JdbcUtils {
    private JdbcUtils() {}

    @FunctionalInterface
    public interface AcaoSql<T> { T executar(Connection conexao) throws SQLException; }

    public static <T> T executar(DataSource fonte, AcaoSql<T> acao) {
        Connection conexao = DataSourceUtils.getConnection(fonte);
        try {
            return acao.executar(conexao);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1 || e.getErrorCode() == 2291 || e.getErrorCode() == 2292 ||
                    e.getErrorCode() == 2290) {
                throw new RegraNegocioException("Operacao nao permitida: dados duplicados ou vinculados");
            }
            throw new IllegalStateException("Erro ao acessar os dados");
        } finally {
            DataSourceUtils.releaseConnection(conexao, fonte);
        }
    }

    public static long proximoId(Connection c, String sequence) throws SQLException {
        // Chamado apenas com constantes no codigo, nunca com parametros do usuario.
        try (PreparedStatement ps = c.prepareStatement("SELECT " + sequence + ".NEXTVAL FROM DUAL");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
