package com.br.eldutra.vendas.exception;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException() { super("Pedido não encontrado.");}
}
