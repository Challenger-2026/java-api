package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// T_LGT_CRIADOR
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Criador {
    private int id;              // id_criador
    private int nvCriador;       // nv_criador (>= 1)
    private String rtCriador;    // rt_criador
    private LocalDate dtAdesao;  // dt_adesao
    private int idUsuario;       // FK T_LGT_USUARIO_id_usuario (UNIQUE)
}
