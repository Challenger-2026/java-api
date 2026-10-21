package org.example.guara.controller;

import jakarta.validation.Valid;
import org.example.guara.dto.*;
import org.example.guara.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// recebe as requisições HTTP do usuário e repassa para o service
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /usuarios
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DTOUsuarioResponse> listarTodos() {
        return usuarioService.listarTodos();
    }

    // GET /usuarios/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOUsuarioResponse buscarPorId(@PathVariable int id) {
        return usuarioService.buscarPorId(id);
    }

    // POST /usuarios
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public DTOUsuarioResponse cadastrar(@Valid @RequestBody DTOUsuarioRequest dto) {
        return usuarioService.cadastrar(dto);
    }

    // PUT /usuarios/{id}
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTOUsuarioResponse atualizar(@PathVariable int id, @Valid @RequestBody DTOUsuarioUpdateRequest dto) {
        return usuarioService.atualizar(id, dto);
    }

    // DELETE /usuarios/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 - deu certo, sem corpo na resposta
    public void deletar(@PathVariable int id) {
        usuarioService.deletar(id);
    }

    // POST /usuarios/login - login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public DTOUsuarioResponse login(@Valid @RequestBody DTOLoginRequest dto) {
        return usuarioService.login(dto);
    }

    // PUT /usuarios/{id}/perfil - trocar o perfil
    @PutMapping("/{id}/perfil")
    @ResponseStatus(HttpStatus.OK)
    public DTOUsuarioResponse trocarPerfil(@PathVariable int id, @Valid @RequestBody DTOPerfilRequest dto) {
        return usuarioService.trocarPerfil(id, dto);
    }
}
