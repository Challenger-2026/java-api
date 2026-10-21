package org.example.guara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// o que sai nas respostas (sem dados sensíveis)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOUsuarioResponse {
    private int id;
    private String nmCompleto;
    private String email;
    private String username;
    private LocalDate dtNascimento;
    private double sdPonto;
    private int nvUsuario;
    private String tipoPerfil;
}
