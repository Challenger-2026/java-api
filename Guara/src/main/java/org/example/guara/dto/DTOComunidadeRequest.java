package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que chega no POST/PUT de comunidade
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOComunidadeRequest {

    @NotBlank(message = "O nome da comunidade é obrigatório")
    @Size(max = 150, message = "O nome pode ter no máximo 150 caracteres")
    private String nmComunidade;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @Positive(message = "O id do usuário criador é obrigatório")
    private int idUsuario; // quem está fazendo a requisição (deve ser CRIADOR)
}