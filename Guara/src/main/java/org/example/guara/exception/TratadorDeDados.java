package org.example.guara.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// onde trata as exceções de qualquer classe
@RestControllerAdvice
public class TratadorDeDados {

    // 404 - não encontrado
    @ExceptionHandler(RecursoNaoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse lidadorComError(RecursoNaoEncontrado ex){
        return new ErroResponse(ex.getMessage(), 404);
    }

    // 400 - JSON não passou nas validações do DTO (@NotBlank, @Size...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse lidadorComValidacao(MethodArgumentNotValidException ex){
        String mensagem = "Dados inválidos";
        FieldError erro = ex.getBindingResult().getFieldError();
        if(erro != null){
            mensagem = erro.getDefaultMessage();
        }
        return new ErroResponse(mensagem, 400);
    }

    // 409 - regra de negócio
    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse lidadorComRegraNegocio(RegraNegocioException ex){
        return new ErroResponse(ex.getMessage(), 409);
    }
    // 401 - login inválido
    @ExceptionHandler(CredenciaisInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErroResponse lidadorComLogin(CredenciaisInvalidasException ex){
        return new ErroResponse(ex.getMessage(), 401);
    }
    // 403 - sem permissão
    @ExceptionHandler(AcessoNegadoException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResponse lidadorComAcesso(AcessoNegadoException ex){
        return new ErroResponse(ex.getMessage(), 403);
    }
}