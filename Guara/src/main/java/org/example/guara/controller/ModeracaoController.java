package org.example.guara.controller;

import jakarta.validation.Valid;
import org.example.guara.dto.DTOModeracaoRequest;
import org.example.guara.dto.DTOModeracaoResponse;
import org.example.guara.dto.DTOPostagemFilaResponse;
import org.example.guara.service.ModeracaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// recebe as requisições HTTP da moderação e repassa para o service
@RestController
@RequestMapping("/moderacoes")
public class ModeracaoController {
    private final ModeracaoService moderacaoService;

    public ModeracaoController(ModeracaoService moderacaoService) {
        this.moderacaoService = moderacaoService;
    }

    // GET /moderacoes/fila - postagens pendentes, sem o autor (correção as cegas)
    @GetMapping("/fila")
    @ResponseStatus(HttpStatus.OK)
    public List<DTOPostagemFilaResponse> listarFila() {
        return moderacaoService.listarFila();
    }

    // GET /moderacoes
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DTOModeracaoResponse> listarTodas() {
        return moderacaoService.listarTodas();
    }

    // GET /moderacoes/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOModeracaoResponse buscarPorId(@PathVariable int id) {
        return moderacaoService.buscarPorId(id);
    }

    // POST /moderacoes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public DTOModeracaoResponse moderar(@Valid @RequestBody DTOModeracaoRequest dto) {
        return moderacaoService.moderar(dto);
    }
}