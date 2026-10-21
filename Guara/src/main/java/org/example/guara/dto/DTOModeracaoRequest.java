package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que chega no POST de moderação
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOModeracaoRequest {

    @NotBlank(message = "A decisão é obrigatória")
    @Pattern(regexp = "A|R", message = "Decisão deve ser A (aprovar) ou R (reprovar)")
    private String decisao;

    @NotBlank(message = "A justificativa é obrigatória")
    private String justificativa;

    @Positive(message = "O id do usuário moderador é obrigatório")
    private int idUsuario; // moderador logado (o service converte em idModerador)

    @Positive(message = "O id da postagem é obrigatório")
    private int idPostagem;
}