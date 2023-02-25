package com.br.eldutra.vendas.domain.repository;

import com.br.eldutra.vendas.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto,Integer> {

}
