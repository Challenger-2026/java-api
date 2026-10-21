package org.example.guara.controller;

import jakarta.validation.Valid;
import org.example.guara.dto.DTOComunidadeRequest;
import org.example.guara.dto.DTOComunidadeResponse;
import org.example.guara.service.ComunidadeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// recebe as requisições HTTP da comunidade e repassa para o service
@RestController
@RequestMapping("/comunidades")
public class ComunidadeController {
    private final ComunidadeService comunidadeService;

    public ComunidadeController(ComunidadeService comunidadeService) {
        this.comunidadeService = comunidadeService;
    }

    // GET /comunidades
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DTOComunidadeResponse> listarTodas() {
        return comunidadeService.listarTodas();
    }

    // GET /comunidades/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOComunidadeResponse buscarPorId(@PathVariable int id) {
        return comunidadeService.buscarPorId(id);
    }

    // POST /comunidades
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public DTOComunidadeResponse cadastrar(@Valid @RequestBody DTOComunidadeRequest dto) {
        return comunidadeService.cadastrar(dto);
    }

    // PUT /comunidades/{id}
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOComunidadeResponse atualizar(@PathVariable int id, @Valid @RequestBody DTOComunidadeRequest dto) {
        return comunidadeService.atualizar(id, dto);
    }

    // DELETE /comunidades/{id}?idUsuario=5
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deletar(@PathVariable int id, @RequestParam int idUsuario) {
        comunidadeService.deletar(id, idUsuario);
    }

    // POST /comunidades/{id}/membros?idUsuario=5 - entrar em uma comunidade
    @PostMapping("/{id}/membros")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public void entrar(@PathVariable int id, @RequestParam int idUsuario) {
        comunidadeService.entrar(id, idUsuario);
    }

    // DELETE /comunidades/{id}/membros?idUsuario=5 - sair de uma comunidade
    @DeleteMapping("/{id}/membros")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void sair(@PathVariable int id, @RequestParam int idUsuario) {
        comunidadeService.sair(id, idUsuario);
    }

    // GET /comunidades/{id}/membros/total
    @GetMapping("/{id}/membros/total")
    @ResponseStatus(HttpStatus.OK)
    public int contarMembros(@PathVariable int id) {
        return comunidadeService.contarMembros(id);
    }
}
