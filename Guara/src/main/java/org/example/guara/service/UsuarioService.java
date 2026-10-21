package org.example.guara.service;

import org.example.guara.dto.*;
import org.example.guara.exception.CredenciaisInvalidasException;
import org.example.guara.exception.RecursoNaoEncontrado;
import org.example.guara.exception.RegraNegocioException;
import org.example.guara.model.Criador;
import org.example.guara.model.Moderador;
import org.example.guara.model.Usuario;
import org.example.guara.repository.CriadorRepository;
import org.example.guara.repository.ModeradorRepository;
import org.example.guara.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// regras de negócio do usuário
@Service
public class UsuarioService {

    // valores iniciais dos perfis (trocar aqui se o grupo definir outro rótulo)
    private static final int NIVEL_INICIAL = 1;
    private static final String ROTULO_INICIAL = "INICIANTE";

    private final UsuarioRepository usuarioRepository;
    private final CriadorRepository criadorRepository;
    private final ModeradorRepository moderadorRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CriadorRepository criadorRepository,
                          ModeradorRepository moderadorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.criadorRepository = criadorRepository;
        this.moderadorRepository = moderadorRepository;
    }

    // READ - todos
    public List<DTOUsuarioResponse> listarTodos() {
        List<DTOUsuarioResponse> resposta = new ArrayList<>();
        for (Usuario u : usuarioRepository.buscarTodos()) {
            resposta.add(paraResponse(u));
        }
        return resposta;
    }

    // READ - por id
    public DTOUsuarioResponse buscarPorId(int id) {
        return paraResponse(buscarUsuarioOuErro(id));
    }

    // CREATE
    public DTOUsuarioResponse cadastrar(DTOUsuarioRequest dto) {
        validarDuplicidade(dto, 0);

        Usuario u = new Usuario();
        preencherDados(u, dto);
        u.setTipoPerfil("COMUM");     // todo usuário começa comum; muda pela rota /perfil
        u.setSdPonto(0);              // todo usuário começa sem pontos
        u.setNvUsuario(NIVEL_INICIAL);

        usuarioRepository.inserir(u); // aqui o id é gerado
        return paraResponse(u);
    }

    // UPDATE (só nome, username e senha, como no protótipo)
    public DTOUsuarioResponse atualizar(int id, DTOUsuarioUpdateRequest dto) {
        Usuario u = buscarUsuarioOuErro(id);

        // RF12 - username não pode ser de outro usuário
        if (usuarioRepository.existeUsername(dto.getUsername(), id)) {
            throw new RegraNegocioException("Username já cadastrado");
        }

        u.setNmCompleto(dto.getNmCompleto());
        u.setUsername(dto.getUsername());
        u.setSenha(dto.getSenha());
        // cpf, email, dtNascimento, tipoPerfil, sdPonto e nvUsuario continuam os do banco

        usuarioRepository.atualizar(u);
        return paraResponse(u);
    }

    // DELETE
    public void deletar(int id) {
        Usuario u = buscarUsuarioOuErro(id);

        removerPerfil(id, u.getTipoPerfil()); // primeiro a linha "filha" de criador/moderador

        try {
            usuarioRepository.deletar(id);
        } catch (RuntimeException e) {
            if (violouFk(e)) {
                criarPerfil(id, u.getTipoPerfil()); // devolve a linha de criador/moderador que foi apagada
                throw new RegraNegocioException("Usuário possui registros vinculados e não pode ser excluído");
            }
            throw e;
        }
    }

    // LOGIN
    public DTOUsuarioResponse login(DTOLoginRequest dto) {
        Optional<Usuario> resultado = usuarioRepository.buscarPorUsername(dto.getUsername());
        if (resultado.isEmpty()) {
            throw new CredenciaisInvalidasException("Usuário ou senha inválidos");
        }

        Usuario u = resultado.get();
        if (!u.getSenha().equals(dto.getSenha())) {
            throw new CredenciaisInvalidasException("Usuário ou senha inválidos");
        }
        return paraResponse(u); // front guarda id e tipoPerfil
    }

    // TROCA DE PERFIL (ex.: depois do questionário de moderador)
    public DTOUsuarioResponse trocarPerfil(int id, DTOPerfilRequest dto) {
        Usuario u = buscarUsuarioOuErro(id);

        String perfilAntigo = u.getTipoPerfil();
        String perfilNovo = dto.getTipoPerfil();

        if (perfilAntigo.equals(perfilNovo)) {
            throw new RegraNegocioException("O usuário já possui esse perfil");
        }

        removerPerfil(id, perfilAntigo); // bloqueia se o criador tiver comunidades, por exemplo
        criarPerfil(id, perfilNovo);

        u.setTipoPerfil(perfilNovo);
        usuarioRepository.atualizar(u);
        return paraResponse(u);
    }

    // ===================== métodos auxiliares =====================

    private Usuario buscarUsuarioOuErro(int id) {
        Optional<Usuario> resultado = usuarioRepository.buscarPorId(id);
        if (resultado.isEmpty()) {
            throw new RecursoNaoEncontrado("Usuário não encontrado");
        }
        return resultado.get();
    }

    // copia os campos do DTO para o model (usado no cadastro e no update)
    private void preencherDados(Usuario u, DTOUsuarioRequest dto) {
        u.setNmCompleto(dto.getNmCompleto());
        u.setCpf(dto.getCpf());
        u.setEmail(dto.getEmail());
        u.setUsername(dto.getUsername());
        u.setSenha(dto.getSenha());
        u.setDtNascimento(dto.getDtNascimento());
    }

    // cria a linha em T_LGT_CRIADOR ou T_LGT_MODERADOR conforme o perfil
    private void criarPerfil(int idUsuario, String perfil) {
        if (perfil.equals("CRIADOR")) {
            Criador c = new Criador(0, NIVEL_INICIAL, ROTULO_INICIAL, LocalDate.now(), idUsuario);
            criadorRepository.inserir(c);
        } else if (perfil.equals("MODERADOR")) {
            Moderador m = new Moderador(0, NIVEL_INICIAL, ROTULO_INICIAL, LocalDate.now(), idUsuario);
            moderadorRepository.inserir(m);
        }
        // COMUM não tem tabela própria
    }

    // remove a linha de CRIADOR/MODERADOR; bloqueia se já tiver comunidades/moderações
    private void removerPerfil(int idUsuario, String perfil) {
        try {
            if (perfil.equals("CRIADOR")) {
                criadorRepository.deletarPorIdUsuario(idUsuario);
            } else if (perfil.equals("MODERADOR")) {
                moderadorRepository.deletarPorIdUsuario(idUsuario);
            }
        } catch (RuntimeException e) {
            if (violouFk(e)) {
                if (perfil.equals("CRIADOR")) {
                    throw new RegraNegocioException("Criador possui comunidades e não pode deixar de ser criador");
                }
                throw new RegraNegocioException("Moderador possui moderações registradas e não pode deixar de ser moderador");
            }
            throw e;
        }
    }

    // verifica se o erro veio de uma FK do Oracle (ORA-02292)
    private boolean violouFk(RuntimeException e) {
        return e.getCause() instanceof SQLIntegrityConstraintViolationException;
    }

    // RF12 – não permitir CPF, e-mail ou username já utilizados por outro usuário
    private void validarDuplicidade(DTOUsuarioRequest dto, int idIgnorar) {
        if (usuarioRepository.existeCpf(dto.getCpf(), idIgnorar)) {
            throw new RegraNegocioException("CPF já cadastrado");
        }
        if (usuarioRepository.existeEmail(dto.getEmail(), idIgnorar)) {
            throw new RegraNegocioException("E-mail já cadastrado");
        }
        if (usuarioRepository.existeUsername(dto.getUsername(), idIgnorar)) {
            throw new RegraNegocioException("Username já cadastrado");
        }
    }

    // Model -> DTO de resposta (sem senha e sem CPF)
    private DTOUsuarioResponse paraResponse(Usuario u) {
        return new DTOUsuarioResponse(
                u.getId(),
                u.getNmCompleto(),
                u.getEmail(),
                u.getUsername(),
                u.getDtNascimento(),
                u.getSdPonto(),
                u.getNvUsuario(),
                u.getTipoPerfil()
        );
    }
}