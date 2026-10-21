package org.example.guara.service;

import org.example.guara.exception.AcessoNegadoException;
import org.example.guara.repository.UsuarioComunidadeRepository;
import org.example.guara.repository.UsuarioRepository;
import org.example.guara.dto.DTOComunidadeRequest;
import org.example.guara.dto.DTOComunidadeResponse;
import org.example.guara.exception.RecursoNaoEncontrado;
import org.example.guara.exception.RegraNegocioException;
import org.example.guara.model.Comunidade;
import org.example.guara.model.Criador;
import org.example.guara.repository.ComunidadeRepository;
import org.example.guara.repository.CriadorRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// regras de negócio da comunidade
@Service
public class ComunidadeService {
    private final ComunidadeRepository comunidadeRepository;
    private final CriadorRepository criadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioComunidadeRepository usuarioComunidadeRepository;

    public ComunidadeService(ComunidadeRepository comunidadeRepository,
                             CriadorRepository criadorRepository,
                             UsuarioRepository usuarioRepository,
                             UsuarioComunidadeRepository usuarioComunidadeRepository) {
        this.comunidadeRepository = comunidadeRepository;
        this.criadorRepository = criadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioComunidadeRepository = usuarioComunidadeRepository;
    }

    // READ - todas
    public List<DTOComunidadeResponse> listarTodas() {
        List<DTOComunidadeResponse> resposta = new ArrayList<>();
        for (Comunidade c : comunidadeRepository.buscarTodas()) {
            resposta.add(paraResponse(c));
        }
        return resposta;
    }

    // READ - por id
    public DTOComunidadeResponse buscarPorId(int id) {
        return paraResponse(buscarComunidadeOuErro(id));
    }

    // CREATE
    public DTOComunidadeResponse cadastrar(DTOComunidadeRequest dto) {
        Criador criador = buscarCriadorOuErro(dto.getIdUsuario()); // só CRIADOR pode criar

        Comunidade c = new Comunidade();
        c.setNmComunidade(dto.getNmComunidade());
        c.setDescricao(dto.getDescricao());
        c.setDtCriacao(LocalDate.now()); // data definida pelo sistema
        c.setIdCriador(criador.getId());
        c.setIdRanking(null);            // nasce sem ranking

        comunidadeRepository.inserir(c); // aqui o id é gerado
        return paraResponse(c);
    }

    // UPDATE
    public DTOComunidadeResponse atualizar(int id, DTOComunidadeRequest dto) {
        Comunidade c = buscarComunidadeOuErro(id);
        validarDono(c, dto.getIdUsuario());

        c.setNmComunidade(dto.getNmComunidade());
        c.setDescricao(dto.getDescricao());
        // dtCriacao, idCriador e idRanking continuam os do banco

        comunidadeRepository.atualizar(c);
        return paraResponse(c);
    }

    // DELETE
    public void deletar(int id, int idUsuario) {
        Comunidade c = buscarComunidadeOuErro(id);
        validarDono(c, idUsuario);

        try {
            comunidadeRepository.deletar(id);
        } catch (RuntimeException e) {
            if (violouFk(e)) {
                throw new RegraNegocioException("Comunidade possui membros ou postagens e não pode ser excluída");
            }
            throw e;
        }
    }

    // ENTRAR na comunidade
    public void entrar(int idComunidade, int idUsuario) {
        buscarComunidadeOuErro(idComunidade);

        if (usuarioRepository.buscarPorId(idUsuario).isEmpty()) {
            throw new RecursoNaoEncontrado("Usuário não encontrado");
        }
        if (usuarioComunidadeRepository.ehMembro(idUsuario, idComunidade)) {
            throw new RegraNegocioException("O usuário já é membro desta comunidade");
        }

        usuarioComunidadeRepository.inserir(idUsuario, idComunidade);
    }

    // SAIR da comunidade
    public void sair(int idComunidade, int idUsuario) {
        buscarComunidadeOuErro(idComunidade);

        if (!usuarioComunidadeRepository.ehMembro(idUsuario, idComunidade)) {
            throw new RegraNegocioException("O usuário não é membro desta comunidade");
        }

        usuarioComunidadeRepository.deletar(idUsuario, idComunidade);
    }

    // quantidade de membros (para o front mostrar na página da comunidade)
    public int contarMembros(int idComunidade) {
        buscarComunidadeOuErro(idComunidade);
        return usuarioComunidadeRepository.contarMembros(idComunidade);
    }

    // ===================== métodos auxiliares =====================

    private Comunidade buscarComunidadeOuErro(int id) {
        Optional<Comunidade> resultado = comunidadeRepository.buscarPorId(id);
        if (resultado.isEmpty()) {
            throw new RecursoNaoEncontrado("Comunidade não encontrada");
        }
        return resultado.get();
    }

    // se o usuário não tem linha em T_LGT_CRIADOR, ele não é criador
    private Criador buscarCriadorOuErro(int idUsuario) {
        Optional<Criador> resultado = criadorRepository.buscarPorIdUsuario(idUsuario);
        if (resultado.isEmpty()) {
            throw new AcessoNegadoException("Apenas usuários com perfil CRIADOR podem gerenciar comunidades");
        }
        return resultado.get();
    }

    // confere se quem está alterando/excluindo é o criador da comunidade
    private void validarDono(Comunidade c, int idUsuario) {
        Criador criador = buscarCriadorOuErro(idUsuario);
        if (criador.getId() != c.getIdCriador()) {
            throw new AcessoNegadoException("Apenas o criador da comunidade pode alterá-la ou excluí-la");
        }
    }

    // verifica se o erro veio de uma FK do Oracle (ORA-02292)
    private boolean violouFk(RuntimeException e) {
        return e.getCause() instanceof SQLIntegrityConstraintViolationException;
    }

    // Model -> DTO de resposta
    private DTOComunidadeResponse paraResponse(Comunidade c) {
        return new DTOComunidadeResponse(
                c.getId(),
                c.getNmComunidade(),
                c.getDescricao(),
                c.getDtCriacao(),
                c.getIdCriador(),
                c.getIdRanking()
        );
    }
}