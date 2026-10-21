package org.example.guara.repository;

import org.example.guara.model.Postagem;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// acesso à T_LGT_POSTAGEM via JDBC (sem regra de negócio)
@Repository
public class PostagemRepository {
    private final DataSource dataSource;

    public PostagemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // monta uma Postagem a partir da linha atual do ResultSet
    private Postagem montarPostagem(ResultSet rs) throws SQLException {
        Postagem p = new Postagem();
        p.setId(rs.getInt("id_postagem"));
        p.setCtTexto(rs.getString("ct_texto"));   // CLOB lido como String
        p.setCtUrl(rs.getString("ct_url"));       // CLOB lido como String
        p.setStValidacao(rs.getString("st_validacao"));
        p.setPtGerado(rs.getDouble("pt_gerado"));
        p.setIdUsuario(rs.getInt("T_LGT_USUARIO_id_usuario"));
        p.setIdComunidade(rs.getInt("T_LGT_COMUNIDADE_id_comunidade"));
        return p;
    }

    // READ - todas
    public List<Postagem> buscarTodas() {
        String sql = "SELECT * FROM T_LGT_POSTAGEM ORDER BY id_postagem";
        List<Postagem> lista = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarPostagem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ - por status (usado na fila de moderação com "PENDENTE")
    public List<Postagem> buscarPorStatus(String status) {
        String sql = "SELECT * FROM T_LGT_POSTAGEM WHERE st_validacao = ? ORDER BY id_postagem";
        List<Postagem> lista = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarPostagem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ - por id
    public Optional<Postagem> buscarPorId(int id) {
        String sql = "SELECT * FROM T_LGT_POSTAGEM WHERE id_postagem = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarPostagem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // CREATE
    public Postagem inserir(Postagem postagem) {
        String sql = """
                INSERT INTO T_LGT_POSTAGEM
                (ct_texto, ct_url, st_validacao, pt_gerado, T_LGT_USUARIO_id_usuario, T_LGT_COMUNIDADE_id_comunidade)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_POSTAGEM"}) // ID gerado pelo IDENTITY
        ) {
            ps.setString(1, postagem.getCtTexto());
            ps.setString(2, postagem.getCtUrl());
            ps.setString(3, postagem.getStValidacao());
            ps.setDouble(4, postagem.getPtGerado());
            ps.setInt(5, postagem.getIdUsuario());
            ps.setInt(6, postagem.getIdComunidade());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    postagem.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postagem;
    }

    // UPDATE - só o status (chamado pela moderação)
    public int atualizarStatus(int id, String status) {
        String sql = "UPDATE T_LGT_POSTAGEM SET st_validacao = ? WHERE id_postagem = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}