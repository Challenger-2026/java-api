package org.example.guara.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// o que chega no POST/PUT (JSON de entrada)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DTOUsuarioRequest {

    @NotBlank(message = "O nome completo é obrigatório")
    @Size(max = 150, message = "O nome pode ter no máximo 150 caracteres")
    private String nmCompleto;

    @NotBlank(message = "O CPF é obrigatório")
    @Size(max = 14, message = "O CPF pode ter no máximo 14 caracteres")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "O e-mail pode ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "O username é obrigatório")
    @Size(max = 50, message = "O username pode ter no máximo 50 caracteres")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    @Size(max = 150, message = "A senha pode ter no máximo 150 caracteres")
    private String senha;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dtNascimento;

}
