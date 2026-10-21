package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private int id;                 // id_usuario
    private String nmCompleto;      // nm_completo
    private String cpf;             // cpf (UNIQUE)
    private String email;           // email (UNIQUE)
    private String username;        // username (UNIQUE)
    private String senha;           // senha -> não devolver no DTO de resposta
    private LocalDate dtNascimento; // dt_nascimento (DATE)
    private double sdPonto;         // sd_ponto (>= 0)
    private int nvUsuario;          // nv_usuario (>= 1)
    private String tipoPerfil;      // tipo_perfil: COMUM | CRIADOR | MODERADOR
}
