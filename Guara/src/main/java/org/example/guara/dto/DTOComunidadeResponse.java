package org.example.guara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// o que sai nas respostas de comunidade
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOComunidadeResponse {
    private int id;
    private String nmComunidade;
    private String descricao;
    private LocalDate dtCriacao;
    private int idCriador;
    private Integer idRanking; // pode vir null enquanto a comunidade não tiver ranking (criação inicial de uma comunidade)
}
