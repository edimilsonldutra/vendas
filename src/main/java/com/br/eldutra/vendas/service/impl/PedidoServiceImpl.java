package com.br.eldutra.vendas.service.impl;

import com.br.eldutra.vendas.domain.entity.Cliente;
import com.br.eldutra.vendas.domain.entity.ItemPedido;
import com.br.eldutra.vendas.domain.entity.Pedido;
import com.br.eldutra.vendas.domain.entity.Produto;
import com.br.eldutra.vendas.domain.enums.StatusPedido;
import com.br.eldutra.vendas.domain.repository.ClienteRepository;
import com.br.eldutra.vendas.domain.repository.ItemPedidoRepository;
import com.br.eldutra.vendas.domain.repository.PedidoRepository;
import com.br.eldutra.vendas.domain.repository.ProdutoRepository;
import com.br.eldutra.vendas.exception.PedidoNaoEncontradoException;
import com.br.eldutra.vendas.exception.RegraNegocioException;
import com.br.eldutra.vendas.rest.dto.ItemPedidoDTO;
import com.br.eldutra.vendas.rest.dto.PedidoDTO;
import com.br.eldutra.vendas.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ClienteRepository clienteRepository;

    private final ProdutoRepository produtoRepository;

    private final ItemPedidoRepository itemPedidoRepository;
    @Override
    public Pedido salvar(PedidoDTO pedidoDTO) {
        Integer idCliente = pedidoDTO.getCliente();
        Cliente cliente = clienteRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Códido de cliente inválido."));
        Pedido pedido = new Pedido();
        pedido.setTotal(pedidoDTO.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatusPedido(StatusPedido.REALIZADO);

        List<ItemPedido> itemPedidos = converterItems(pedido, pedidoDTO.getItems());
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(itemPedidos);
        pedido.setItemPedidos(itemPedidos);
        return pedido;
    }

    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items.isEmpty()) {
            throw new RegraNegocioException("Não é possivel realizar um pedido sem items.");
        }

        return items
                .stream()
                .map(itemPedidoDTO -> {
                    Integer idProduto = itemPedidoDTO.getProduto();
                    Produto produto = produtoRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto invalido: "+ idProduto
                                    ));
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(itemPedidoDTO.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidoRepository.findByIdFetchItens(id);
    }

    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidoRepository
                .findById(id)
                .map(pedido -> {
                    pedido.setStatusPedido(statusPedido);
                    return pedidoRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }
}
