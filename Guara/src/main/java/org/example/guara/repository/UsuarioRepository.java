package org.example.guara.repository;

import org.example.guara.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// CRUD do usuário via JDBC (sem regra de negócio)
@Repository
public class UsuarioRepository {
    private final DataSource dataSource;

    public UsuarioRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // monta um Usuario a partir da linha atual do ResultSet (evita repetir código)
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setNmCompleto(rs.getString("nm_completo"));
        u.setCpf(rs.getString("cpf"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setSenha(rs.getString("senha"));
        u.setDtNascimento(rs.getDate("dt_nascimento").toLocalDate());
        u.setSdPonto(rs.getDouble("sd_ponto"));
        u.setNvUsuario(rs.getInt("nv_usuario"));
        u.setTipoPerfil(rs.getString("tipo_perfil"));
        return u;
    }

    // READ - todos
    public List<Usuario> buscarTodos() {
        String sql = "SELECT * FROM T_LGT_USUARIO ORDER BY id_usuario";
        List<Usuario> lista = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(montarUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ - por id
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM T_LGT_USUARIO WHERE id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty(); // não achou
    }

    // CREATE
    public Usuario inserir(Usuario usuario) {
        String sql = """
                INSERT INTO T_LGT_USUARIO
                (nm_completo, cpf, email, username, senha, dt_nascimento, sd_ponto, nv_usuario, tipo_perfil)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_USUARIO"}) // ID gerado pelo IDENTITY
        ) {
            ps.setString(1, usuario.getNmCompleto());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getSenha());
            ps.setDate(6, Date.valueOf(usuario.getDtNascimento())); // LocalDate -> java.sql.Date
            ps.setDouble(7, usuario.getSdPonto());
            ps.setInt(8, usuario.getNvUsuario());
            ps.setString(9, usuario.getTipoPerfil());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuario;
    }

    // UPDATE (pontos e nível não mudam por aqui)
    public int atualizar(Usuario usuario) {
        String sql = """
                UPDATE T_LGT_USUARIO
                SET nm_completo = ?, cpf = ?, email = ?, username = ?, senha = ?, dt_nascimento = ?, tipo_perfil = ?
                WHERE id_usuario = ?
                """;
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, usuario.getNmCompleto());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getSenha());
            ps.setDate(6, Date.valueOf(usuario.getDtNascimento()));
            ps.setString(7, usuario.getTipoPerfil());
            ps.setInt(8, usuario.getId());
            return ps.executeUpdate(); // linhas afetadas (1 = ok, 0 = id não existe)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    public int deletar(int id) {
        String sql = "DELETE FROM T_LGT_USUARIO WHERE id_usuario = ?";
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

    // Barreiras do UNIQUE
    // idIgnorar: no cadastro passa 0; no update passa o id do próprio usuário
    public boolean existeCpf(String cpf, int idIgnorar) {
        return existe("SELECT COUNT(*) FROM T_LGT_USUARIO WHERE cpf = ? AND id_usuario <> ?", cpf, idIgnorar);
    }

    public boolean existeEmail(String email, int idIgnorar) {
        return existe("SELECT COUNT(*) FROM T_LGT_USUARIO WHERE email = ? AND id_usuario <> ?", email, idIgnorar);
    }

    public boolean existeUsername(String username, int idIgnorar) {
        return existe("SELECT COUNT(*) FROM T_LGT_USUARIO WHERE username = ? AND id_usuario <> ?", username, idIgnorar);
    }

    // junta os códigos e verifica se existe ou não
    private boolean existe(String sql, String valor, int idIgnorar) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, valor);
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // usado no login
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM T_LGT_USUARIO WHERE username = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // soma pontos ao saldo do usuário (RN10 - postagem aprovada)
    public int somarPontos(int idUsuario, double pontos) {
        String sql = "UPDATE T_LGT_USUARIO SET sd_ponto = sd_ponto + ? WHERE id_usuario = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDouble(1, pontos);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}