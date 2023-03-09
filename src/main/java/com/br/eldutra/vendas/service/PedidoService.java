package com.br.eldutra.vendas.service;

import com.br.eldutra.vendas.domain.entity.Pedido;
import com.br.eldutra.vendas.domain.enums.StatusPedido;
import com.br.eldutra.vendas.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {
    Pedido salvar(PedidoDTO pedidoDTO);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizaStatus(Integer id, StatusPedido statusPedido);
}
