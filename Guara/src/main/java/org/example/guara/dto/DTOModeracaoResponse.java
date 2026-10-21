package org.example.guara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que sai nas respostas de moderação
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOModeracaoResponse {
    private int id;
    private String decisao;       // A | R
    private String justificativa;
    private double bnModerador;
    private int idModerador;
    private int idPostagem;
}