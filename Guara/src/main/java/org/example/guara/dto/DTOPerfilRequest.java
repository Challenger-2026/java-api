package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que chega na troca de perfil
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOPerfilRequest {
    @NotBlank(message = "O tipo de perfil é obrigatório")
    @Pattern(regexp = "COMUM|CRIADOR|MODERADOR", message = "Tipo de perfil deve ser COMUM, CRIADOR ou MODERADOR")
    private String tipoPerfil;
}