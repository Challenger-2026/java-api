package org.example.guara.controller;

import jakarta.validation.Valid;
import org.example.guara.dto.DTOPostagemRequest;
import org.example.guara.dto.DTOPostagemResponse;
import org.example.guara.service.PostagemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// recebe as requisições HTTP da postagem e repassa para o service
@RestController
@RequestMapping("/postagens")
public class PostagemController {
    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    // GET /postagens
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DTOPostagemResponse> listarTodas() {
        return postagemService.listarTodas();
    }

    // GET /postagens/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOPostagemResponse buscarPorId(@PathVariable int id) {
        return postagemService.buscarPorId(id);
    }

    // POST /postagens
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public DTOPostagemResponse publicar(@Valid @RequestBody DTOPostagemRequest dto) {
        return postagemService.publicar(dto);
    }
}