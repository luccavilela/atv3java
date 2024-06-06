package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class ServicoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	
	public void atualizar(Servico servico, Servico atualizacao) {
		if (atualizacao != null) {
			if(!verificador.verificar(atualizacao.getNome())) {
				servico.setNome(atualizacao.getNome());;
			}
			
			if(!verificador.verificar(atualizacao.getValor())) {
				servico.setValor(atualizacao.getValor());
			}
			
			if(!verificador.verificar(atualizacao.getDescricao())) {
				servico.setDescricao(atualizacao.getDescricao());;
			}
			
		}
		
	}

}
