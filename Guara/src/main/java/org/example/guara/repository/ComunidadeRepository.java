package org.example.guara.repository;

import org.example.guara.model.Comunidade;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// CRUD da comunidade via JDBC (sem regra de negócio)
@Repository
public class ComunidadeRepository {
    private final DataSource dataSource;

    public ComunidadeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // monta uma Comunidade a partir da linha atual do ResultSet
    private Comunidade montarComunidade(ResultSet rs) throws SQLException {
        Comunidade c = new Comunidade();
        c.setId(rs.getInt("id_comunidade"));
        c.setNmComunidade(rs.getString("nm_comunidade"));
        c.setDescricao(rs.getString("descricao")); // CLOB lido como String
        c.setDtCriacao(rs.getDate("dt_criacao").toLocalDate());
        c.setIdCriador(rs.getInt("T_LGT_CRIADOR_id_criador"));
        c.setIdRanking(rs.getObject("T_LGT_RANKING_id_ranking", Integer.class)); // null se não tiver ranking
        return c;
    }

    // READ - todas
    public List<Comunidade> buscarTodas() {
        String sql = "SELECT * FROM T_LGT_COMUNIDADE ORDER BY id_comunidade";
        List<Comunidade> lista = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarComunidade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ - por id
    public Optional<Comunidade> buscarPorId(int id) {
        String sql = "SELECT * FROM T_LGT_COMUNIDADE WHERE id_comunidade = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarComunidade(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // CREATE
    public Comunidade inserir(Comunidade comunidade) {
        String sql = """
                INSERT INTO T_LGT_COMUNIDADE
                (nm_comunidade, descricao, dt_criacao, T_LGT_CRIADOR_id_criador, T_LGT_RANKING_id_ranking)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_COMUNIDADE"}) // ID gerado pelo IDENTITY
        ) {
            ps.setString(1, comunidade.getNmComunidade());
            ps.setString(2, comunidade.getDescricao());
            ps.setDate(3, Date.valueOf(comunidade.getDtCriacao()));
            ps.setInt(4, comunidade.getIdCriador());
            if (comunidade.getIdRanking() == null) {
                ps.setNull(5, Types.INTEGER); // comunidade nasce sem ranking
            } else {
                ps.setInt(5, comunidade.getIdRanking());
            }
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    comunidade.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comunidade;
    }

    // UPDATE (só nome e descrição; criador, data e ranking não mudam por aqui)
    public int atualizar(Comunidade comunidade) {
        String sql = """
                UPDATE T_LGT_COMUNIDADE
                SET nm_comunidade = ?, descricao = ?
                WHERE id_comunidade = ?
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, comunidade.getNmComunidade());
            ps.setString(2, comunidade.getDescricao());
            ps.setInt(3, comunidade.getId());
            return ps.executeUpdate(); // quantas linhas foram alteradas
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    public int deletar(int id) {
        String sql = "DELETE FROM T_LGT_COMUNIDADE WHERE id_comunidade = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}