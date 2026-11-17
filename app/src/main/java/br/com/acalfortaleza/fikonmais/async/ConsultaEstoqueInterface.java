package br.com.acalfortaleza.fikonmais.async;

import java.util.List;

import br.com.acalfortaleza.fikonmais.models.EstoqueValidacao;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;

public interface ConsultaEstoqueInterface {

     void depoisDaConsulta(List<ItemPedidos> retorno);
}
