package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// T_LGT_POSTAGEM (o moderador só altera st_validacao)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postagem {
    private int id;              // id_postagem
    private String ctTexto;      // ct_texto (CLOB)
    private String ctUrl;        // ct_url (CLOB)
    private String stValidacao;  // st_validacao: PENDENTE | APROVADO | REJEITADO
    private double ptGerado;     // pt_gerado (>= 0)
    private int idUsuario;       // FK autor da postagem
    private int idComunidade;    // FK comunidade
}