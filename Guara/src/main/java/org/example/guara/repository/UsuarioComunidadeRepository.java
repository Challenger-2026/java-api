package org.example.guara.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// acesso à T_LGT_USUARIO_COMUNIDADE (quem é membro de qual comunidade)
@Repository
public class UsuarioComunidadeRepository {
    private final DataSource dataSource;

    public UsuarioComunidadeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ENTRAR na comunidade
    public void inserir(int idUsuario, int idComunidade) {
        String sql = """
                INSERT INTO T_LGT_USUARIO_COMUNIDADE
                (T_LGT_USUARIO_id_usuario, T_LGT_COMUNIDADE_id_comunidade, membros)
                VALUES (?, ?, 1)
                """; // membros = 1: cada linha conta como um membro
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idComunidade);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // SAIR da comunidade
    public int deletar(int idUsuario, int idComunidade) {
        String sql = """
                DELETE FROM T_LGT_USUARIO_COMUNIDADE
                WHERE T_LGT_USUARIO_id_usuario = ? AND T_LGT_COMUNIDADE_id_comunidade = ?
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idComunidade);
            return ps.executeUpdate(); // 0 se ele não era membro
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // confere se o usuário é membro (usado na postagem e no "entrar")
    public boolean ehMembro(int idUsuario, int idComunidade) {
        String sql = """
                SELECT COUNT(*) FROM T_LGT_USUARIO_COMUNIDADE
                WHERE T_LGT_USUARIO_id_usuario = ? AND T_LGT_COMUNIDADE_id_comunidade = ?
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idComunidade);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // quantos membros a comunidade tem
    public int contarMembros(int idComunidade) {
        String sql = "SELECT COUNT(*) FROM T_LGT_USUARIO_COMUNIDADE WHERE T_LGT_COMUNIDADE_id_comunidade = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, idComunidade);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}