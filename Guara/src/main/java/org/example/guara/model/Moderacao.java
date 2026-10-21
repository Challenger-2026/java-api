package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// T_LGT_MODERACAO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moderacao {
    private int id;               // id_moderacao
    private String decisao;       // decisao CHAR(1): 'A' aprovar | 'R' reprovar
    private String justificativa; // justificativa (CLOB)
    private double bnModerador;   // bn_moderador
    private int idModerador;      // FK T_LGT_MODERADOR_id_moderador
    private int idPostagem;       // FK T_LGT_POSTAGEM_id_postagem (UNIQUE)
}