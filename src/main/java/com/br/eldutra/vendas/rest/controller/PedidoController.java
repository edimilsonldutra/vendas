package com.br.eldutra.vendas.rest.controller;

import com.br.eldutra.vendas.domain.entity.ItemPedido;
import com.br.eldutra.vendas.domain.entity.Pedido;
import com.br.eldutra.vendas.domain.enums.StatusPedido;
import com.br.eldutra.vendas.rest.dto.AtualizacaoStatusPedidoDTO;
import com.br.eldutra.vendas.rest.dto.InformacaoItemPedidoDTO;
import com.br.eldutra.vendas.rest.dto.InformacaoPedidoDTO;
import com.br.eldutra.vendas.rest.dto.PedidoDTO;
import com.br.eldutra.vendas.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {


    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) { this.pedidoService = pedidoService; }

    @PostMapping
    @ResponseStatus(CREATED)
    public Integer save(@RequestBody @Valid PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoService.salvar(pedidoDTO);
        return pedido.getId();
    }
    @GetMapping("{id}")
    public InformacaoPedidoDTO getById(@PathVariable Integer id) {
        return pedidoService
                .obterPedidoCompleto(id)
                .map(pedidoCompleto -> converter(pedidoCompleto))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
    }

    @PatchMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void updateStatus(@PathVariable Integer id,
                             @RequestBody AtualizacaoStatusPedidoDTO atualizacaoStatusPedidoDTO) {
        String novoStatus = atualizacaoStatusPedidoDTO.getNovoStatus();
        pedidoService.atualizaStatus(id, StatusPedido.valueOf(novoStatus));
    }

    private InformacaoPedidoDTO converter(Pedido pedido) {
        return InformacaoPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatusPedido().name())
                .itemPedidoDTOS(converter(pedido.getItemPedidos()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itemPedidos) {
        if (CollectionUtils.isEmpty(itemPedidos)) {
            return Collections.emptyList();
        }
        return itemPedidos.stream().map(
                itemPedido -> InformacaoItemPedidoDTO
                        .builder().descricaoProduto(itemPedido.getProduto().getDescricao())
                        .precoUnitario(itemPedido.getProduto().getPreco())
                        .quantidade(itemPedido.getQuantidade())
                        .build()
        ).collect(Collectors.toList());
    }
}
