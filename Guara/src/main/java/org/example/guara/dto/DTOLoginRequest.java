package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Login do usuário
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOLoginRequest {
    @NotBlank(message = "O username é obrigatório")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}