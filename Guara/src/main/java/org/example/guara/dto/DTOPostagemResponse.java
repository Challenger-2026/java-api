package org.example.guara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que sai nas respostas de postagem
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOPostagemResponse {
    private int id;
    private String ctTexto;
    private String ctUrl;
    private String stValidacao; // PENDENTE | APROVADO | REJEITADO
    private double ptGerado;
    private int idUsuario;
    private int idComunidade;
}