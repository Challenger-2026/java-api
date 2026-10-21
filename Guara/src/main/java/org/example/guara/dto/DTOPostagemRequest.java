package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que chega no POST de postagem
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOPostagemRequest {

    @NotBlank(message = "O texto da postagem é obrigatório")
    private String ctTexto;

    @NotBlank(message = "A URL da evidência é obrigatória")
    private String ctUrl;

    @NotBlank(message = "O impacto é obrigatório")
    @Pattern(regexp = "CRITICO|ESTRUTURAL|ROTINA", message = "Impacto deve ser CRITICO, ESTRUTURAL ou ROTINA")
    private String impacto;

    @Positive(message = "O id do usuário é obrigatório")
    private int idUsuario; // autor (vem do usuário logado no front)

    @Positive(message = "O id da comunidade é obrigatório")
    private int idComunidade;
}