package org.example.guara.exception;

// erro de regra de negócio (dado duplicado, operação não permitida...)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
