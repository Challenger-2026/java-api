package org.example.guara.service;

import org.example.guara.dto.DTOModeracaoRequest;
import org.example.guara.dto.DTOModeracaoResponse;
import org.example.guara.dto.DTOPostagemFilaResponse;
import org.example.guara.exception.AcessoNegadoException;
import org.example.guara.exception.RecursoNaoEncontrado;
import org.example.guara.exception.RegraNegocioException;
import org.example.guara.model.Moderacao;
import org.example.guara.model.Moderador;
import org.example.guara.model.Postagem;
import org.example.guara.repository.ModeracaoRepository;
import org.example.guara.repository.ModeradorRepository;
import org.example.guara.repository.PostagemRepository;
import org.example.guara.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// regras de negócio da moderação
@Service
public class ModeracaoService {
    // bônus do moderador = 10% dos pontos da postagem (1.06 / 0.66 / 0.16)
    private static final double PERCENTUAL_BONUS = 0.10;

    private final ModeracaoRepository moderacaoRepository;
    private final ModeradorRepository moderadorRepository;
    private final PostagemRepository postagemRepository;
    private final UsuarioRepository usuarioRepository;

    public ModeracaoService(ModeracaoRepository moderacaoRepository,
                            ModeradorRepository moderadorRepository,
                            PostagemRepository postagemRepository,
                            UsuarioRepository usuarioRepository) {
        this.moderacaoRepository = moderacaoRepository;
        this.moderadorRepository = moderadorRepository;
        this.postagemRepository = postagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // FILA - postagens pendentes, às cegas (sem dados do autor)
    public List<DTOPostagemFilaResponse> listarFila() {
        List<DTOPostagemFilaResponse> fila = new ArrayList<>();
        for (Postagem p : postagemRepository.buscarPorStatus("PENDENTE")) {
            fila.add(new DTOPostagemFilaResponse(p.getId(), p.getCtTexto(), p.getCtUrl(), p.getPtGerado()));
        }
        return fila;
    }

    // READ - todas
    public List<DTOModeracaoResponse> listarTodas() {
        List<DTOModeracaoResponse> resposta = new ArrayList<>();
        for (Moderacao m : moderacaoRepository.buscarTodas()) {
            resposta.add(paraResponse(m));
        }
        return resposta;
    }

    // READ - por id
    public DTOModeracaoResponse buscarPorId(int id) {
        Optional<Moderacao> resultado = moderacaoRepository.buscarPorId(id);
        if (resultado.isEmpty()) {
            throw new RecursoNaoEncontrado("Moderação não encontrada");
        }
        return paraResponse(resultado.get());
    }

    // CREATE - moderar uma postagem
    public DTOModeracaoResponse moderar(DTOModeracaoRequest dto) {
        Moderador moderador = buscarModeradorOuErro(dto.getIdUsuario());
        Postagem postagem = buscarPostagemOuErro(dto.getIdPostagem());

        // só postagens pendentes, e cada uma só uma vez (UNIQUE)
        if (!postagem.getStValidacao().equals("PENDENTE") || moderacaoRepository.existePorPostagem(postagem.getId())) {
            throw new RegraNegocioException("Esta postagem já foi moderada");
        }

        // moderador não avalia a própria postagem
        if (postagem.getIdUsuario() == dto.getIdUsuario()) {
            throw new RegraNegocioException("O moderador não pode avaliar a própria postagem");
        }

        Moderacao m = new Moderacao();
        m.setDecisao(dto.getDecisao());
        m.setJustificativa(dto.getJustificativa());
        m.setBnModerador(calcularBonus(postagem.getPtGerado()));
        m.setIdModerador(moderador.getId());
        m.setIdPostagem(postagem.getId());
        moderacaoRepository.inserir(m); // aqui o id é gerado

        if (dto.getDecisao().equals("A")) {
            postagemRepository.atualizarStatus(postagem.getId(), "APROVADO");
            usuarioRepository.somarPontos(postagem.getIdUsuario(), postagem.getPtGerado()); // RN10
        } else {
            postagemRepository.atualizarStatus(postagem.getId(), "REJEITADO");
        }

        return paraResponse(m);
    }

    // ===================== métodos auxiliares =====================

    // se o usuário não tem linha em T_LGT_MODERADOR, ele não é moderador
    private Moderador buscarModeradorOuErro(int idUsuario) {
        Optional<Moderador> resultado = moderadorRepository.buscarPorIdUsuario(idUsuario);
        if (resultado.isEmpty()) {
            throw new AcessoNegadoException("Apenas usuários com perfil MODERADOR podem moderar postagens");
        }
        return resultado.get();
    }

    private Postagem buscarPostagemOuErro(int id) {
        Optional<Postagem> resultado = postagemRepository.buscarPorId(id);
        if (resultado.isEmpty()) {
            throw new RecursoNaoEncontrado("Postagem não encontrada");
        }
        return resultado.get();
    }

    // 10% dos pontos, arredondado em 2 casas
    private double calcularBonus(double ptGerado) {
        return Math.round(ptGerado * PERCENTUAL_BONUS * 100) / 100.0;
    }

    // Model -> DTO de resposta
    private DTOModeracaoResponse paraResponse(Moderacao m) {
        return new DTOModeracaoResponse(
                m.getId(),
                m.getDecisao(),
                m.getJustificativa(),
                m.getBnModerador(),
                m.getIdModerador(),
                m.getIdPostagem()
        );
    }
}