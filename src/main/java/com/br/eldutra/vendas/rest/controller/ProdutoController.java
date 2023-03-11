package com.br.eldutra.vendas.rest.controller;

import com.br.eldutra.vendas.domain.entity.Produto;
import com.br.eldutra.vendas.domain.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Produto save(@RequestBody @Valid Produto produto) {
        return produtoRepository.save(produto);
    }

    public void update(@PathVariable Integer id,
                       @RequestBody @Valid Produto produto) {
        produtoRepository
                .findById(id)
                .map(buscaProduto -> {
                    produto.setId(buscaProduto.getId());
                    produtoRepository.save(produto);
                    return produto;
                }).orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND,
                                "Produto não encontrado."));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        produtoRepository
                .findById(id)
                .map(buscaProduto -> {
                    produtoRepository.delete(buscaProduto);
                    return Void.TYPE;
                }).orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND,
                                "Produto não encontrado."));
    }
    @GetMapping("{id}")
    public Produto getById(@PathVariable Integer id) {
        return produtoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND,
                                "Produto não encontrado."));
    }
    @GetMapping
    public List<Produto> find(Produto filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);
        Example example = Example.of(filtro, matcher);
        return produtoRepository.findAll(example);
    }
}
