package org.example.guara.repository;

import org.example.guara.model.Moderador;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// acesso à T_LGT_MODERADOR (uso interno: UsuarioService e ModeracaoService)
@Repository
public class ModeradorRepository {
    private final DataSource dataSource;

    public ModeradorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Moderador montarModerador(ResultSet rs) throws SQLException {
        Moderador m = new Moderador();
        m.setId(rs.getInt("id_moderador"));
        m.setNvModerador(rs.getInt("nv_moderador"));
        m.setRtModerador(rs.getString("rt_moderador"));
        m.setDtAdesao(rs.getDate("dt_adesao").toLocalDate());
        m.setIdUsuario(rs.getInt("T_LGT_USUARIO_id_usuario"));
        return m;
    }

    // CREATE - chamado quando o usuário vira MODERADOR
    public Moderador inserir(Moderador moderador) {
        String sql = """
                INSERT INTO T_LGT_MODERADOR
                (nv_moderador, rt_moderador, dt_adesao, T_LGT_USUARIO_id_usuario)
                VALUES (?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_MODERADOR"})
        ) {
            ps.setInt(1, moderador.getNvModerador());
            ps.setString(2, moderador.getRtModerador());
            ps.setDate(3, Date.valueOf(moderador.getDtAdesao()));
            ps.setInt(4, moderador.getIdUsuario());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    moderador.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return moderador;
    }

    // READ - pelo id do usuário (usado para saber se o usuário é moderador)
    public Optional<Moderador> buscarPorIdUsuario(int idUsuario) {
        String sql = "SELECT * FROM T_LGT_MODERADOR WHERE T_LGT_USUARIO_id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarModerador(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // DELETE - chamado na troca de perfil ou exclusão do usuário
    public int deletarPorIdUsuario(int idUsuario) {
        String sql = "DELETE FROM T_LGT_MODERADOR WHERE T_LGT_USUARIO_id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate(); // 0 se o usuário não era moderador
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}