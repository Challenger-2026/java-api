package org.example.guara.repository;

import org.example.guara.model.Moderacao;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// acesso à T_LGT_MODERACAO via JDBC (semi-CRUD: criar e consultar)
@Repository
public class ModeracaoRepository {
    private final DataSource dataSource;

    public ModeracaoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // monta uma Moderacao a partir da linha atual do ResultSet
    private Moderacao montarModeracao(ResultSet rs) throws SQLException {
        Moderacao m = new Moderacao();
        m.setId(rs.getInt("id_moderacao"));
        m.setDecisao(rs.getString("decisao"));
        m.setJustificativa(rs.getString("justificativa")); // CLOB lido como String
        m.setBnModerador(rs.getDouble("bn_moderador"));
        m.setIdModerador(rs.getInt("T_LGT_MODERADOR_id_moderador"));
        m.setIdPostagem(rs.getInt("T_LGT_POSTAGEM_id_postagem"));
        return m;
    }

    // READ - todas
    public List<Moderacao> buscarTodas() {
        String sql = "SELECT * FROM T_LGT_MODERACAO ORDER BY id_moderacao";
        List<Moderacao> lista = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarModeracao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ - por id
    public Optional<Moderacao> buscarPorId(int id) {
        String sql = "SELECT * FROM T_LGT_MODERACAO WHERE id_moderacao = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarModeracao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // confere se a postagem já foi moderada (a FK da postagem é UNIQUE)
    public boolean existePorPostagem(int idPostagem) {
        String sql = "SELECT COUNT(*) FROM T_LGT_MODERACAO WHERE T_LGT_POSTAGEM_id_postagem = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idPostagem);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0; // 1ª coluna do resultado = o COUNT
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // CREATE
    public Moderacao inserir(Moderacao moderacao) {
        String sql = """
                INSERT INTO T_LGT_MODERACAO
                (decisao, justificativa, bn_moderador, T_LGT_MODERADOR_id_moderador, T_LGT_POSTAGEM_id_postagem)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_MODERACAO"}) // ID gerado pelo IDENTITY
        ) {
            ps.setString(1, moderacao.getDecisao());
            ps.setString(2, moderacao.getJustificativa());
            ps.setDouble(3, moderacao.getBnModerador());
            ps.setInt(4, moderacao.getIdModerador());
            ps.setInt(5, moderacao.getIdPostagem());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    moderacao.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return moderacao;
    }
}