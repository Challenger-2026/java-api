package org.example.guara.repository;

import org.example.guara.model.Criador;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// acesso à T_LGT_CRIADOR
@Repository
public class CriadorRepository {
    private final DataSource dataSource;

    public CriadorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Criador montarCriador(ResultSet rs) throws SQLException {
        Criador c = new Criador();
        c.setId(rs.getInt("id_criador"));
        c.setNvCriador(rs.getInt("nv_criador"));
        c.setRtCriador(rs.getString("rt_criador"));
        c.setDtAdesao(rs.getDate("dt_adesao").toLocalDate());
        c.setIdUsuario(rs.getInt("T_LGT_USUARIO_id_usuario"));
        return c;
    }

    // CREATE
    public Criador inserir(Criador criador) {
        String sql = """
                INSERT INTO T_LGT_CRIADOR
                (nv_criador, rt_criador, dt_adesao, T_LGT_USUARIO_id_usuario)
                VALUES (?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_CRIADOR"})
        ) {
            ps.setInt(1, criador.getNvCriador());
            ps.setString(2, criador.getRtCriador());
            ps.setDate(3, Date.valueOf(criador.getDtAdesao()));
            ps.setInt(4, criador.getIdUsuario());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    criador.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return criador;
    }

    // READ - pelo id do usuário (usado para saber se o usuário é criador)
    public Optional<Criador> buscarPorIdUsuario(int idUsuario) {
        String sql = "SELECT * FROM T_LGT_CRIADOR WHERE T_LGT_USUARIO_id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarCriador(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // DELETE - pelo id do usuário (troca de perfil ou exclusão do usuário)
    public int deletarPorIdUsuario(int idUsuario) {
        String sql = "DELETE FROM T_LGT_CRIADOR WHERE T_LGT_USUARIO_id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate(); // 0 se o usuário não era criador
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}