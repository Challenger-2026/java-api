package org.example.guara.service;

import org.example.guara.dto.DTOPostagemRequest;
import org.example.guara.dto.DTOPostagemResponse;
import org.example.guara.exception.RecursoNaoEncontrado;
import org.example.guara.exception.RegraNegocioException;
import org.example.guara.model.Postagem;
import org.example.guara.repository.ComunidadeRepository;
import org.example.guara.repository.PostagemRepository;
import org.example.guara.repository.UsuarioComunidadeRepository;
import org.example.guara.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// regras de negócio da postagem
@Service
public class PostagemService {
    // pontos por impacto, na proporção do trabalho (10 x o peso por dia)
    private static final double PONTOS_CRITICO = 10.6;
    private static final double PONTOS_ESTRUTURAL = 6.6;
    private static final double PONTOS_ROTINA = 1.6;

    private final PostagemRepository postagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComunidadeRepository comunidadeRepository;
    private final UsuarioComunidadeRepository usuarioComunidadeRepository;

    public PostagemService(PostagemRepository postagemRepository,
                           UsuarioRepository usuarioRepository,
                           ComunidadeRepository comunidadeRepository,
                           UsuarioComunidadeRepository usuarioComunidadeRepository) {
        this.postagemRepository = postagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.comunidadeRepository = comunidadeRepository;
        this.usuarioComunidadeRepository = usuarioComunidadeRepository;
    }

    // READ - todas
    public List<DTOPostagemResponse> listarTodas() {
        List<DTOPostagemResponse> resposta = new ArrayList<>();
        for (Postagem p : postagemRepository.buscarTodas()) {
            resposta.add(paraResponse(p));
        }
        return resposta;
    }

    // READ - por id
    public DTOPostagemResponse buscarPorId(int id) {
        Optional<Postagem> resultado = postagemRepository.buscarPorId(id);
        if (resultado.isEmpty()) {
            throw new RecursoNaoEncontrado("Postagem não encontrada");
        }
        return paraResponse(resultado.get());
    }

    // CREATE
    public DTOPostagemResponse publicar(DTOPostagemRequest dto) {
        if (usuarioRepository.buscarPorId(dto.getIdUsuario()).isEmpty()) {
            throw new RecursoNaoEncontrado("Usuário não encontrado");
        }
        if (comunidadeRepository.buscarPorId(dto.getIdComunidade()).isEmpty()) {
            throw new RecursoNaoEncontrado("Comunidade não encontrada");
        }
        if (!usuarioComunidadeRepository.ehMembro(dto.getIdUsuario(), dto.getIdComunidade())) {
            throw new RegraNegocioException("O usuário precisa ser membro da comunidade para publicar");
        }

        Postagem p = new Postagem();
        p.setCtTexto(dto.getCtTexto());
        p.setCtUrl(dto.getCtUrl());
        p.setStValidacao("PENDENTE");                    // toda postagem nasce pendente
        p.setPtGerado(calcularPontos(dto.getImpacto())); // pontos definidos pelo sistema
        p.setIdUsuario(dto.getIdUsuario());
        p.setIdComunidade(dto.getIdComunidade());

        postagemRepository.inserir(p); // aqui o id é gerado
        return paraResponse(p);
    }

    // ===================== métodos auxiliares =====================

    // converte o impacto escolhido em pontos
    private double calcularPontos(String impacto) {
        switch (impacto) {
            case "CRITICO":
                return PONTOS_CRITICO;
            case "ESTRUTURAL":
                return PONTOS_ESTRUTURAL;
            default: // ROTINA (o @Pattern do DTO já barra outros valores)
                return PONTOS_ROTINA;
        }
    }

    // Model -> DTO de resposta
    private DTOPostagemResponse paraResponse(Postagem p) {
        return new DTOPostagemResponse(
                p.getId(),
                p.getCtTexto(),
                p.getCtUrl(),
                p.getStValidacao(),
                p.getPtGerado(),
                p.getIdUsuario(),
                p.getIdComunidade()
        );
    }
}