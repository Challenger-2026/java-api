package org.example.guara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// postagem na fila de moderação (às cegas: sem dados do autor)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOPostagemFilaResponse {
    private int id;          // o front mostra como "Postagem #id"
    private String ctTexto;
    private String ctUrl;
    private double ptGerado; // indica o impacto escolhido (10.6 / 6.6 / 1.6)
}