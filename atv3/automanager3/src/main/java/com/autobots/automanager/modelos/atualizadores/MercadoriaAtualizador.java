package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class MercadoriaAtualizador {
	
private StringVerificadorNulo verificador = new StringVerificadorNulo();
	
	public void atualizar(Mercadoria mercadoria, Mercadoria atualizacao) {
		if (atualizacao != null) {
			if(!verificador.verificar(atualizacao.getValidade())) {
				mercadoria.setValidade(atualizacao.getValidade());;
			}
			
			
			if(!verificador.verificar(atualizacao.getFabricao())) {
				mercadoria.setFabricao(atualizacao.getFabricao());;
			}
			
			if(!verificador.verificar(atualizacao.getCadastro())) {
				mercadoria.setCadastro(atualizacao.getCadastro());;
			}
			
			if(!verificador.verificar(atualizacao.getNome())) {
				mercadoria.setNome(atualizacao.getNome());;
			}
			
			if(!verificador.verificar(atualizacao.getQuantidade())) {
				mercadoria.setQuantidade(atualizacao.getQuantidade());;
			}
			
			if(!verificador.verificar(atualizacao.getValor())) {
				mercadoria.setValor(atualizacao.getValor());;
			}
			
			if(!verificador.verificar(atualizacao.getDescricao())) {
				mercadoria.setDescricao(atualizacao.getDescricao());;
			}
			
		}
		
	}
}
