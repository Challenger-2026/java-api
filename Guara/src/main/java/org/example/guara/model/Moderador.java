package org.example.guara.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// T_LGT_MODERADOR
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moderador {
    private int id;              // id_moderador
    private int nvModerador;     // nv_moderador (>= 1)
    private String rtModerador;  // rt_moderador
    private LocalDate dtAdesao;  // dt_adesao
    private int idUsuario;       // FK T_LGT_USUARIO_id_usuario (UNIQUE)
}