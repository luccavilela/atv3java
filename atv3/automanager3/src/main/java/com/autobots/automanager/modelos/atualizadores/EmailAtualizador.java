package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class EmailAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	
	public void atualizar(Email email, Email atualizacao) {
		if (atualizacao != null) {
			if(!verificador.verificar(atualizacao.getEndereco())) {
				email.setEndereco(atualizacao.getEndereco());
			}
		}
		
	}

}
