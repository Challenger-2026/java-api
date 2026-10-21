package org.example.guara.exception;

// usuário existe, mas não tem permissão para a ação
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}