package org.example.guara.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// o que chega no PUT de usuário (só o que o protótipo deixa editar)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOUsuarioUpdateRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome pode ter no máximo 150 caracteres")
    private String nmCompleto;

    @NotBlank(message = "O username é obrigatório")
    @Size(max = 50, message = "O username pode ter no máximo 50 caracteres")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    @Size(max = 150, message = "A senha pode ter no máximo 150 caracteres")
    private String senha;
}