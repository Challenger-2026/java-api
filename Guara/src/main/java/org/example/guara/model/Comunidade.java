package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// T_LGT_COMUNIDADE
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comunidade {
    private int id;               // id_comunidade
    private String nmComunidade;  // nm_comunidade
    private String descricao;     // descricao (CLOB -> String)
    private LocalDate dtCriacao;  // dt_criacao
    private int idCriador;        // FK T_LGT_CRIADOR_id_criador
    private Integer idRanking;    // FK T_LGT_RANKING_id_ranking (opcional, pode ser null)
}
